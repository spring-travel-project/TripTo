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
import com.tripto.dto.PollContentDTO;
import com.tripto.dto.PollDTO;
import com.tripto.dto.RoutineDTO;
import java.text.SimpleDateFormat;

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
    
 // 🌟 기존 메서드를 지우고 이 코드로 덮어씌우세요.
    public ChatMessageDTO saveSocketMessage(int roomId, int seqMember, String message, Integer seqFile) {
        
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSeqMember(seqMember);
        dto.setSeqChattingroom(roomId);
        dto.setSeqFile(seqFile);

        // 1. 공백/NULL 에러 방지 (기존 로직 유지)
        if ((message == null || message.trim().isEmpty()) && seqFile != null) {
            dto.setDetail(" "); 
        } else {
            dto.setDetail(message);
        }

        // 🌟 [추가] 실시간 전송을 위한 안 읽은 숫자 계산
        // 1:1 채팅이면 보통 2명이라 2-1 = 1이 찍힙니다.
        int totalCount = chatDAO.getRoomMemberCount(roomId); 
        dto.setUnreadCount(totalCount - 1); 

        // 2. DB에 메시지 저장
        int result = chatDAO.insertMessage(dto);
        if (result != 1) return null;

        // 3. 브라우저로 돌려줄 응답 데이터 구성
        ChatMessageDTO saved = new ChatMessageDTO();
        saved.setNickname(chatDAO.getNicknameByMemberId(seqMember));
        saved.setDetail(dto.getDetail());
        saved.setSeqFile(seqFile);
        saved.setSeqMember(seqMember);
        saved.setUnreadCount(dto.getUnreadCount()); // 🌟 계산된 숫자를 응답 DTO에 세팅!
        
        // 시간 정보 추가 (브라우저에서 바로 띄워주기 위함)
        saved.setMessageTime(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));

        if (seqFile != null && seqFile > 0) {
            saved.setSavedName(chatDAO.getFileNameBySeq(seqFile));
        }
        
        return saved; // 이 데이터가 WebSocket을 타고 JSP의 appendMessage로 갑니다!
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

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return false;
        }

        if (dto.getDetail() == null || dto.getDetail().trim().isEmpty()) {
            return false;
        }

        int seqTravelPost = chatDAO.getTravelPostSeqByRoomId(dto.getSeqChattingroom());

        dto.setSeqTravelPost(seqTravelPost);
        dto.setTitle(dto.getTitle().trim());
        dto.setDetail(dto.getDetail().trim());

        // 종료 날짜 변환
        try {
            if (dto.getdDayInput() != null && !dto.getdDayInput().trim().isEmpty()) {
                dto.setdDay(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.getdDayInput()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 목적지 선택 시 location 저장
        if (dto.getPlaceName() != null && !dto.getPlaceName().trim().isEmpty()) {
            chatDAO.insertLocation(dto);
        }

        return chatDAO.insertRoutine(dto) == 1;
    }
    
    public RoutineDTO getRoutineDetail(int routineId) {
        chatDAO.updateExpiredRoutineStatus();
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

        return chatDAO.updateRoutine(dto) == 1;
    }
    

    public int uploadChatFile(MultipartFile file, int seqMember, int roomId) {
        // 🌟 1. 파일을 저장할 경로 (태훈님 설정에 맞게 수정하세요)
    	String uploadPath = "C:\\upload\\chat"; 
        
        // 폴더가 없으면 생성
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 🌟 2. 파일명 중복 방지를 위한 이름 변경 (UUID 사용)
            String orgName = file.getOriginalFilename();
            String extension = orgName.substring(orgName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString() + extension;

            // 🌟 3. 하드디스크에 파일 물리적 저장
            File target = new File(uploadPath, savedName);
            file.transferTo(target);

            // 🌟 4. DB(FILES 테이블)에 정보 insert를 위해 Map 생성
            Map<String, Object> map = new HashMap<>();
            map.put("orgName", orgName);
            map.put("savedName", savedName);
            map.put("filePath", "/upload/chat/" + savedName); // 웹에서 접근할 경로

            // chat.xml의 insertChatFile 호출 (selectKey로 인해 seqFile이 map에 담김)
            chatDAO.insertChatFile(map);

            // 🌟 5. 생성된 파일 번호(seqFile) 리턴
            return (int) map.get("seqFile");

        } catch (Exception e) {
            e.printStackTrace();
            return -1; // 실패 시 -1 리턴
        }
    }
    

    public void updateExpiredRoutineStatus() {
        chatDAO.updateExpiredRoutineStatus();
    }
    

    public void updateReadStatus(int roomId, int loginUserId) {
        chatDAO.insertReadStatus(roomId, loginUserId);
    }

    
    
}