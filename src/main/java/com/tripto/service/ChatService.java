package com.tripto.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tripto.dao.ChatDAO;
import com.tripto.dto.ChatMemberDTO;
import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.dto.FileDTO;
import com.tripto.dto.PollContentDTO;
import com.tripto.dto.PollDTO;
import com.tripto.dto.RoutineDTO;
import com.tripto.dto.TravelPostDTO;

@Service
public class ChatService {

    @Autowired
    private ChatDAO chatDAO;

    public List<ChatRoomDTO> getRoomList(int seqMember, Integer selectedRoomId, Integer category) {
        Map<String, Object> map = new HashMap<>();
        map.put("seqMember", seqMember);
        map.put("category", category);

        List<ChatRoomDTO> roomList = chatDAO.getRoomList(map);

        if (roomList != null && !roomList.isEmpty()) {
            int targetRoomId = selectedRoomId == null ? roomList.get(0).getRoomId() : selectedRoomId;
            for (ChatRoomDTO dto : roomList) {
                dto.setActive(dto.getRoomId() == targetRoomId ? 1 : 0);
            }
        }
        return roomList;
    }

    public List<ChatMessageDTO> getMessageList(int roomId, int loginUserId) {
        Map<String, Integer> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("loginUserId", loginUserId);

        List<ChatMessageDTO> messageList = chatDAO.getMessageList(map);
        if (messageList != null) {
            for (ChatMessageDTO dto : messageList) {
                dto.setMine(dto.getSeqMember() == loginUserId);
            }
        }
        return messageList;
    }

    public boolean insertMessage(int roomId, int loginUserId, String detail) {
        if (detail == null || detail.trim().isEmpty()) {
            return false;
        }
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSeqMember(loginUserId);
        dto.setSeqChattingroom(roomId);
        dto.setDetail(detail.trim());
        return chatDAO.insertMessage(dto) == 1;
    }

    public ChatMessageDTO saveSocketMessage(int roomId, int seqMember, String message, Integer seqFile) {
        if ((message == null || message.trim().isEmpty()) && seqFile == null) {
            return null;
        }

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSeqMember(seqMember);
        dto.setSeqChattingroom(roomId);
        dto.setSeqFile(seqFile);

        if ((message == null || message.trim().isEmpty()) && seqFile != null) {
            dto.setDetail(" ");
        } else {
            dto.setDetail(message.trim());
        }

        int totalCount = chatDAO.getRoomMemberCount(roomId);
        dto.setUnreadCount(totalCount - 1);

        if (chatDAO.insertMessage(dto) != 1) {
            return null;
        }

        ChatMessageDTO saved = new ChatMessageDTO();
        saved.setSeq(dto.getSeq());
        saved.setNickname(chatDAO.getNicknameByMemberId(seqMember));
        saved.setDetail(dto.getDetail());
        saved.setSeqFile(seqFile);
        saved.setSeqMember(seqMember);
        saved.setSeqChattingroom(roomId);
        saved.setUnreadCount(dto.getUnreadCount());
        saved.setMessageTime(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));

        if (seqFile != null && seqFile > 0) {
            saved.setSavedName(chatDAO.getFileNameBySeq(seqFile));
        }
        return saved;
    }

    public ChatMessageDTO saveSocketMessage(int roomId, int seqMember, String message) {
        return saveSocketMessage(roomId, seqMember, message, null);
    }

    public String getNicknameByMemberId(int seqMember) {
        return chatDAO.getNicknameByMemberId(seqMember);
    }

    public boolean exitRoom(int roomId, int userId) {
        return chatDAO.exitRoom(roomId, userId) == 1;
    }

    public List<RoutineDTO> getRoutineList(int roomId) {
        chatDAO.updateExpiredRoutineStatus();
        return chatDAO.getRoutineList(roomId);
    }

    public List<PollDTO> getPollList(int roomId) {
        return chatDAO.getPollList(roomId);
    }

    public boolean insertPoll(PollDTO dto, List<String> pollContents) {
        if (dto.getPollTitle() == null || dto.getPollTitle().trim().isEmpty()) return false;
        if (dto.getPollEnddateInput() == null || dto.getPollEnddateInput().trim().isEmpty()) return false;
        if (dto.getPolldetail() == null || dto.getPolldetail().trim().isEmpty()) return false;
        if (pollContents == null || pollContents.size() < 2) return false;

        dto.setPollTitle(dto.getPollTitle().trim());
        dto.setPolldetail(dto.getPolldetail().trim());

        if (chatDAO.insertPoll(dto) != 1) return false;

        for (String content : pollContents) {
            if (content != null && !content.trim().isEmpty()) {
                Map<String, Object> map = new HashMap<>();
                map.put("seqPoll", dto.getSeq());
                map.put("pollContent", content.trim());
                chatDAO.insertPollContent(map);
            }
        }
        return true;
    }

    public PollDTO getPollDetail(int pollId) {
        return chatDAO.getPollDetail(pollId);
    }

    public List<PollContentDTO> getPollContentList(int pollId) {
        return chatDAO.getPollContentList(pollId);
    }

    public boolean votePoll(int pollId, int pollContentId, int seqMember) {
        chatDAO.deletePreviousVote(pollId, seqMember);
        return chatDAO.votePoll(pollContentId, seqMember) == 1;
    }

    public boolean deletePoll(int pollId, int loginUserId) {
        PollDTO poll = chatDAO.getPollDetail(pollId);
        if (poll == null || poll.getSeqMember() != loginUserId) return false;

        chatDAO.deletePollResultByPollId(pollId);
        chatDAO.deletePollContentByPollId(pollId);
        return chatDAO.deletePoll(pollId) == 1;
    }

    public boolean insertRoutine(RoutineDTO dto, List<MultipartFile> files) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) return false;
        if (dto.getDetail() == null || dto.getDetail().trim().isEmpty()) return false;

        Integer seqTravelPost = chatDAO.getTravelPostSeqByRoomId(dto.getSeqChattingroom());
        dto.setSeqTravelPost(seqTravelPost != null ? seqTravelPost : 0);
        dto.setTitle(dto.getTitle().trim());
        dto.setDetail(dto.getDetail().trim());

        try {
            if (dto.getdDayInput() != null && !dto.getdDayInput().trim().isEmpty()) {
                dto.setdDay(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.getdDayInput()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (dto.getPlaceName() != null && !dto.getPlaceName().trim().isEmpty()) {
            chatDAO.insertLocation(dto);
        }

        if (chatDAO.insertRoutine(dto) != 1) return false;

        if (files == null || files.isEmpty()) return true;

        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            		"cloud_name", "df2o0mjgj",
                    "api_key", "154321363337894",
                    "api_secret", "Z3WzpCWRQ4tBgwXQ-J1lYZc44XU"
            ));

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                String originalName = file.getOriginalFilename();

                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "tripto/routine",
                                "resource_type", "auto"
                        )
                );

                String fileUrl = uploadResult.get("secure_url").toString();

                FileDTO fileDTO = new FileDTO();
                fileDTO.setOriginalName(originalName);
                fileDTO.setSavedName(originalName);
                fileDTO.setFilePath(fileUrl);
                fileDTO.setFileSize(file.getSize());
                fileDTO.setFileType(file.getContentType());

                chatDAO.insertFile(fileDTO);

                Map<String, Object> map = new HashMap<>();
                map.put("seqFile", fileDTO.getSeq());
                map.put("roomId", dto.getSeqChattingroom());
                map.put("seqRoutine", dto.getSeq());

                chatDAO.insertRoutineFile(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public RoutineDTO getRoutineDetail(int routineId) {
        chatDAO.updateExpiredRoutineStatus();
        return chatDAO.getRoutineDetail(routineId);
    }

    public int createOrGetMatchingChatRoom(int me, int target) {
        Map<String, Integer> map = new HashMap<>();
        map.put("me", me);
        map.put("target", target);

        Integer existingRoomId = chatDAO.findMatchingRoom(map);
        if (existingRoomId != null) return existingRoomId;

        ChatRoomDTO newRoom = new ChatRoomDTO();
        newRoom.setCategory(1);
        chatDAO.createChattingRoom(newRoom);
        int newRoomId = newRoom.getRoomId();

        Map<String, Integer> userMap1 = new HashMap<>();
        userMap1.put("roomId", newRoomId);
        userMap1.put("userId", me);
        chatDAO.insertUserChat(userMap1);

        Map<String, Integer> userMap2 = new HashMap<>();
        userMap2.put("roomId", newRoomId);
        userMap2.put("userId", target);
        chatDAO.insertUserChat(userMap2);

        return newRoomId;
    }

    public boolean deleteRoutine(int routineId, int loginUserId) {
        RoutineDTO routine = chatDAO.getRoutineDetail(routineId);
        if (routine == null || routine.getSeqMember() != loginUserId) return false;
        return chatDAO.deleteRoutine(routineId) == 1;
    }

    public boolean updateRoutine(RoutineDTO dto, int loginUserId, List<MultipartFile> files, String removedFiles) {
        RoutineDTO origin = chatDAO.getRoutineDetail(dto.getSeq());

        if (origin == null || origin.getSeqMember() != loginUserId) return false;
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) return false;
        if (dto.getDetail() == null || dto.getDetail().trim().isEmpty()) return false;

        dto.setTitle(dto.getTitle().trim());
        dto.setDetail(dto.getDetail().trim());

        try {
            if (dto.getdDayInput() != null && !dto.getdDayInput().trim().isEmpty()) {
                dto.setdDay(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.getdDayInput()));
            } else {
                dto.setdDay(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (dto.getPlaceName() != null && !dto.getPlaceName().trim().isEmpty()) {
            chatDAO.insertLocation(dto);
        } else {
            dto.setSeqLocation(origin.getSeqLocation());
        }

        if (chatDAO.updateRoutine(dto) != 1) {
            return false;
        }

        // 기존 파일 삭제 처리
        if (removedFiles != null && !removedFiles.trim().isEmpty()) {
            String[] fileIds = removedFiles.split(",");

            for (String fileId : fileIds) {
                if (fileId == null || fileId.trim().isEmpty()) continue;

                int seqFile = Integer.parseInt(fileId.trim());

                chatDAO.deleteRoutineFile(seqFile);
                chatDAO.deleteFile(seqFile);
            }
        }

        // 새 파일 추가 처리
        if (files != null && !files.isEmpty()) {
            String uploadPath = "C:/upload/";
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                try {
                    String originalName = file.getOriginalFilename();
                    String savedName = UUID.randomUUID().toString() + "_" + originalName;

                    file.transferTo(new File(uploadPath + savedName));

                    FileDTO fileDTO = new FileDTO();
                    fileDTO.setOriginalName(originalName);
                    fileDTO.setSavedName(savedName);
                    fileDTO.setFilePath("/upload/" + savedName);
                    fileDTO.setFileSize(file.getSize());
                    fileDTO.setFileType(file.getContentType());

                    chatDAO.insertFile(fileDTO);

                    Map<String, Object> map = new HashMap<>();
                    map.put("seqFile", fileDTO.getSeq());
                    map.put("roomId", dto.getSeqChattingroom());
                    map.put("seqRoutine", dto.getSeq());

                    chatDAO.insertRoutineFile(map);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public int uploadCloudinaryFile(String originalName, String imageUrl, int memberSeq, int roomId) {
        Map<String, Object> map = new HashMap<>();
        map.put("orgName", originalName);
        map.put("savedName", imageUrl);
        map.put("filePath", "cloudinary");
        chatDAO.insertChatFile(map);
        return Integer.parseInt(map.get("seqFile").toString());
    }

    public void updateReadStatus(int roomId, int loginUserId) {
        chatDAO.insertReadStatus(roomId, loginUserId);
    }

    public List<FileDTO> getRoutineFileList(int routineId) {
        return chatDAO.getRoutineFileList(routineId);
    }
    
    public ChatRoomDTO getRoomById(int roomId, int seqMember) {
        return chatDAO.getRoomById(roomId, seqMember);
    }

    @Transactional
    public int updateJoinRequest(Map<String, Object> map) {
        return chatDAO.updateJoinRequest(map);
    }

    public int getRoomAuth(int roomId, int seqMember) {
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("seqMember", seqMember);
        return chatDAO.getRoomAuth(map);
    }

    public Integer findTravelRoom(int seqTravelPost) {
        return chatDAO.findTravelRoom(seqTravelPost);
    }

    public TravelPostDTO getTravelPostForChat(int seqTravelPost) {
        return chatDAO.getTravelPostForChat(seqTravelPost);
    }

    public int insertUserChatIfNotExists(Map<String, Integer> map) {
        return chatDAO.insertUserChatIfNotExists(map);
    }

    public void updateSystemMessage(int msgSeq, String finalMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgSeq", msgSeq);
        map.put("finalMsg", finalMsg);
        chatDAO.updateSystemMessage(map);
    }

    public String getPostStatusByRoomId(int roomId) {
        return chatDAO.getPostStatusByRoomId(roomId);
    }

    public List<ChatMemberDTO> getChatRoomMembers(int roomId) {
        return chatDAO.getChatRoomMembers(roomId);
    }

    public List<RoutineDTO> getRoutineLocationListByTravelPost(int seqTravelPost) {
        return chatDAO.getRoutineLocationListByTravelPost(seqTravelPost);
    }

    // 🌟 [통합 완료] 동행 게시글 전용 채팅방 생성 및 자동 입장
    public int createOrGetTravelChatRoom(int seqTravelPost, int loginUserId) {

        Integer roomId = chatDAO.findTravelRoom(seqTravelPost);

        if (roomId != null) {
            Map<String, Integer> map = new HashMap<>();
            map.put("roomId", roomId);
            map.put("userId", loginUserId);
            chatDAO.insertUserChatIfNotExists(map);
            return roomId;
        }

        TravelPostDTO post = chatDAO.getTravelPostForChat(seqTravelPost);
        ChatRoomDTO room = new ChatRoomDTO();
        room.setRoomName(post.getTitle());
        room.setCategory(0);
        room.setSeqTravelPost(seqTravelPost);

        chatDAO.createTravelChatRoom(room);
        int newRoomId = room.getRoomId();

        Map<String, Integer> writerMap = new HashMap<>();
        writerMap.put("roomId", newRoomId);
        writerMap.put("userId", post.getSeqMember());
        chatDAO.insertUserChatIfNotExists(writerMap);

        Map<String, Integer> loginMap = new HashMap<>();
        loginMap.put("roomId", newRoomId);
        loginMap.put("userId", loginUserId);
        chatDAO.insertUserChatIfNotExists(loginMap);

        return newRoomId;
    }

    // 🌟 [통합 완료] 채팅방 멤버 여부 확인 (boolean 반환)
    public boolean isRoomMember(int roomId, int seqMember) {
        return chatDAO.isRoomMember(roomId, seqMember) > 0;
    }
}