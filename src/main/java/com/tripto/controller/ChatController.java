package com.tripto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.service.ChatService;
import com.tripto.dto.RoutineDTO;
import com.tripto.dto.PollDTO;
import com.tripto.dto.PollContentDTO;
import com.tripto.dto.MemberDTO;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/chat/list")
    public String list(
            @RequestParam(value = "roomId", required = false) Integer roomId,
            @RequestParam(value = "category", required = false) Integer category,
            Model model) {

    	Integer loginUserId = getLoginUserSeq();

    	if (loginUserId == null) {
    	    return "redirect:/member/login.do";
    	}

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

    	Integer loginUserId = getLoginUserSeq();

    	if (loginUserId == null) {
    	    return "redirect:/member/login.do";
    	} 

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

    	Integer loginUserId = getLoginUserSeq();

        boolean result = chatService.exitRoom(roomId, loginUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);

        return response;
    }
    
    @GetMapping("/chat/schedulePoll")
    public String schedulePoll(
            @RequestParam("roomId") int roomId,
            Model model) {

        Integer loginUserId = getLoginUserSeq();

        if (loginUserId == null) {
            return "redirect:/member/login.do";
        }

        List<RoutineDTO> routineList = chatService.getRoutineList(roomId);
        List<PollDTO> pollList = chatService.getPollList(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("routineList", routineList);
        model.addAttribute("pollList", pollList);
        model.addAttribute("loginUserId", loginUserId);

        return "chat/schedulePoll";
    }
    
    @GetMapping("/chat/poll/write")
    public String pollWrite(
            @RequestParam("roomId") int roomId,
            Model model) {

        model.addAttribute("roomId", roomId);

        return "chat/pollWrite";
    }

    @PostMapping("/chat/poll/write")
    public String pollWriteOk(
            PollDTO dto,
            @RequestParam("pollContents") List<String> pollContents) {

    	Integer loginUserId = getLoginUserSeq();

    	if (loginUserId == null) {
    	    return "redirect:/member/login.do";
    	}

        dto.setSeqMember(loginUserId);

        chatService.insertPoll(dto, pollContents);

        return "redirect:/chat/schedulePoll?roomId=" + dto.getSeqChattingroom();
    }
    
    @GetMapping("/chat/poll/detail")
    public String pollDetail(
            @RequestParam("roomId") int roomId,
            @RequestParam("pollId") int pollId,
            Model model) {

        PollDTO poll = chatService.getPollDetail(pollId);
        List<PollContentDTO> pollContentList = chatService.getPollContentList(pollId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("poll", poll);
        model.addAttribute("pollContentList", pollContentList);

        return "chat/pollDetail";
    }
    
    @PostMapping("/chat/poll/vote")
    public String votePoll(
            @RequestParam("roomId") int roomId,
            @RequestParam("pollId") int pollId,
            @RequestParam("pollContentId") int pollContentId) {

    	Integer loginUserId = getLoginUserSeq();

    	if (loginUserId == null) {
    	    return "redirect:/member/login.do";
    	}

        chatService.votePoll(pollId, pollContentId, loginUserId);

        return "redirect:/chat/poll/detail?roomId=" + roomId + "&pollId=" + pollId;
    }
    
    @PostMapping("/chat/poll/delete")
    public String deletePoll(
            @RequestParam("roomId") int roomId,
            @RequestParam("pollId") int pollId) {

    	Integer loginUserId = getLoginUserSeq();

    	if (loginUserId == null) {
    	    return "redirect:/member/login.do";
    	}

        chatService.deletePoll(pollId, loginUserId);

        return "redirect:/chat/schedulePoll?roomId=" + roomId;
    }
    
    @GetMapping("/chat/routine/write")
    public String routineWrite(
            @RequestParam("roomId") int roomId,
            Model model) {

        model.addAttribute("roomId", roomId);

        return "chat/routineWrite";
    }
    
    @PostMapping("/chat/routine/write")
    public String routineWriteOk(RoutineDTO dto) {

    	Integer loginUserId = getLoginUserSeq();

    	if (loginUserId == null) {
    	    return "redirect:/member/login.do";
    	}

        dto.setSeqMember(loginUserId);

        chatService.insertRoutine(dto);

        return "redirect:/chat/schedulePoll?roomId=" + dto.getSeqChattingroom();
    }

    @GetMapping("/chat/routine/detail")
    public String routineDetail(
            @RequestParam("roomId") int roomId,
            @RequestParam("routineId") int routineId,
            Model model) {

        Integer loginUserId = getLoginUserSeq();

        if (loginUserId == null) {
            return "redirect:/member/login.do";
        }

        RoutineDTO routine = chatService.getRoutineDetail(routineId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("routine", routine);
        model.addAttribute("loginUserId", loginUserId);

        return "chat/routineDetail";
    }
    
    private Integer getLoginUserSeq() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String loggedInId = auth.getName();

        MemberDTO loginUser = memberService.getMemberById(loggedInId);

        if (loginUser == null) {
            return null;
        }

        return loginUser.getSeqMember();
    }
    
 // 🌟 매칭 프로필에서 1:1 채팅 시작하기
    @GetMapping("/chat/start")
    public String startChat(@RequestParam("targetSeq") int targetSeq) {

        // 1. 내 로그인 번호 확인
        Integer loginUserId = getLoginUserSeq();
        
        if (loginUserId == null) {
            return "redirect:/member/login.do";
        }

        // 2. 나 자신에게 채팅을 거는 경우 튕겨내기 (방어 코드)
        if (loginUserId == targetSeq) {
            return "redirect:/matching/detail?seqMember=" + targetSeq;
        }

        // 3. 서비스 호출: 기존 방이 있으면 가져오고, 없으면 방 + 참여자 생성 후 방 번호 리턴
        int roomId = chatService.createOrGetMatchingChatRoom(loginUserId, targetSeq);

        // 4. 찾은(또는 생성된) 방으로 리다이렉트 이동! (카테고리 1 = 매칭)
        return "redirect:/chat/list?roomId=" + roomId + "&category=1";
    }
    
}