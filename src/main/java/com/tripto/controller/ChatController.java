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

// 🌟 Cloudinary 전용 Import 추가
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
            
            int myAuth = chatService.getRoomAuth(selectedRoomId, loginUserId);
            model.addAttribute("myAuth", myAuth);
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
        
        if ((message == null || message.trim().isEmpty())) {
            message = "사진을 보냈습니다."; 
        }

        chatService.insertMessage(roomId, loginUserId, message);

        if (category == null) {
            return "redirect:/chat/list?roomId=" + roomId;
        }

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

        // 1. 권한 확인 (방장 여부)
        int myAuth = chatService.getRoomAuth(roomId, loginUserId);
        
        // 2. 방 정보 가져오기 (카테고리 확인)
        ChatRoomDTO room = chatService.getRoomById(roomId, loginUserId);

        // 🌟 [디버깅] 콘솔창에서 이 값들을 확인해봐!
        System.out.println("--- 채팅방 나가기 체크 ---");
        System.out.println("방번호(roomId): " + roomId);
        System.out.println("내권한(myAuth): " + myAuth + " (0이면 방장)");
        
        // 3. 방장인 경우에만 게시글 상태 체크
        if (myAuth == 0 && room != null && room.getCategory() == 0) {
            
            String postStatus = chatService.getPostStatusByRoomId(roomId);
            
            // 🌟 [디버깅] DB에서 가져온 실제 상태값 확인
            System.out.println("DB에서 가져온 게시글 상태(postStatus): [" + postStatus + "]");
            
            if (postStatus != null && postStatus.trim().equalsIgnoreCase("NORMAL")) {
                response.put("success", false);
                response.put("message", "모집 게시글이 게시판에 게시 중일 때는 채팅방을 나갈 수 없습니다.\n모집을 취소하려면 게시글을 먼저 삭제해주세요.");
                return response;
            }
        }

        // 4. 조건 통과 시 퇴장 처리
        boolean result = chatService.exitRoom(roomId, loginUserId);
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

        ChatRoomDTO selectedRoom = chatService.getRoomById(roomId, loginUserId);
        
        model.addAttribute("roomId", roomId);
        model.addAttribute("routineList", routineList);
        model.addAttribute("pollList", pollList);
        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("selectedRoom", selectedRoom);

        return "chat/schedulePoll";
    }
    
    @GetMapping("/chat/poll/write")
    public String pollWrite(
            @RequestParam("roomId") int roomId,
            Model model) {

        model.addAttribute("roomId", roomId);

        return "chat/pollWrite";
    }

    // 🌟 투표 작성 완료 시 시스템 메시지 발송 로직 추가 🌟
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

        // 1. 투표 내용 DB 저장
        chatService.insertPoll(dto, pollContents);

        // 2. 시스템 메시지(회원번호: 0) 강제 전송
        // (주의: MyBatis에서 insert 후 새로 생성된 pollId를 DTO에 담아준다고 가정합니다)
        // 만약 DTO의 제목 필드명이 title이 아니라면 dto.getTitle()을 dto.get이름()으로 변경하세요.
        String sysMsg = "<a href='/TripTo/chat/poll/detail?roomId=" + dto.getSeqChattingroom() 
                      + "&pollId=" + dto.getSeq() + "' "
                      + "class='text-blue-600 underline font-bold hover:text-blue-800'>"
                      + "📋 [투표] " + dto.getPollTitle() + "</a><br>새로운 투표가 등록되었습니다!";
        
        chatService.insertMessage(dto.getSeqChattingroom(), 0, sysMsg);
        
        chatWebSocketHandler.broadcastSystemMessage(dto.getSeqChattingroom(), sysMsg);

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
    
    // 🌟 일정 작성 완료 시 시스템 메시지 발송 로직 추가 🌟
    @PostMapping("/chat/routine/write")
    public String routineWriteOk(RoutineDTO dto,
                                 @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int loginUserId = loginMember.getSeqMember();
        dto.setSeqMember(loginUserId);

        // 1. 일정 DB 저장
        chatService.insertRoutine(dto, files);

        // 2. 시스템 메시지(회원번호: 0) 강제 전송
        // RoutineDTO의 번호가 getSeq() 이고 제목이 getTitle() 이라고 가정합니다.
        String sysMsg = "<a href='/TripTo/chat/routine/detail?roomId=" + dto.getSeqChattingroom() 
                      + "&routineId=" + dto.getSeq() + "' "
                      + "class='text-emerald-600 underline font-bold hover:text-emerald-800'>"
                      + "📅 [일정] " + dto.getTitle() + "</a><br>새로운 일정이 등록되었습니다!";
        
        chatService.insertMessage(dto.getSeqChattingroom(), 0, sysMsg);

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
        List<FileDTO> fileList = chatService.getRoutineFileList(routineId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("routine", routine);
        model.addAttribute("fileList", fileList);
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

        MemberDTO loginMember = getLoginMember();
        
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int loginUserId = loginMember.getSeqMember();

        if (loginUserId == targetSeq) {
            return "redirect:/matching/detail?seqMember=" + targetSeq;
        }

        int roomId = chatService.createOrGetMatchingChatRoom(loginUserId, targetSeq);

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
    
    // 🌟🚨 Cloudinary 용으로 변경된 업로드 로직 🚨🌟
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

            // 1. Cloudinary 설정 (가입 후 발급받은 키 3개를 꼭! 넣어주세요)
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dh5p4lvo2",
                "api_key", "283127846695383",
                "api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k",
                "secure", true
            ));

            // 2. Cloudinary로 파일 쏘기!
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            
            // 3. 반환된 이미지의 "절대 주소(URL)" 가져오기
            // 이 주소는 http://res.cloudinary.com/... 형태로 나옵니다.
            String imageUrl = (String) uploadResult.get("secure_url");

            // 4. 기존 ChatService 호출 로직 변경
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
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int loginUserId = loginMember.getSeqMember();

        // 1. 게시글 정보 가져오기 (누가 쓴 글인지 확인하기 위해)
        TravelPostDTO post = chatService.getTravelPostForChat(seqTravelPost);
        
        // 2. 이미 만들어져 있는 채팅방 번호 가져오기
        Integer roomId = chatService.findTravelRoom(seqTravelPost);

        if (roomId == null) {
            // 혹시라도 예전에 써서 방이 없는 글이면 에러 처리
            return "redirect:/travel/list.do?message=" + java.net.URLEncoder.encode("채팅방이 존재하지 않습니다.");
        }

        // 🌟 3. 내가 방장(글 작성자)이라면? -> 신청 메시지 쏘지 말고 바로 내 채팅방으로 입장!
        if (post != null && post.getSeqMember() == loginUserId) {
            return "redirect:/chat/list?roomId=" + roomId + "&category=0";
        }

        // 🌟 4. 내가 신청자(일반 유저)라면? -> 참여 대기 명단에 넣고 시스템 메시지 쏘기!
        
        // DB의 user_chat 테이블에 대기자(isActive=2)로 넣기 (Map 세팅은 Service에 맞게 조절해)
        Map<String, Integer> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("userId", loginUserId);
        
        // 🌟 여기서 서비스의 결과를 받습니다. (1: 첫 신청 성공, 0: 이미 신청함)
        int insertResult = chatService.insertUserChatIfNotExists(map); 

        if (insertResult == 0) {
            // DB에 안 들어갔다 = 이미 신청해서 대기 중이거나, 이미 방에 있는 사람이다!
            // 👉 메시지 안 쏘고 여기서 컷!
            return "redirect:/chat/list?message=" + java.net.URLEncoder.encode("이미 참여 신청을 했거나 참여 중인 방입니다.");
        }

        // 👇 여기부터는 insertResult가 1일 때(처음 신청할 때)만 실행됨!
        String applicantName = loginMember.getNickname();
        String joinMsg = applicantName + "님이 동행 참여를 신청했습니다. "
                       + "<span data-applicant-seq='" + loginUserId + "'></span>";
        
        chatService.insertMessage(roomId, 0, joinMsg);
        
        if(chatWebSocketHandler != null) {
            chatWebSocketHandler.broadcastSystemMessage(roomId, joinMsg);
        }

        return "redirect:/chat/list?message=" + java.net.URLEncoder.encode("참여 신청이 완료되었습니다. 방장의 승인을 기다려주세요!");
    }
    
    @PostMapping("/chat/processRequest.do")
    @ResponseBody 
    public Map<String, Object> processRequest(
            @RequestParam("roomId") int roomId, 
            @RequestParam("applicantSeq") int applicantSeq, 
            @RequestParam("status") int status,
            @RequestParam("msgSeq") int msgSeq) { // 🌟 파라미터에 msgSeq 추가!
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        
        map.put("roomId", roomId);
        map.put("applicantSeq", applicantSeq);
        map.put("status", status); // 1: 승인, 0: 거절
        
        // 1. 유저 상태 변경 (승인/거절)
        int row = chatService.updateJoinRequest(map);
        
        if (row > 0) {
            // 🌟 2. 상태 변경 성공 시 시스템 메시지도 영구적으로 업데이트!
            String applicantName = chatService.getNicknameByMemberId(applicantSeq);
            String action = (status == 1) ? "승인" : "거절";
            String finalMsg = "✅ " + applicantName + "님의 참여 요청이 " + action + "되었습니다.";
            
            chatService.updateSystemMessage(msgSeq, finalMsg); // 시스템 메시지 덮어쓰기 호출
            
            result.put("success", true);
            result.put("msg", action + "되었습니다.");
        } else {
            result.put("success", false);
            result.put("msg", "처리 중 오류가 발생했습니다.");
        }
        
        return result;
    }
    
 // 🌟 동행 참여 신청 (AJAX 전용)
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

        int loginUserId = loginMember.getSeqMember();
        Integer roomId = chatService.findTravelRoom(seqTravelPost);

        if (roomId == null) {
            result.put("success", false);
            result.put("message", "채팅방이 존재하지 않습니다.");
            return result;
        }

        // 대기 명단에 넣기 (0이면 이미 신청했거나 참여 중인 상태)
        Map<String, Integer> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("userId", loginUserId);
        
        int insertResult = chatService.insertUserChatIfNotExists(map); 

        if (insertResult == 0) {
            result.put("success", false);
            result.put("message", "이미 참여 신청을 했거나 참여 중인 동행입니다.");
            return result;
        }

        // 처음 신청하는 경우 방장에게 시스템 메시지 발송
        String applicantName = loginMember.getNickname();
        String joinMsg = applicantName + "님이 동행 참여를 신청했습니다. "
                       + "<span data-applicant-seq='" + loginUserId + "'></span>";
        
        chatService.insertMessage(roomId, 0, joinMsg);
        
        if(chatWebSocketHandler != null) {
            chatWebSocketHandler.broadcastSystemMessage(roomId, joinMsg);
        }

        result.put("success", true);
        result.put("message", "참여 신청이 완료되었습니다!\n방장의 승인 후 채팅방에 입장할 수 있습니다.");
        return result;
    }
}