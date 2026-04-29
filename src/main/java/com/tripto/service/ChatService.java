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

    // 🌟 기존 메서드를 지우고 이 코드로 덮어씌우세요.
    public ChatMessageDTO saveSocketMessage(int roomId, int seqMember, String message, Integer seqFile) {

        if ((message == null || message.trim().isEmpty()) && seqFile == null) {
            return null;
        }

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSeqMember(seqMember);
        dto.setSeqChattingroom(roomId);
        dto.setSeqFile(seqFile);

        // 1. 공백/NULL 에러 방지 (기존 로직 유지)
        if ((message == null || message.trim().isEmpty()) && seqFile != null) {
            dto.setDetail(" ");
        } else {
            dto.setDetail(message.trim());
        }

        // 🌟 [추가] 실시간 전송을 위한 안 읽은 숫자 계산
        // 1:1 채팅이면 보통 2명이라 2-1 = 1이 찍힙니다.
        int totalCount = chatDAO.getRoomMemberCount(roomId);
        dto.setUnreadCount(totalCount - 1);

        // 2. DB에 메시지 저장
        int result = chatDAO.insertMessage(dto);

        if (result != 1) {
            return null;
        }

        // 3. 브라우저로 돌려줄 응답 데이터 구성
        ChatMessageDTO saved = new ChatMessageDTO();
        saved.setSeq(dto.getSeq());
        saved.setNickname(chatDAO.getNicknameByMemberId(seqMember));
        saved.setDetail(dto.getDetail());
        saved.setSeqFile(seqFile);
        saved.setSeqMember(seqMember);
        saved.setSeqChattingroom(roomId);
        saved.setUnreadCount(dto.getUnreadCount()); // 🌟 계산된 숫자를 응답 DTO에 세팅!

        // 시간 정보 추가 (브라우저에서 바로 띄워주기 위함)
        saved.setMessageTime(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));

        if (seqFile != null && seqFile > 0) {
            saved.setSavedName(chatDAO.getFileNameBySeq(seqFile));
        }

        return saved; // 이 데이터가 WebSocket을 타고 JSP의 appendMessage로 갑니다!
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
        
        String message =
        		"<div class='chat-system-msg'>" +
    		        "<div>🧾 <b>[투표]</b> 새로운 투표가 등록되었습니다.</div>" +
    		        "<a href='/TripTo/chat/poll/detail?roomId=" 
    		            + dto.getSeqChattingroom() 
    		            + "&pollId=" + dto.getSeq() + "'>" +
    		            dto.getPollTitle() +
    		        "</a>" +
    		    "</div>";

        insertMessage(dto.getSeqChattingroom(), dto.getSeqMember(), message);

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

        int routineResult = chatDAO.insertRoutine(dto);

        if (routineResult != 1) {
        	return false;
        }
        
        String message =
        		"<div class='chat-system-msg'>" +
    		        "<div>📅 <b>[일정]</b> 새로운 일정이 등록되었습니다.</div>" +
    		        "<a href='/TripTo/chat/routine/detail?roomId=" 
    		            + dto.getSeqChattingroom() 
    		            + "&routineId=" + dto.getSeq() + "'>" +
    		            dto.getTitle() +
    		        "</a>" +
    		    "</div>";

        insertMessage(dto.getSeqChattingroom(), dto.getSeqMember(), message);

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

    public List<FileDTO> getRoutineFileList(int routineId) {
        return chatDAO.getRoutineFileList(routineId);
    }
    
    public int uploadCloudinaryFile(String originalName, String imageUrl, int memberSeq, int roomId) {
        
        // 1. DB에 저장하기 위한 맵 생성
        Map<String, Object> map = new HashMap<>();
        map.put("orgName", originalName);
        map.put("savedName", imageUrl); // 🌟 파일명 대신 'URL 주소'를 통째로 넣습니다.
        map.put("filePath", "cloudinary"); // 경로는 구분하기 쉽게 클라우디너리로 표시
        
        // 2. chat.xml의 insertChatFile 호출 (이건 기존 쿼리 그대로 써도 됩니다)
        chatDAO.insertChatFile(map);
        
        // 3. 방금 생성된 파일 번호(seqFile) 반환
        return Integer.parseInt(map.get("seqFile").toString());
    }
    
    public ChatRoomDTO getRoomById(int roomId, int seqMember) {
        return chatDAO.getRoomById(roomId, seqMember);
    }
    
    public List<RoutineDTO> getRoutineLocationListByTravelPost(int seqTravelPost) {
        return chatDAO.getRoutineLocationListByTravelPost(seqTravelPost);
    }
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
    
    
}