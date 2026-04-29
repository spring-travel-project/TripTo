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

    // roomId별 세션 관리
    private final Map<Integer, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // sessionId -> roomId 매핑
    private final Map<String, Integer> sessionRoomMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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
            int seqMember = socketMessage.getSeqMember();

            roomSessions.computeIfAbsent(roomId, key -> Collections.synchronizedSet(new HashSet<>())).add(session);
            sessionRoomMap.put(session.getId(), roomId);

            // DB 업데이트: 안 읽은 메시지 읽음 처리
            chatService.updateReadStatus(roomId, seqMember);

            // 실시간 READ 신호 전송
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

                // 🌟 [핵심 수정] ORA-01400 에러 방지 로직
                // 메시지 내용이 비어있는데 파일(사진)이 있는 경우 "(사진)"으로 텍스트를 채워줍니다.
                if (message == null || message.trim().isEmpty()) {
                    if (seqFile != null) {
                        message = "(사진)"; 
                    } else {
                        return; // 파일도 없고 메시지도 없으면 저장하지 않음
                    }
                }

                ChatMessageDTO saved = null;
                try {
                    // 서비스 내부에서 DB 저장 및 unreadCount 계산
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
                    response.setSavedName(saved.getSavedName()); // Cloudinary URL 포함됨
                    response.setPartnerProfile(saved.getPartnerProfile()); // 🌟 프로필 사진 추가
                    response.setMessageTime(new SimpleDateFormat("HH:mm").format(new Date()));
                    response.setUnreadCount(saved.getUnreadCount());

                    String json = objectMapper.writeValueAsString(response);
                    broadcastToRoom(roomId, json);
                }
            } catch (Exception e) {
                System.err.println("❌ 핸들러 치명적 에러: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 3. [EXIT] 방을 나갈 때
        if ("EXIT".equals(socketMessage.getType())) {
            Integer roomId = socketMessage.getRoomId();
            if (roomId == null) return;

            ChatSocketMessageDTO response = new ChatSocketMessageDTO();
            response.setType("EXIT");
            response.setRoomId(roomId);
            response.setMessage("상대방이 채팅방을 나갔습니다.");

            String json = objectMapper.writeValueAsString(response);
            broadcastToRoom(roomId, json);
        }
    }

    private void broadcastToRoom(Integer roomId, String payload) throws Exception {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions == null) return;

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
    
    public void broadcastSystemMessage(Integer roomId, String messageHtml) {
        try {
            ChatSocketMessageDTO response = new ChatSocketMessageDTO();
            response.setType("TALK");
            response.setRoomId(roomId);
            response.setSeqMember(0); // 0번 = 시스템 메시지
            response.setMessage(messageHtml);
            response.setMessageTime(new SimpleDateFormat("HH:mm").format(new Date()));

            String json = objectMapper.writeValueAsString(response);
            broadcastToRoom(roomId, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}