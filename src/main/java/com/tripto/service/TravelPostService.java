package com.tripto.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.LocationDAO;
import com.tripto.dao.TravelPostDAO;
import com.tripto.dto.LocationDTO;
import com.tripto.dto.TravelPostDTO;
import com.tripto.dto.TravelPostFileDTO;

@Service
public class TravelPostService {

    @Autowired
    private TravelPostDAO dao;

    @Autowired
    private LocationDAO locationDAO;

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

    public int add(TravelPostDTO dto, HttpServletRequest req) {

        int result = dao.add(dto);

        // 위치 등록
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

        // 파일 등록
        if (dto.getFileList() != null && !dto.getFileList().isEmpty()) {

            for (TravelPostFileDTO fileDto : dto.getFileList()) {
                fileDto.setSeqTravelPost(dto.getSeqTravelPost());
                dao.addFile(fileDto);
            }
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