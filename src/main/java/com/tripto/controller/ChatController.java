package com.tripto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.service.ChatService;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/chat/list")
    public String list(
            @RequestParam(value = "roomId", required = false) Integer roomId,
            @RequestParam(value = "category", required = false) Integer category,
            Model model) {

        int loginUserId = 1; // 임시 로그인 사용자

        List<ChatRoomDTO> roomList = chatService.getRoomList(loginUserId, roomId, category);

        Integer selectedRoomId = roomId;
        if ((selectedRoomId == null || selectedRoomId == 0) && roomList != null && !roomList.isEmpty()) {
            selectedRoomId = roomList.get(0).getRoomId();
        }

        List<ChatMessageDTO> messageList = new ArrayList<>();
        ChatRoomDTO selectedRoom = null;

        if (selectedRoomId != null && selectedRoomId > 0) {
            messageList = chatService.getMessageList(selectedRoomId, loginUserId);

            for (ChatRoomDTO room : roomList) {
                if (room.getRoomId() == selectedRoomId) {
                    selectedRoom = room;
                    break;
                }
            }
        }

        model.addAttribute("roomList", roomList);
        model.addAttribute("messageList", messageList);
        model.addAttribute("selectedRoomId", selectedRoomId);
        model.addAttribute("selectedRoom", selectedRoom);
        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("category", category);

        return "chat/list";
    }

    @PostMapping("/chat/send")
    public String sendMessage(
            @RequestParam("roomId") int roomId,
            @RequestParam("message") String message,
            @RequestParam(value = "category", required = false) Integer category) {

        int loginUserId = 1; // 임시 로그인 사용자

        chatService.insertMessage(roomId, loginUserId, message);

        if (category == null) {
            return "redirect:/chat/list?roomId=" + roomId;
        }

        return "redirect:/chat/list?roomId=" + roomId + "&category=" + category;
    }
    
    @PostMapping("/chat/exit")
    @ResponseBody
    public Map<String, Object> exitRoom(
            @RequestParam("roomId") int roomId) {

        int loginUserId = 1; // 임시 로그인

        boolean result = chatService.exitRoom(roomId, loginUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);

        return response;
    }
}