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

        // 1. [ENTER] 사용자가 방에 들어왔을 때
        if ("ENTER".equals(socketMessage.getType())) {
            int roomId = socketMessage.getRoomId();
            int seqMember = socketMessage.getSeqMember(); // 🌟 읽음 처리를 위해 회원 번호 필요

            roomSessions.computeIfAbsent(roomId, key -> Collections.synchronizedSet(new HashSet<>())).add(session);
            sessionRoomMap.put(session.getId(), roomId);

            // 🌟 [추가] 1. DB 업데이트: 이 방의 안 읽은 메시지들을 내가 읽은 것으로 기록
            chatService.updateReadStatus(roomId, seqMember);

            // 🌟 [추가] 2. 실시간 신호: 방에 있는 다른 사람들에게 "누군가 들어와서 읽었으니 1 지워라!"라고 전송
            ChatSocketMessageDTO readSignal = new ChatSocketMessageDTO();
            readSignal.setType("READ");
            readSignal.setRoomId(roomId);
            readSignal.setSeqMember(seqMember);
            
            broadcastToRoom(roomId, objectMapper.writeValueAsString(readSignal));
            return;
        }

        // 2. [TALK] 메시지를 보낼 때
        if ("TALK".equals(socketMessage.getType())) {
            try {
                Integer roomId = socketMessage.getRoomId();
                Integer seqMember = socketMessage.getSeqMember();
                String message = socketMessage.getMessage();
                Integer seqFile = socketMessage.getSeqFile();

                if (roomId == null || seqMember == null) return;

                ChatMessageDTO saved = null;
                try {
                    // 🌟 서비스 내부에서 unreadCount가 계산되어 나옵니다.
                    saved = chatService.saveSocketMessage(roomId, seqMember, message, seqFile);
                } catch (Exception e) {
                    System.err.println("❌ DB 저장 중 에러: " + e.getMessage());
                }

                if (saved != null) {
                    ChatSocketMessageDTO response = new ChatSocketMessageDTO();
                    response.setType("TALK");
                    response.setRoomId(roomId);
                    response.setSeqMember(seqMember);
                    response.setNickname(saved.getNickname());
                    response.setMessage(saved.getDetail());
                    response.setSeqFile(saved.getSeqFile());
                    response.setSavedName(saved.getSavedName());
                    response.setMessageTime(new SimpleDateFormat("HH:mm").format(new Date()));
                    
                    // 🌟 [추가] 3. 실시간 숫자: "이 메시지는 처음에 1(혹은 인원수-1)로 시작해!"라고 알려줌
                    response.setUnreadCount(saved.getUnreadCount());

                    String json = objectMapper.writeValueAsString(response);
                    broadcastToRoom(roomId, json);
                }
            } catch (Exception e) {
                System.err.println("❌ 핸들러 치명적 에러: " + e.getMessage());
                e.printStackTrace();
            }
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