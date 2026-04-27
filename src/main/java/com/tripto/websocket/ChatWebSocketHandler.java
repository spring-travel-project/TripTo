package com.tripto.websocket;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatSocketMessageDTO;
import com.tripto.service.ChatService;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // roomId �� ���� ���� ���
    private final Map<Integer, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // sessionId -> roomId
    private final Map<String, Integer> sessionRoomMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // ���Ḹ �Ϸ�. ���� room ����� ù ENTER �޽������� ó��
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {

        ChatSocketMessageDTO socketMessage = objectMapper.readValue(textMessage.getPayload(), ChatSocketMessageDTO.class);

        if (socketMessage.getType() == null || socketMessage.getRoomId() == null) {
            return;
        }

        if ("ENTER".equals(socketMessage.getType())) {
            int roomId = socketMessage.getRoomId();

            roomSessions.computeIfAbsent(roomId, key -> Collections.synchronizedSet(new HashSet<>())).add(session);
            sessionRoomMap.put(session.getId(), roomId);
            return;
        }

        if ("TALK".equals(socketMessage.getType())) {
            Integer roomId = socketMessage.getRoomId();
            Integer seqMember = socketMessage.getSeqMember();
            String message = socketMessage.getMessage();

            if (roomId == null || seqMember == null || message == null || message.trim().isEmpty()) {
                return;
            }

            ChatMessageDTO saved = chatService.saveSocketMessage(roomId, seqMember, message);

            if (saved == null) {
                return;
            }

            ChatSocketMessageDTO response = new ChatSocketMessageDTO();
            response.setType("TALK");
            response.setRoomId(roomId);
            response.setSeqMember(seqMember);
            response.setNickname(saved.getNickname());
            response.setMessage(saved.getDetail());
            response.setMessageTime(new SimpleDateFormat("HH:mm").format(new Date()));

            String json = objectMapper.writeValueAsString(response);
            broadcastToRoom(roomId, json);
        }
        if ("EXIT".equals(socketMessage.getType())) {

            Integer roomId = socketMessage.getRoomId();
            Integer seqMember = socketMessage.getSeqMember();

            if (roomId == null) return;

            ChatSocketMessageDTO response = new ChatSocketMessageDTO();
            response.setType("EXIT");
            response.setRoomId(roomId);
            response.setMessage("�� ���� ä�ù��� �������ϴ�.");

            String json = objectMapper.writeValueAsString(response);
            broadcastToRoom(roomId, json);
        }
    }

    private void broadcastToRoom(Integer roomId, String payload) throws Exception {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);

        if (sessions == null) {
            return;
        }

        synchronized (sessions) {
            Iterator<WebSocketSession> iterator = sessions.iterator();

            while (iterator.hasNext()) {
                WebSocketSession ws = iterator.next();

                if (ws.isOpen()) {
                    ws.sendMessage(new TextMessage(payload));
                } else {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer roomId = sessionRoomMap.remove(session.getId());

        if (roomId != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);

            if (sessions != null) {
                sessions.remove(session);

                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }
    }
}