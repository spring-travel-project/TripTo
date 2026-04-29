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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tripto.dto.ChatMemberDTO;
import com.tripto.dto.ChatMessageDTO;
import com.tripto.dto.ChatRoomDTO;
import com.tripto.dto.FileDTO;
import com.tripto.dto.MemberDTO;
import com.tripto.dto.PollContentDTO;
import com.tripto.dto.PollDTO;
import com.tripto.dto.RoutineDTO;
import com.tripto.dto.TravelPostDTO;
import com.tripto.service.ChatService;
import com.tripto.service.MemberService;
import com.tripto.websocket.ChatWebSocketHandler;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @GetMapping("/chat/list")
    public String list(
            @RequestParam(value = "roomId", required = false) Integer roomId,
            @RequestParam(value = "category", required = false) Integer category,
            Model model) {

        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        int loginUserId = loginMember.getSeqMember();

        if (roomId != null && roomId > 0) {
            // 접근 권한 체크
            if (!canAccessRoom(roomId, loginUserId)) return "redirect:/chat/list";
            chatService.updateReadStatus(roomId, loginUserId);
        }

        List<ChatRoomDTO> roomList = chatService.getRoomList(loginUserId, roomId, category);

        Integer selectedRoomId = roomId;
        if ((selectedRoomId == null || selectedRoomId == 0) && roomList != null && !roomList.isEmpty()) {
            selectedRoomId = roomList.get(0).getRoomId();
        }

        List<ChatMessageDTO> messageList = new ArrayList<>();
        ChatRoomDTO selectedRoom = null;

        if (selectedRoomId != null && selectedRoomId > 0) {
            if (!canAccessRoom(selectedRoomId, loginUserId)) return "redirect:/chat/list";

            messageList = chatService.getMessageList(selectedRoomId, loginUserId);

            for (ChatRoomDTO room : roomList) {
                if (room.getRoomId() == (int)selectedRoomId) {
                    selectedRoom = room;
                    break;
                }
            }

            // 통합 로직: 방장 권한 + 멤버 목록
            model.addAttribute("myAuth", chatService.getRoomAuth(selectedRoomId, loginUserId));
            model.addAttribute("memberList", chatService.getChatRoomMembers(selectedRoomId));
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
        if (loginMember == null) return "redirect:/member/login.do";
        
        int loginUserId = loginMember.getSeqMember();
        if (!canAccessRoom(roomId, loginUserId)) return "redirect:/chat/list";

        String finalMsg = (message == null || message.trim().isEmpty()) ? "사진을 보냈습니다." : message;
        if (finalMsg.length() > 1000) finalMsg = finalMsg.substring(0, 1000);

        chatService.insertMessage(roomId, loginUserId, finalMsg);

        String redirectUrl = "redirect:/chat/list?roomId=" + roomId;
        if (category != null) redirectUrl += "&category=" + category;
        return redirectUrl;
    }

    @PostMapping("/chat/exit")
    @ResponseBody
    public Map<String, Object> exitRoom(@RequestParam("roomId") int roomId) {
        Map<String, Object> response = new HashMap<>();
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        int loginUserId = loginMember.getSeqMember();
        if (!canAccessRoom(roomId, loginUserId)) {
            response.put("success", false);
            response.put("message", "접근 권한이 없습니다.");
            return response;
        }

        // 방장 나가기 방지 로직 (NORMAL 상태일 때)
        int myAuth = chatService.getRoomAuth(roomId, loginUserId);
        ChatRoomDTO room = chatService.getRoomById(roomId, loginUserId);
        if (myAuth == 0 && room != null && room.getCategory() == 0) {
            String postStatus = chatService.getPostStatusByRoomId(roomId);
            if (postStatus != null && postStatus.trim().equalsIgnoreCase("NORMAL")) {
                response.put("success", false);
                response.put("message", "모집 게시글이 게시 중일 때는 채팅방을 나갈 수 없습니다.\n게시글을 먼저 삭제해주세요.");
                return response;
            }
        }

        boolean result = chatService.exitRoom(roomId, loginUserId);
        response.put("success", result);
        return response;
    }

    @GetMapping("/chat/schedulePoll")
    public String schedulePoll(@RequestParam("roomId") int roomId, Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        model.addAttribute("roomId", roomId);
        model.addAttribute("routineList", chatService.getRoutineList(roomId));
        model.addAttribute("pollList", chatService.getPollList(roomId));
        model.addAttribute("loginUserId", loginMember.getSeqMember());
        model.addAttribute("selectedRoom", chatService.getRoomById(roomId, loginMember.getSeqMember()));

        return "chat/schedulePoll";
    }

    @GetMapping("/chat/poll/write")
    public String pollWrite(@RequestParam("roomId") int roomId, Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        model.addAttribute("roomId", roomId);
        return "chat/pollWrite";
    }

    @PostMapping("/chat/poll/write")
    public String pollWriteOk(PollDTO dto, @RequestParam("pollContents") List<String> pollContents) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        int loginUserId = loginMember.getSeqMember();
        int roomId = dto.getSeqChattingroom();
        if (!canAccessRoom(roomId, loginUserId)) return "redirect:/chat/list";

        dto.setSeqMember(loginUserId);
        chatService.insertPoll(dto, pollContents);

        String safeTitle = escapeHtml(dto.getPollTitle());
        String sysMsg = "<a href='/TripTo/chat/poll/detail?roomId=" + roomId 
                      + "&pollId=" + dto.getSeq() + "' "
                      + "class='text-blue-600 underline font-bold hover:text-blue-800'>"
                      + "📋 [투표] " + safeTitle + "</a><br>새로운 투표가 등록되었습니다!";
        
        chatService.insertMessage(roomId, 0, sysMsg);
        chatWebSocketHandler.broadcastSystemMessage(roomId, sysMsg);

        return "redirect:/chat/schedulePoll?roomId=" + roomId;
    }

    @GetMapping("/chat/poll/detail")
    public String pollDetail(@RequestParam("roomId") int roomId, @RequestParam("pollId") int pollId, Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        int loginUserId = loginMember.getSeqMember();
        if (!canAccessRoom(roomId, loginUserId)) return "redirect:/chat/list";

        PollDTO poll = chatService.getPollDetail(pollId);
        if (poll == null || poll.getSeqChattingroom() != roomId) return "redirect:/chat/schedulePoll?roomId=" + roomId;

        model.addAttribute("roomId", roomId);
        model.addAttribute("poll", poll);
        model.addAttribute("pollContentList", chatService.getPollContentList(pollId));
        model.addAttribute("loginUserId", loginUserId);

        return "chat/pollDetail";
    }

    @PostMapping("/chat/poll/vote")
    public String votePoll(@RequestParam("roomId") int roomId, @RequestParam("pollId") int pollId, @RequestParam("pollContentId") int pollContentId) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        PollDTO poll = chatService.getPollDetail(pollId);
        if (poll == null || poll.getSeqChattingroom() != roomId) return "redirect:/chat/schedulePoll?roomId=" + roomId;

        chatService.votePoll(pollId, pollContentId, loginMember.getSeqMember());
        return "redirect:/chat/poll/detail?roomId=" + roomId + "&pollId=" + pollId;
    }

    @PostMapping("/chat/poll/delete")
    public String deletePoll(@RequestParam("roomId") int roomId, @RequestParam("pollId") int pollId) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        PollDTO poll = chatService.getPollDetail(pollId);
        if (poll == null || poll.getSeqChattingroom() != roomId) return "redirect:/chat/schedulePoll?roomId=" + roomId;

        chatService.deletePoll(pollId, loginMember.getSeqMember());
        return "redirect:/chat/schedulePoll?roomId=" + roomId;
    }

    @GetMapping("/chat/routine/write")
    public String routineWrite(@RequestParam("roomId") int roomId, Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        model.addAttribute("roomId", roomId);
        return "chat/routineWrite";
    }

    @PostMapping("/chat/routine/write")
    public String routineWriteOk(RoutineDTO dto, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(dto.getSeqChattingroom(), loginMember.getSeqMember())) return "redirect:/chat/list";

        dto.setSeqMember(loginMember.getSeqMember());
        chatService.insertRoutine(dto, files);

        String sysMsg = "<a href='/TripTo/chat/routine/detail?roomId=" + dto.getSeqChattingroom() 
                      + "&routineId=" + dto.getSeq() + "' "
                      + "class='text-emerald-600 underline font-bold hover:text-emerald-800'>"
                      + "📅 [일정] " + dto.getTitle() + "</a><br>새로운 일정이 등록되었습니다!";
        
        chatService.insertMessage(dto.getSeqChattingroom(), 0, sysMsg);
        return "redirect:/chat/schedulePoll?roomId=" + dto.getSeqChattingroom();
    }

    @GetMapping("/chat/routine/detail")
    public String routineDetail(@RequestParam("roomId") int roomId, @RequestParam("routineId") int routineId, Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        model.addAttribute("roomId", roomId);
        model.addAttribute("routine", chatService.getRoutineDetail(routineId));
        model.addAttribute("fileList", chatService.getRoutineFileList(routineId));
        model.addAttribute("loginUserId", loginMember.getSeqMember());

        return "chat/routineDetail";
    }

    @GetMapping("/chat/start")
    public String startChat(@RequestParam("targetSeq") int targetSeq) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        int loginUserId = loginMember.getSeqMember();
        if (loginUserId == targetSeq) return "redirect:/matching/detail?seqMember=" + targetSeq;

        int roomId = chatService.createOrGetMatchingChatRoom(loginUserId, targetSeq);
        return "redirect:/chat/list?roomId=" + roomId + "&category=1";
    }

    @PostMapping("/chat/routine/delete")
    public String deleteRoutine(@RequestParam("roomId") int roomId, @RequestParam("routineId") int routineId) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        chatService.deleteRoutine(routineId, loginMember.getSeqMember());
        return "redirect:/chat/schedulePoll?roomId=" + roomId;
    }

    @GetMapping("/chat/routine/edit")
    public String routineEdit(@RequestParam("roomId") int roomId, 
                              @RequestParam("routineId") int routineId, 
                              Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(roomId, loginMember.getSeqMember())) return "redirect:/chat/list";

        RoutineDTO routine = chatService.getRoutineDetail(routineId);
        if (routine == null || routine.getSeqMember() != loginMember.getSeqMember()) {
            return "redirect:/chat/schedulePoll?roomId=" + roomId;
        }
        
        List<FileDTO> fileList = chatService.getRoutineFileList(routineId);
        System.out.println("수정 화면 파일 개수 = " + fileList.size());

        model.addAttribute("roomId", roomId);
        model.addAttribute("routine", routine);
        model.addAttribute("fileList", chatService.getRoutineFileList(routineId));

        return "chat/routineEdit";
    }

    @PostMapping("/chat/routine/edit")
    public String routineEditOk(RoutineDTO dto,
                                @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                @RequestParam(value = "removedFiles", required = false) String removedFiles) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";
        if (!canAccessRoom(dto.getSeqChattingroom(), loginMember.getSeqMember())) return "redirect:/chat/list";

        chatService.updateRoutine(dto, loginMember.getSeqMember(), files, removedFiles);

        return "redirect:/chat/routine/detail?roomId=" 
                + dto.getSeqChattingroom() 
                + "&routineId=" 
                + dto.getSeq();
    }

    @PostMapping("/chat/uploadFile.do")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("roomId") int roomId) {
        Map<String, Object> response = new HashMap<>();
        try {
            MemberDTO loginMember = getLoginMember();
            if (loginMember == null || !canAccessRoom(roomId, loginMember.getSeqMember())) {
                response.put("success", false);
                response.put("message", "권한이 없습니다.");
                return response;
            }

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dh5p4lvo2", "api_key", "283127846695383", "api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k", "secure", true
            ));

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            int seqFile = chatService.uploadCloudinaryFile(file.getOriginalFilename(), imageUrl, loginMember.getSeqMember(), roomId);
            response.put("success", true);
            response.put("seqFile", seqFile);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "파일 업로드 실패");
        }
        return response;
    }

    @GetMapping("/chat/travel")
    public String startTravelChat(@RequestParam("seqTravelPost") int seqTravelPost) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        int loginUserId = loginMember.getSeqMember();
        TravelPostDTO post = chatService.getTravelPostForChat(seqTravelPost);
        Integer roomId = chatService.findTravelRoom(seqTravelPost);

        if (roomId == null) return "redirect:/travel/list.do?message=" + java.net.URLEncoder.encode("채팅방이 존재하지 않습니다.");

        if (post != null && post.getSeqMember() == loginUserId) {
            return "redirect:/chat/list?roomId=" + roomId + "&category=0";
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("userId", loginUserId);
        
        if (chatService.insertUserChatIfNotExists(map) == 0) {
            return "redirect:/chat/list?message=" + java.net.URLEncoder.encode("이미 참여 신청을 했거나 참여 중인 방입니다.");
        }

        String joinMsg = loginMember.getNickname() + "님이 동행 참여를 신청했습니다. "
                       + "<span data-applicant-seq='" + loginUserId + "'></span>";
        
        chatService.insertMessage(roomId, 0, joinMsg);
        if(chatWebSocketHandler != null) chatWebSocketHandler.broadcastSystemMessage(roomId, joinMsg);

        return "redirect:/chat/list?message=" + java.net.URLEncoder.encode("참여 신청이 완료되었습니다. 방장의 승인을 기다려주세요!");
    }

    @PostMapping("/chat/processRequest.do")
    @ResponseBody 
    public Map<String, Object> processRequest(@RequestParam("roomId") int roomId, @RequestParam("applicantSeq") int applicantSeq, @RequestParam("status") int status, @RequestParam("msgSeq") int msgSeq) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("applicantSeq", applicantSeq);
        map.put("status", status);
        
        if (chatService.updateJoinRequest(map) > 0) {
            String applicantName = chatService.getNicknameByMemberId(applicantSeq);
            String action = (status == 1) ? "승인" : "거절";
            String finalMsg = "✅ " + applicantName + "님의 참여 요청이 " + action + "되었습니다.";
            chatService.updateSystemMessage(msgSeq, finalMsg);
            result.put("success", true);
            result.put("msg", action + "되었습니다.");
        } else {
            result.put("success", false);
            result.put("msg", "처리 중 오류 발생");
        }
        return result;
    }

    @PostMapping("/chat/apply")
    @ResponseBody
    public Map<String, Object> applyCompanion(@RequestParam("seqTravelPost") int seqTravelPost) {
        Map<String, Object> result = new HashMap<>();
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        Integer roomId = chatService.findTravelRoom(seqTravelPost);
        if (roomId == null) {
            result.put("success", false);
            result.put("message", "채팅방이 존재하지 않습니다.");
            return result;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("userId", loginMember.getSeqMember());
        
        if (chatService.insertUserChatIfNotExists(map) == 0) {
            result.put("success", false);
            result.put("message", "이미 신청했거나 참여 중입니다.");
            return result;
        }

        String joinMsg = loginMember.getNickname() + "님이 참여를 신청했습니다. <span data-applicant-seq='" + loginMember.getSeqMember() + "'></span>";
        chatService.insertMessage(roomId, 0, joinMsg);
        if(chatWebSocketHandler != null) chatWebSocketHandler.broadcastSystemMessage(roomId, joinMsg);

        result.put("success", true);
        result.put("message", "신청 완료! 승인을 기다려주세요.");
        return result;
    }

    private MemberDTO getLoginMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return null;
        return memberService.getMemberById(auth.getName());
    }
    
    private boolean canAccessRoom(int roomId, int loginUserId) {
        // chatService에 isRoomMember 메서드가 구현되어 있어야 함
        return chatService.isRoomMember(roomId, loginUserId);
    }
    
    private String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}