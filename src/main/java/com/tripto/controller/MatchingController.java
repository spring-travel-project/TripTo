package com.tripto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication; // 🌟 시큐리티 추가
import org.springframework.security.core.context.SecurityContextHolder; // 🌟 시큐리티 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}