package com.tripto.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.ChatMemberDTO;
import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.dto.FileDTO;
import com.tripto.dto.PollContentDTO;
import com.tripto.dto.PollDTO;
import com.tripto.dto.RoutineDTO;
import com.tripto.dto.TravelPostDTO;

@Repository
public class ChatDAO {

    @Autowired
    private SqlSessionTemplate template;

    public List<ChatRoomDTO> getRoomList(Map<String, Object> map) {
        return template.selectList("chat.getRoomList", map);
    }

    public List<ChatMessageDTO> getMessageList(Map<String, Integer> map) {
        return template.selectList("chat.getMessageList", map);
    }

    public int insertMessage(ChatMessageDTO dto) {
        return template.insert("chat.insertMessage", dto);
    }
    
    public String getNicknameByMemberId(int seqMember) {
        return template.selectOne("chat.getNicknameByMemberId", seqMember);
    }
    
    public int exitRoom(int roomId, int userId) {

        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("userId", userId);

        return template.update("chat.exitRoom", map);
    }
    
    public List<RoutineDTO> getRoutineList(int roomId) {
        return template.selectList("chat.getRoutineList", roomId);
    }

    public List<PollDTO> getPollList(int roomId) {
        return template.selectList("chat.getPollList", roomId);
    }
    
    public int insertPoll(PollDTO dto) {
        return template.insert("chat.insertPoll", dto);
    }
    
    public int insertPollContent(Map<String, Object> map) {
        return template.insert("chat.insertPollContent", map);
    }
    
    public PollDTO getPollDetail(int pollId) {
        return template.selectOne("chat.getPollDetail", pollId);
    }

    public List<PollContentDTO> getPollContentList(int pollId) {
        return template.selectList("chat.getPollContentList", pollId);
    }
    
    public int deletePreviousVote(int pollId, int seqMember) {
        Map<String, Object> map = new HashMap<>();
        map.put("pollId", pollId);
        map.put("seqMember", seqMember);

        return template.delete("chat.deletePreviousVote", map);
    }

    public int votePoll(int pollContentId, int seqMember) {
        Map<String, Object> map = new HashMap<>();
        map.put("pollContentId", pollContentId);
        map.put("seqMember", seqMember);

        return template.insert("chat.votePoll", map);
    }
    
    public int deletePollResultByPollId(int pollId) {
        return template.delete("chat.deletePollResultByPollId", pollId);
    }

    public int deletePollContentByPollId(int pollId) {
        return template.delete("chat.deletePollContentByPollId", pollId);
    }

    public int deletePoll(int pollId) {
        return template.delete("chat.deletePoll", pollId);
    }
    
    public Integer getTravelPostSeqByRoomId(int roomId) {
        return template.selectOne("chat.getTravelPostSeqByRoomId", roomId);
    }

    public int insertRoutine(RoutineDTO dto) {
        return template.insert("chat.insertRoutine", dto);
    }
    
    public RoutineDTO getRoutineDetail(int routineId) {
        return template.selectOne("chat.getRoutineDetail", routineId);
    }


    // 1. 기존 매칭 채팅방 번호 찾기 (없으면 null 반환)
    public Integer findMatchingRoom(Map<String, Integer> map) {
        // sqlSession 대신 template 사용
        return template.selectOne("chat.findMatchingRoom", map); 
    }

    // 2. 새 채팅방 만들기 (INSERT)
    public void createChattingRoom(ChatRoomDTO newRoom) {
        template.insert("chat.createChattingRoom", newRoom);
    }

    // 3. 만들어진 방에 유저 참여시키기 (INSERT)
    public void insertUserChat(Map<String, Integer> map) {
        template.insert("chat.insertUserChat", map);
    }
    
    public int deleteRoutine(int routineId) {
        return template.delete("chat.deleteRoutine", routineId);
    }
    
    public int updateRoutine(RoutineDTO dto) {
        return template.update("chat.updateRoutine", dto);
    }
    
    public int insertLocation(RoutineDTO dto) {
        return template.insert("chat.insertLocation", dto);
    }
    
    public String getFileNameBySeq(int seqFile) {
        return template.selectOne("chat.getFileNameBySeq", seqFile);
    }

	public int insertChatFile(Map<String, Object> map) {
	    return template.insert("chat.insertChatFile", map);
	}

    public int updateExpiredRoutineStatus() {
        return template.update("chat.updateExpiredRoutineStatus");
    }
    

    public void insertReadStatus(int roomId, int loginUserId) {
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("loginUserId", loginUserId);
        
        template.insert("chat.insertReadStatus", map);
    }

    public int getRoomMemberCount(int roomId) {
        // user_chat 테이블에서 해당 방에 참여 중인 인원수를 가져옵니다.
        return template.selectOne("chat.getRoomMemberCount", roomId);
    }

    public int insertFile(FileDTO dto) {
        return template.insert("chat.insertFile", dto);
    }

    public int insertRoutineFile(Map<String, Object> map) {
        return template.insert("chat.insertRoutineFile", map);
    }

    public List<FileDTO> getRoutineFileList(int routineId) {
        return template.selectList("chat.getRoutineFileList", routineId);
    }
    
    public ChatRoomDTO getRoomById(int roomId, int seqMember) {

        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("seqMember", seqMember);

        return template.selectOne("chat.getRoomById", map);
    }
    
    public List<RoutineDTO> getRoutineLocationListByTravelPost(int seqTravelPost) {
        return template.selectList("chat.getRoutineLocationListByTravelPost", seqTravelPost);
    }
        
    public Integer findTravelRoom(int seqTravelPost) {
        return template.selectOne("chat.findTravelRoom", seqTravelPost);
    }

    public TravelPostDTO getTravelPostForChat(int seqTravelPost) {
        return template.selectOne("chat.getTravelPostForChat", seqTravelPost);
    }

    public void createTravelChatRoom(ChatRoomDTO dto) {
        template.insert("chat.createTravelChatRoom", dto);
    }

    public int insertUserChatIfNotExists(Map<String, Integer> map) {
        return template.insert("chat.insertUserChatIfNotExists", map);
    }
    
    // 🌟 참여 신청 승인/거절 처리 (기존 sql -> template으로 수정)
    public int updateJoinRequest(Map<String, Object> map) {
        return template.update("chat.updateJoinRequest", map);
    }

    // 🌟 특정 방에서의 내 권한(방장 여부) 가져오기 (기존 sql -> template으로 수정)
    public int getRoomAuth(Map<String, Object> map) {
        return template.selectOne("chat.getRoomAuth", map);
    }
    
 // 🌟 시스템 메시지 업데이트
    public void updateSystemMessage(Map<String, Object> map) {
        template.update("chat.updateSystemMessage", map);
    }
    
    public String getPostStatusByRoomId(int roomId) {
        return template.selectOne("chat.getPostStatusByRoomId", roomId);
    }
    
    public List<ChatMemberDTO> getChatRoomMembers(int roomId) {
        return template.selectList("chat.getChatRoomMembers", roomId);
    }
    
    public int deleteRoutineFile(int seqFile) {
        return template.delete("chat.deleteRoutineFile", seqFile);
    }

    public int deleteFile(int seqFile) {
        return template.delete("chat.deleteFile", seqFile);
    }
    
}