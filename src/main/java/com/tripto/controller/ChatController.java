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

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int loginUserId = loginMember.getSeqMember();
        
        if (roomId != null && roomId > 0) {
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
            messageList = chatService.getMessageList(selectedRoomId, loginUserId);

            for (ChatRoomDTO room : roomList) {
                if (room.getRoomId() == selectedRoomId) {
                    selectedRoom = room;
                    break;
                }
            }
            
            // 🌟 [충돌 해결: 통합 영역]
            // 1. 내가 방장인지 권한 확인 (Head 코드)
            int myAuth = chatService.getRoomAuth(selectedRoomId, loginUserId);
            model.addAttribute("myAuth", myAuth);

            // 2. 채팅방 멤버 목록 가져오기 (Dev 코드)
            List<ChatMemberDTO> memberList = chatService.getChatRoomMembers(selectedRoomId);
            model.addAttribute("memberList", memberList);
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

        if (message == null || message.trim().isEmpty()) {
            message = "사진을 보냈습니다."; 
        }

        chatService.insertMessage(roomId, loginMember.getSeqMember(), message);

        if (category == null) return "redirect:/chat/list?roomId=" + roomId;
        return "redirect:/chat/list?roomId=" + roomId + "&category=" + category;
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
        int myAuth = chatService.getRoomAuth(roomId, loginUserId);
        ChatRoomDTO room = chatService.getRoomById(roomId, loginUserId);

        // 🌟 방장 나기기 방지 로직 (게시글이 NORMAL일 때만 차단)
        if (myAuth == 0 && room != null && room.getCategory() == 0) {
            String postStatus = chatService.getPostStatusByRoomId(roomId);
            if (postStatus != null && postStatus.trim().equalsIgnoreCase("NORMAL")) {
                response.put("success", false);
                response.put("message", "모집 게시글이 게시판에 게시 중일 때는 채팅방을 나갈 수 없습니다.\n모집을 취소하려면 게시글을 먼저 삭제해주세요.");
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

        model.addAttribute("roomId", roomId);
        model.addAttribute("routineList", chatService.getRoutineList(roomId));
        model.addAttribute("pollList", chatService.getPollList(roomId));
        model.addAttribute("loginUserId", loginMember.getSeqMember());
        model.addAttribute("selectedRoom", chatService.getRoomById(roomId, loginMember.getSeqMember()));

        return "chat/schedulePoll";
    }

    @GetMapping("/chat/poll/write")
    public String pollWrite(@RequestParam("roomId") int roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "chat/pollWrite";
    }

    @PostMapping("/chat/poll/write")
    public String pollWriteOk(PollDTO dto, @RequestParam("pollContents") List<String> pollContents) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        dto.setSeqMember(loginMember.getSeqMember());
        chatService.insertPoll(dto, pollContents);

        String sysMsg = "<a href='/TripTo/chat/poll/detail?roomId=" + dto.getSeqChattingroom() 
                      + "&pollId=" + dto.getSeq() + "' "
                      + "class='text-blue-600 underline font-bold hover:text-blue-800'>"
                      + "📋 [투표] " + dto.getPollTitle() + "</a><br>새로운 투표가 등록되었습니다!";
        
        chatService.insertMessage(dto.getSeqChattingroom(), 0, sysMsg);
        chatWebSocketHandler.broadcastSystemMessage(dto.getSeqChattingroom(), sysMsg);

        return "redirect:/chat/schedulePoll?roomId=" + dto.getSeqChattingroom();
    }

    @GetMapping("/chat/poll/detail")
    public String pollDetail(@RequestParam("roomId") int roomId, @RequestParam("pollId") int pollId, Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        model.addAttribute("roomId", roomId);
        model.addAttribute("poll", chatService.getPollDetail(pollId));
        model.addAttribute("pollContentList", chatService.getPollContentList(pollId));
        model.addAttribute("loginUserId", loginMember.getSeqMember());

        return "chat/pollDetail";
    }

    @PostMapping("/chat/poll/vote")
    public String votePoll(@RequestParam("roomId") int roomId, @RequestParam("pollId") int pollId, @RequestParam("pollContentId") int pollContentId) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        chatService.votePoll(pollId, pollContentId, loginMember.getSeqMember());
        return "redirect:/chat/poll/detail?roomId=" + roomId + "&pollId=" + pollId;
    }

    @PostMapping("/chat/poll/delete")
    public String deletePoll(@RequestParam("roomId") int roomId, @RequestParam("pollId") int pollId) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        chatService.deletePoll(pollId, loginMember.getSeqMember());
        return "redirect:/chat/schedulePoll?roomId=" + roomId;
    }

    @GetMapping("/chat/routine/write")
    public String routineWrite(@RequestParam("roomId") int roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "chat/routineWrite";
    }

    @PostMapping("/chat/routine/write")
    public String routineWriteOk(RoutineDTO dto, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

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

        chatService.deleteRoutine(routineId, loginMember.getSeqMember());
        return "redirect:/chat/schedulePoll?roomId=" + roomId;
    }

    @GetMapping("/chat/routine/edit")
    public String routineEdit(@RequestParam("roomId") int roomId, 
                              @RequestParam("routineId") int routineId, 
                              Model model) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

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
            if (loginMember == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dh5p4lvo2",
                "api_key", "283127846695383",
                "api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k",
                "secure", true
            ));

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            int seqFile = chatService.uploadCloudinaryFile(file.getOriginalFilename(), imageUrl, loginMember.getSeqMember(), roomId);
            response.put("success", true);
            response.put("seqFile", seqFile);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "파일 업로드 실패: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/chat/travel")
    public String startTravelChat(@RequestParam("seqTravelPost") int seqTravelPost) {
        MemberDTO loginMember = getLoginMember();
        if (loginMember == null) return "redirect:/member/login.do";

        TravelPostDTO post = chatService.getTravelPostForChat(seqTravelPost);
        Integer roomId = chatService.findTravelRoom(seqTravelPost);

        if (roomId == null) {
            return "redirect:/travel/list.do?message=" + java.net.URLEncoder.encode("채팅방이 존재하지 않습니다.");
        }

        if (post != null && post.getSeqMember() == loginMember.getSeqMember()) {
            return "redirect:/chat/list?roomId=" + roomId + "&category=0";
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("userId", loginMember.getSeqMember());
        
        if (chatService.insertUserChatIfNotExists(map) == 0) {
            return "redirect:/chat/list?message=" + java.net.URLEncoder.encode("이미 참여 신청을 했거나 참여 중인 방입니다.");
        }

        String joinMsg = loginMember.getNickname() + "님이 동행 참여를 신청했습니다. "
                       + "<span data-applicant-seq='" + loginMember.getSeqMember() + "'></span>";
        
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
            result.put("msg", "처리 중 오류가 발생했습니다.");
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
            result.put("message", "이미 참여 신청을 했거나 참여 중인 동행입니다.");
            return result;
        }

        String joinMsg = loginMember.getNickname() + "님이 동행 참여를 신청했습니다. "
                       + "<span data-applicant-seq='" + loginMember.getSeqMember() + "'></span>";
        
        chatService.insertMessage(roomId, 0, joinMsg);
        if(chatWebSocketHandler != null) chatWebSocketHandler.broadcastSystemMessage(roomId, joinMsg);

        result.put("success", true);
        result.put("message", "참여 신청이 완료되었습니다!\n방장의 승인 후 채팅방에 입장할 수 있습니다.");
        return result;
    }

    private MemberDTO getLoginMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return null;
        return memberService.getMemberById(auth.getName());
    }
}