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
        
        // 1. 로그인한 내 번호 가져오기
        Integer loginSeq = (Integer) session.getAttribute("loginSeq");
        if (loginSeq == null) {
            session.setAttribute("loginSeq", 1); // 테스트용 강제 로그인
            loginSeq = 1;
        }

        // 2. 서비스로부터 내 정보와 상대방 정보를 비교 분석한 결과 가져오기
        Map<String, Integer> map = new HashMap<>();
        map.put("loginSeq", loginSeq);   // 나
        map.put("targetSeq", targetSeq); // 상대방
        
        MatchDTO matchDetail = matchingService.getMatchDetail(map);

        model.addAttribute("match", matchDetail);
        
        return "matching/detail";
    }
}