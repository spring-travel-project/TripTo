package com.tripto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.tripto.dto.MatchDTO;
import com.tripto.service.MatchingService;

@Controller
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/matching/list")
    public String matchingList(
            @RequestParam(required = false) String mbti,
            @RequestParam(required = false) Integer smoking,
            @RequestParam(required = false) Integer drinking,
            @RequestParam(required = false) Integer travelType,
            @RequestParam(required = false) Integer stepCount,
            @RequestParam(required = false) String pic,
            HttpSession session, 
            Model model) {

		/*
		 * // 로그인 유저 PK 가져오기 Integer loginSeq = (Integer)
		 * session.getAttribute("loginSeq");
		 * 
		 * if (loginSeq == null) { return "redirect:/member/login"; }
		 */
    	
    	//  [임시 로그인 로직] 
        // 세션에 로그인 정보가 없으면 테스트를 위해 강제로 1번 유저로 세팅합니다.
        if (session.getAttribute("loginSeq") == null) {
            session.setAttribute("loginSeq", 1); // DB에 존재하는 회원 번호로 설정하세요.
        }
        
        

        // 이제 loginSeq는 무조건 존재하게 됩니다.
        Integer loginSeq = (Integer) session.getAttribute("loginSeq");
        
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
    public String matchingDetail(@RequestParam("seqMember") int targetSeq, HttpSession session, Model model) {
        
        // 1. 내 번호 세팅 (테스트용)
        Integer loginSeq = (Integer) session.getAttribute("loginSeq");
        if (loginSeq == null) {
            session.setAttribute("loginSeq", 1); 
            loginSeq = 1;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("loginSeq", loginSeq);
        map.put("targetSeq", targetSeq);

        // 2. 상대방 프로필 (+ 나와의 매칭 카운트 계산 포함) 가져오기
        MatchDTO targetProfile = matchingService.getMatchDetail(map);
        
        // 3. 내 프로필 가져오기 (JSP에서 블라인드 처리 비교용)
        MatchDTO myProfile = matchingService.getMyProfile(loginSeq);

        // 4. JSP로 전달
        model.addAttribute("target", targetProfile);
        model.addAttribute("me", myProfile);

        return "matching/detail";
    }
}