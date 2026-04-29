package com.tripto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication; // 🌟 시큐리티 추가
import org.springframework.security.core.context.SecurityContextHolder; // 🌟 시큐리티 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tripto.dto.MatchDTO;
import com.tripto.dto.MemberDTO; // 🌟 회원 정보 DTO 추가
import com.tripto.service.MatchingService;
import com.tripto.service.MemberService; // 🌟 내 번호 찾기용 서비스 추가

@Controller
public class MatchingController {

    private final MatchingService matchingService;
    private final MemberService memberService; // 🌟 MemberService 추가

    // 생성자를 통한 의존성 주입 (권장 방식)
    public MatchingController(MatchingService matchingService, MemberService memberService) {
        this.matchingService = matchingService;
        this.memberService = memberService;
    }

    @GetMapping("/matching/list.do")
    public String matchingList(
            @RequestParam(required = false) String mbti,
            @RequestParam(required = false) Integer smoking,
            @RequestParam(required = false) Integer drinking,
            @RequestParam(required = false) Integer travelType,
            @RequestParam(required = false) Integer stepCount,
            @RequestParam(required = false) String pic,
            Model model) {

        // ========================================================
        // 🌟 1. 스프링 시큐리티 금고에서 현재 로그인한 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // 비로그인 상태면 로그인 페이지로 튕겨내기
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/member/login.do"; 
        }

        // 시큐리티에서 로그인 아이디(String) 꺼내서 DB에서 내 진짜 번호(seqMember) 찾기
        String loggedInId = auth.getName();
        MemberDTO loginUser = memberService.getMemberById(loggedInId);
        
        if (loginUser == null) {
            return "redirect:/member/login.do";
        }
        
        int loginSeq = loginUser.getSeqMember(); // 진짜 내 번호 획득!
        // ========================================================

        Map<String, Object> params = new HashMap<>();
        params.put("loginSeq", loginSeq); 
        
        params.put("mbti", mbti);
        params.put("smoking", smoking);
        params.put("drinking", drinking);
        params.put("travelType", travelType);
        params.put("stepCount", stepCount);
        params.put("pic", pic);

        MatchDTO myProfile = matchingService.getMyProfile(loginSeq);
        List<MatchDTO> matchList = matchingService.getMatchingList(params);

        model.addAttribute("myProfile", myProfile);
        model.addAttribute("matchList", matchList);
        model.addAttribute("params", params); 

        return "matching/list";
    }
    
    @GetMapping("/matching/detail")
    public String matchingDetail(@RequestParam("seqMember") int targetSeq, Model model) {
        
        // ========================================================
        // 🌟 1. 시큐리티에서 인증 정보 가져오기 (상세 페이지도 동일하게 적용)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/member/login.do";
        }

        // 내 회원 번호 알아내기
        String loggedInId = auth.getName();
        MemberDTO loginUser = memberService.getMemberById(loggedInId);
        
        if (loginUser == null) {
            return "redirect:/member/login.do";
        }
        
        int loginSeq = loginUser.getSeqMember();
        // ========================================================

        Map<String, Integer> map = new HashMap<>();
        map.put("loginSeq", loginSeq);
        map.put("targetSeq", targetSeq);

        // 2. 상대방 정보 및 매칭 데이터 가져오기
        MatchDTO targetProfile = matchingService.getMatchDetail(map);
        
        // 3. 내 정보 가져오기
        MatchDTO myProfile = matchingService.getMyProfile(loginSeq);

        // 4. JSP로 전달
        model.addAttribute("target", targetProfile);
        model.addAttribute("me", myProfile);

        return "matching/detail";
    }
    
 // 🌟 프로필 사진 수정 (Cloudinary 업로드)
    @PostMapping("/member/updateProfile.do")
    public String updateProfile(@RequestParam("picFile") MultipartFile picFile) {
        
        try {
            // 1. 시큐리티에서 현재 로그인한 유저 정보 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/member/login.do";
            }
            
            String loggedInId = auth.getName();
            MemberDTO loginUser = memberService.getMemberById(loggedInId);
            int loginSeq = loginUser.getSeqMember();

            // 2. 파일이 비어있는지 체크
            if (picFile != null && !picFile.isEmpty()) {
                
                // 3. Cloudinary 설정 (태훈님의 진짜 키를 꼭 입력하세요!)
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                		 "cloud_name", "dh5p4lvo2",
                         "api_key", "283127846695383",
                         "api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k",
                         "secure", true
                ));

                // 4. 클라우드에 실제 업로드 진행
                Map uploadResult = cloudinary.uploader().upload(picFile.getBytes(), ObjectUtils.emptyMap());
                
                // 5. 업로드된 사진의 "전체 주소(URL)" 가져오기
                String imageUrl = (String) uploadResult.get("secure_url");

                // 6. DB 업데이트 (member 테이블의 pic 컬럼에 imageUrl 저장)
                // 💡 MemberService에 updateProfilePic(int seq, String url) 메서드가 있어야 합니다!
                memberService.updateProfilePic(loginSeq, imageUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 에러 시 처리 로직 (필요시 추가)
        }

        // 수정한 뒤 마이페이지로 이동 (경로는 프로젝트에 맞게 수정하세요)
        return "redirect:/mypage/info.do"; 
    }
}