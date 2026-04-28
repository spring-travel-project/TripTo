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
import com.tripto.dto.MemberDTO;
import com.tripto.dto.PollContentDTO;
import com.tripto.dto.PollDTO;
import com.tripto.dto.RoutineDTO;
import com.tripto.service.ChatService;
import com.tripto.service.MemberService;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private MemberService memberService;
    
    @GetMapping("/chat/list")
    public String list(
            @RequestParam(value = "roomId", required = false) Integer roomId,
            @RequestParam(value = "category", required = false) Integer category,
            Model model) {

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember();

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

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember(); 

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

    	MemberDTO loginMember = getLoginMember();

    	int loginUserId = loginMember.getSeqMember();

        boolean result = chatService.exitRoom(roomId, loginUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);

        return response;
    }
    
    @GetMapping("/chat/schedulePoll")
    public String schedulePoll(
            @RequestParam("roomId") int roomId,
            Model model) {

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember();

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

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember();

        dto.setSeqMember(loginUserId);

        chatService.insertPoll(dto, pollContents);

        return "redirect:/chat/schedulePoll?roomId=" + dto.getSeqChattingroom();
    }
    
    @GetMapping("/chat/poll/detail")
    public String pollDetail(
            @RequestParam("roomId") int roomId,
            @RequestParam("pollId") int pollId,
            Model model) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int loginUserId = loginMember.getSeqMember();

        PollDTO poll = chatService.getPollDetail(pollId);
        List<PollContentDTO> pollContentList = chatService.getPollContentList(pollId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("poll", poll);
        model.addAttribute("pollContentList", pollContentList);

       
        model.addAttribute("loginUserId", loginUserId);

        return "chat/pollDetail";
    }
    
    @PostMapping("/chat/poll/vote")
    public String votePoll(
            @RequestParam("roomId") int roomId,
            @RequestParam("pollId") int pollId,
            @RequestParam("pollContentId") int pollContentId) {

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember();

        chatService.votePoll(pollId, pollContentId, loginUserId);

        return "redirect:/chat/poll/detail?roomId=" + roomId + "&pollId=" + pollId;
    }
    
    @PostMapping("/chat/poll/delete")
    public String deletePoll(
            @RequestParam("roomId") int roomId,
            @RequestParam("pollId") int pollId) {

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember();

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

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember();

        dto.setSeqMember(loginUserId);

        chatService.insertRoutine(dto);

        return "redirect:/chat/schedulePoll?roomId=" + dto.getSeqChattingroom();
    }

    @GetMapping("/chat/routine/detail")
    public String routineDetail(
            @RequestParam("roomId") int roomId,
            @RequestParam("routineId") int routineId,
            Model model) {

    	MemberDTO loginMember = getLoginMember();

    	if (loginMember == null) {
    	    return "redirect:/member/login.do";
    	}

    	int loginUserId = loginMember.getSeqMember();

        RoutineDTO routine = chatService.getRoutineDetail(routineId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("routine", routine);
        model.addAttribute("loginUserId", loginUserId);

        return "chat/routineDetail";
    }
    
    private MemberDTO getLoginMember() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String loggedInId = auth.getName();

        return memberService.getMemberById(loggedInId);
    }
    
 // 🌟 매칭 프로필에서 1:1 채팅 시작하기
    @GetMapping("/chat/start")
    public String startChat(@RequestParam("targetSeq") int targetSeq) {

        // 1. 옛날 이름 말고, 태훈님이 새로 만든 getLoginMember() 사용!
        MemberDTO loginMember = getLoginMember();
        
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        // 2. DTO에서 진짜 내 번호(int) 꺼내기
        int loginUserId = loginMember.getSeqMember();

        // 3. 나 자신에게 채팅을 거는 경우 튕겨내기
        if (loginUserId == targetSeq) {
            return "redirect:/matching/detail?seqMember=" + targetSeq;
        }

        // 4. 서비스 호출 (기존 방 찾기 or 새 방 만들기)
        int roomId = chatService.createOrGetMatchingChatRoom(loginUserId, targetSeq);

        // 5. 방으로 이동
        return "redirect:/chat/list?roomId=" + roomId + "&category=1";
    }
    
    @PostMapping("/chat/routine/delete")
    public String deleteRoutine(
            @RequestParam("roomId") int roomId,
            @RequestParam("routineId") int routineId) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int loginUserId = loginMember.getSeqMember();

        chatService.deleteRoutine(routineId, loginUserId);

        return "redirect:/chat/schedulePoll?roomId=" + roomId;
    }
    
    @GetMapping("/chat/routine/edit")
    public String routineEdit(
            @RequestParam("roomId") int roomId,
            @RequestParam("routineId") int routineId,
            Model model) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        RoutineDTO routine = chatService.getRoutineDetail(routineId);

        if (routine == null || routine.getSeqMember() != loginMember.getSeqMember()) {
            return "redirect:/chat/schedulePoll?roomId=" + roomId;
        }

        model.addAttribute("roomId", roomId);
        model.addAttribute("routine", routine);

        return "chat/routineEdit";
    }

    @PostMapping("/chat/routine/edit")
    public String routineEditOk(RoutineDTO dto) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        chatService.updateRoutine(dto, loginMember.getSeqMember());

        return "redirect:/chat/routine/detail?roomId="
                + dto.getSeqChattingroom()
                + "&routineId="
                + dto.getSeq();
    }
    
    @PostMapping("/chat/uploadFile.do")
    @ResponseBody
    public Map<String, Object> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomId") int roomId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MemberDTO loginMember = getLoginMember();
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }

            // ChatService에 만들어둔 uploadChatFile 호출
            int seqFile = chatService.uploadChatFile(file, loginMember.getSeqMember(), roomId);
            
            response.put("success", true);
            response.put("seqFile", seqFile); // DB에 저장된 파일 번호를 JSP로 돌려줌
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "파일 업로드 실패");
        }
        
        return response;
    }
    
}