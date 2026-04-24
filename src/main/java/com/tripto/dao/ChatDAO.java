package com.tripto.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.dto.RoutineDTO;
import com.tripto.dto.PollDTO;

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
    
}