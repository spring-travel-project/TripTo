package com.tripto.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripto.dao.ChatDAO;
import com.tripto.dao.LocationDAO;
import com.tripto.dao.TravelPostDAO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.dto.LocationDTO;
import com.tripto.dto.TravelPostDTO;
import com.tripto.dto.TravelPostFileDTO;

@Service
public class TravelPostService {

    @Autowired
    private TravelPostDAO dao;

    @Autowired
    private LocationDAO locationDAO;

    @Autowired
    private ChatDAO chatDAO;
    
    public int getTotalCount(TravelPostDTO dto) {
        return dao.getTotalCount(dto);
    }

    public List<TravelPostDTO> list(TravelPostDTO dto) {

        List<TravelPostDTO> list = dao.list(dto);

        for (TravelPostDTO post : list) {
            post.setThumbnailUrl(extractFirstImageSrc(post.getContent()));
        }

        return list;
    }

    private String extractFirstImageSrc(String content) {

        if (content == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private String getUploadPath(HttpServletRequest req) {
        return req.getServletContext().getRealPath("/resources/upload/travel");
    }

    @Transactional // 🚨 필수! 하나라도 에러 나면 전체 롤백
    public int add(TravelPostDTO dto, HttpServletRequest req) {

        // 1. 게시글 등록 (이때 dto에 seqTravelPost 값이 채워짐)
        int result = dao.add(dto);

        // 2. 위치 등록
        if (dto.getPlaceName() != null && !dto.getPlaceName().trim().isEmpty()
                && dto.getLatitude() != null && dto.getLongitude() != null) {

            LocationDTO location = new LocationDTO();
            location.setSeqTravelPost(dto.getSeqTravelPost());
            location.setPlaceName(dto.getPlaceName());
            location.setAddress(dto.getAddress());
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            location.setMapProviderId(dto.getMapProviderId());

            locationDAO.add(location);
        }

        // 3. 파일 등록
        if (dto.getFileList() != null && !dto.getFileList().isEmpty()) {

            for (TravelPostFileDTO fileDto : dto.getFileList()) {
                fileDto.setSeqTravelPost(dto.getSeqTravelPost());
                dao.addFile(fileDto);
            }
        }

        // 🌟 4. [추가] 동행 채팅방 자동 생성 및 방장 입장
        if (result > 0) {
            // 4-1. 채팅방 껍데기 생성
            ChatRoomDTO roomDto = new ChatRoomDTO();
            roomDto.setRoomName(dto.getTitle()); // 글 제목을 그대로 채팅방 이름으로
            roomDto.setCategory(0); // 0: 동행 카테고리
            roomDto.setSeqTravelPost(dto.getSeqTravelPost()); // 방금 등록한 게시글 번호
            
            chatDAO.createTravelChatRoom(roomDto); // 방 생성 (이때 roomDto에 roomId가 담김)

            // 4-2. 방장(게시글 작성자) 입장 처리
            Map<String, Integer> chatMap = new HashMap<>();
            chatMap.put("roomId", roomDto.getRoomId());
            chatMap.put("userId", dto.getSeqMember());
            
            // (참고: chat.xml의 insertUserChat은 기본적으로 auth=0, isActive=1 로 꽂히게 되어 있어!)
            chatDAO.insertUserChat(chatMap);
        }

        return result;
    }

    public TravelPostDTO get(int seqTravelPost, boolean increaseViewCount) {

        if (increaseViewCount) {
            dao.increaseViewCount(seqTravelPost);
        }

        TravelPostDTO dto = dao.get(seqTravelPost);

        if (dto != null) {
            dto.setFileList(dao.fileList(seqTravelPost));
        }

        return dto;
    }

    public int edit(TravelPostDTO dto, HttpServletRequest req) {

        int result = dao.edit(dto);

        if (result == 1) {
            try {
                dao.deleteFiles(dto.getSeqTravelPost());

                String uploadPath = getUploadPath(req);
                List<String> srcList = extractImageSrcList(dto.getContent());

                for (String src : srcList) {
                    String savedName = extractSavedName(src);

                    if (savedName == null || savedName.isEmpty()) {
                        continue;
                    }

                    File file = new File(uploadPath, savedName);

                    if (!file.exists()) {
                        continue;
                    }

                    TravelPostFileDTO fileDto = new TravelPostFileDTO();
                    fileDto.setSeqTravelPost(dto.getSeqTravelPost());
                    fileDto.setOriginalName(savedName);
                    fileDto.setSavedName(savedName);
                    fileDto.setFilePath("/resources/upload/travel");
                    fileDto.setFileType("image");
                    fileDto.setFileSize(file.length());

                    dao.addFile(fileDto);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public int delete(int seqTravelPost) {
        dao.deleteFiles(seqTravelPost);
        return dao.deletePost(seqTravelPost);
    }

    public boolean isWriter(int seqTravelPost, int seqMember) {
        Integer writerSeq = dao.getWriterSeq(seqTravelPost);
        return writerSeq != null && writerSeq == seqMember;
    }

    public List<LocationDTO> locationListByTravelPost(int seqTravelPost) {
        return dao.locationListByTravelPost(seqTravelPost);
    }

    private List<String> extractImageSrcList(String content) {
        List<String> srcList = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return srcList;
        }

        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            srcList.add(matcher.group(1));
        }

        return srcList;
    }

    private String extractSavedName(String src) {
        if (src == null || src.trim().isEmpty()) {
            return null;
        }

        int index = src.lastIndexOf("/");

        if (index == -1 || index == src.length() - 1) {
            return null;
        }

        return src.substring(index + 1);
    }
}