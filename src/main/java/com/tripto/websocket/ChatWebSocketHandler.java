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

     // 🌟 handleTextMessage 메서드 내의 TALK 부분만 교체하세요!
        if ("TALK".equals(socketMessage.getType())) {
            try { // 🛡️ 1차 방어막: 메시지 처리 전체를 감쌉니다.
                Integer roomId = socketMessage.getRoomId();
                Integer seqMember = socketMessage.getSeqMember();
                String message = socketMessage.getMessage();
                Integer seqFile = socketMessage.getSeqFile(); // 🌟 프론트에서 보낸 파일번호

                if (roomId == null || seqMember == null) return;

                // 🌟 서비스 호출 (에러가 나기 쉬운 DB 구간이므로 한 번 더 감싸기)
                ChatMessageDTO saved = null;
                try {
                    saved = chatService.saveSocketMessage(roomId, seqMember, message, seqFile);
                } catch (Exception e) {
                    System.err.println("❌ DB 저장 중 에러 발생 (이모티콘/용량 등): " + e.getMessage());
                    // 여기서 에러가 나도 아래 response 전송 로직으로 가지 않게 saved는 null 유지
                }

                if (saved != null) {
                    ChatSocketMessageDTO response = new ChatSocketMessageDTO();
                    response.setType("TALK");
                    response.setRoomId(roomId);
                    response.setSeqMember(seqMember);
                    response.setNickname(saved.getNickname());
                    response.setMessage(saved.getDetail());
                    response.setSeqFile(saved.getSeqFile());
                    response.setSavedName(saved.getSavedName()); // 🌟 사진 출력을 위해 파일명 세팅!
                    response.setMessageTime(new SimpleDateFormat("HH:mm").format(new Date()));

                    String json = objectMapper.writeValueAsString(response);
                    broadcastToRoom(roomId, json);
                }
            } catch (Exception e) {
                // 🛡️ 2차 방어막: 여기서 에러를 잡아줘야 '두 번째 메시지' 전송 시 소켓이 안 끊깁니다!
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