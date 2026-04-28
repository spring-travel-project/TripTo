package com.tripto.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tripto.dao.ChatDAO;
import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.dto.FileDTO;
import com.tripto.dto.PollContentDTO;
import com.tripto.dto.PollDTO;
import com.tripto.dto.RoutineDTO;

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
                if (dto.getRoomId() == targetRoomId) {
                    dto.setActive(1);
                } else {
                    dto.setActive(0);
                }
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

    public ChatMessageDTO saveSocketMessage(int roomId, int seqMember, String message) {

        if (message == null || message.trim().isEmpty()) {
            return null;
        }

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSeqMember(seqMember);
        dto.setSeqChattingroom(roomId);
        dto.setDetail(message.trim());

        int result = chatDAO.insertMessage(dto);

        if (result != 1) {
            return null;
        }

        ChatMessageDTO saved = new ChatMessageDTO();
        saved.setSeq(dto.getSeq());
        saved.setSeqMember(seqMember);
        saved.setSeqChattingroom(roomId);
        saved.setDetail(message.trim());
        saved.setNickname(chatDAO.getNicknameByMemberId(seqMember));
        saved.setMine(false);

        return saved;
    }

    public String getNicknameByMemberId(int seqMember) {
        return chatDAO.getNicknameByMemberId(seqMember);
    }

    public boolean exitRoom(int roomId, int userId) {
        return chatDAO.exitRoom(roomId, userId) == 1;
    }

    public List<RoutineDTO> getRoutineList(int roomId) {
        return chatDAO.getRoutineList(roomId);
    }

    public List<PollDTO> getPollList(int roomId) {
        return chatDAO.getPollList(roomId);
    }

    public boolean insertPoll(PollDTO dto, List<String> pollContents) {

        if (dto.getPollTitle() == null || dto.getPollTitle().trim().isEmpty()) {
            return false;
        }

        if (dto.getPollEnddateInput() == null || dto.getPollEnddateInput().trim().isEmpty()) {
            return false;
        }

        if (dto.getPolldetail() == null || dto.getPolldetail().trim().isEmpty()) {
            return false;
        }

        if (pollContents == null || pollContents.size() < 2) {
            return false;
        }

        dto.setPollTitle(dto.getPollTitle().trim());
        dto.setPolldetail(dto.getPolldetail().trim());

        int result = chatDAO.insertPoll(dto);

        if (result != 1) {
            return false;
        }

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

        // ���� ��ǥ ���� ����
        chatDAO.deletePreviousVote(pollId, seqMember);

        // �� �׸����� ��ǥ
        return chatDAO.votePoll(pollContentId, seqMember) == 1;
    }

    public boolean deletePoll(int pollId, int loginUserId) {

        // �ۼ��ڸ� ���� �����ϰ� �ϰ� ������ DAO���� �ۼ��� Ȯ��
        PollDTO poll = chatDAO.getPollDetail(pollId);

        if (poll == null) {
            return false;
        }

        if (poll.getSeqMember() != loginUserId) {
            return false;
        }

        chatDAO.deletePollResultByPollId(pollId);
        chatDAO.deletePollContentByPollId(pollId);
        return chatDAO.deletePoll(pollId) == 1;
    }

    public boolean insertRoutine(RoutineDTO dto) {
        return insertRoutine(dto, null);
    }

    public boolean insertRoutine(RoutineDTO dto, List<MultipartFile> files) {

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return false;
        }

        if (dto.getDetail() == null || dto.getDetail().trim().isEmpty()) {
            return false;
        }

        Integer seqTravelPost = chatDAO.getTravelPostSeqByRoomId(dto.getSeqChattingroom());

        if (seqTravelPost != null) {
            dto.setSeqTravelPost(seqTravelPost);
        } else {
            dto.setSeqTravelPost(0);
        }

        dto.setTitle(dto.getTitle().trim());
        dto.setDetail(dto.getDetail().trim());

        if (dto.getPlaceName() != null && !dto.getPlaceName().trim().isEmpty()) {
            chatDAO.insertLocation(dto);
        }

        int routineResult = chatDAO.insertRoutine(dto);

        if (routineResult != 1) {
            return false;
        }

        if (files == null || files.isEmpty()) {
            return true;
        }

        String uploadPath = "C:/upload/";

        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            try {
                String originalName = file.getOriginalFilename();
                String savedName = UUID.randomUUID().toString() + "_" + originalName;

                File dest = new File(uploadPath + savedName);
                file.transferTo(dest);

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

        return true;
    }

    public RoutineDTO getRoutineDetail(int routineId) {
        return chatDAO.getRoutineDetail(routineId);
    }

    // 🌟 매칭 채팅방 생성 또는 가져오기 로직
    public int createOrGetMatchingChatRoom(int me, int target) {

        Map<String, Integer> map = new HashMap<>();
        map.put("me", me);
        map.put("target", target);

        // 1. 이미 나와 상대방이 함께 있는 '매칭(category=1)' 방이 있는지 검사
        Integer existingRoomId = chatDAO.findMatchingRoom(map);

        if (existingRoomId != null) {
            return existingRoomId; // 방이 이미 있으면 기존 방 번호 리턴!
        }

        // 2. 방이 없다면 새로 생성 (chattingroom 테이블)
        ChatRoomDTO newRoom = new ChatRoomDTO();
        newRoom.setCategory(1); // 1 = 매칭 카테고리

        // DAO를 다녀오면 newRoom 객체 안에 새로 발급된 roomId(PK)가 채워집니다.
        chatDAO.createChattingRoom(newRoom);

        int newRoomId = newRoom.getRoomId();

        // 3. 나를 이 채팅방에 참여시킴 (user_chat 테이블)
        Map<String, Integer> userMap1 = new HashMap<>();
        userMap1.put("roomId", newRoomId);
        userMap1.put("userId", me);
        chatDAO.insertUserChat(userMap1);

        // 4. 상대방을 이 채팅방에 참여시킴 (user_chat 테이블)
        Map<String, Integer> userMap2 = new HashMap<>();
        userMap2.put("roomId", newRoomId);
        userMap2.put("userId", target);
        chatDAO.insertUserChat(userMap2);

        return newRoomId; // 🌟 최종적으로 새로 만들어진 방 번호를 리턴
    }

    public boolean deleteRoutine(int routineId, int loginUserId) {

        RoutineDTO routine = chatDAO.getRoutineDetail(routineId);

        if (routine == null) {
            return false;
        }

        // 작성자만 삭제 가능
        if (routine.getSeqMember() != loginUserId) {
            return false;
        }

        return chatDAO.deleteRoutine(routineId) == 1;
    }

    public boolean updateRoutine(RoutineDTO dto, int loginUserId) {

        RoutineDTO origin = chatDAO.getRoutineDetail(dto.getSeq());

        if (origin == null) {
            return false;
        }

        if (origin.getSeqMember() != loginUserId) {
            return false;
        }

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return false;
        }

        if (dto.getDetail() == null || dto.getDetail().trim().isEmpty()) {
            return false;
        }

        dto.setTitle(dto.getTitle().trim());
        dto.setDetail(dto.getDetail().trim());

        if (dto.getPlaceName() != null && !dto.getPlaceName().trim().isEmpty()) {
            chatDAO.insertLocation(dto);
        } else {
            dto.setSeqLocation(origin.getSeqLocation());
        }

        return chatDAO.updateRoutine(dto) == 1;
    }

    public List<FileDTO> getRoutineFileList(int routineId) {
        return chatDAO.getRoutineFileList(routineId);
    }
}