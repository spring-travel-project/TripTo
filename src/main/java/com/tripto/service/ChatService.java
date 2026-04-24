package com.tripto.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.ChatDAO;
import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;

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
}