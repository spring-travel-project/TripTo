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

    @GetMapping("/matching/list.do")
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
		 * // �α��� ���� PK �������� Integer loginSeq = (Integer)
		 * session.getAttribute("loginSeq");
		 * 
		 * if (loginSeq == null) { return "redirect:/member/login"; }
		 */
    	
    	//  [�ӽ� �α��� ����] 
        // ���ǿ� �α��� ������ ������ �׽�Ʈ�� ���� ������ 1�� ������ �����մϴ�.
        if (session.getAttribute("loginSeq") == null) {
            session.setAttribute("loginSeq", 1); // DB�� �����ϴ� ȸ�� ��ȣ�� �����ϼ���.
        }
        
        

        // ���� loginSeq�� ������ �����ϰ� �˴ϴ�.
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
        
        // 1. �� ��ȣ ���� (�׽�Ʈ��)
        Integer loginSeq = (Integer) session.getAttribute("loginSeq");
        if (loginSeq == null) {
            session.setAttribute("loginSeq", 1); 
            loginSeq = 1;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("loginSeq", loginSeq);
        map.put("targetSeq", targetSeq);

        // 2. ���� ������ (+ ������ ��Ī ī��Ʈ ��� ����) ��������
        MatchDTO targetProfile = matchingService.getMatchDetail(map);
        
        // 3. �� ������ �������� (JSP���� ����ε� ó�� �񱳿�)
        MatchDTO myProfile = matchingService.getMyProfile(loginSeq);

        // 4. JSP�� ����
        model.addAttribute("target", targetProfile);
        model.addAttribute("me", myProfile);

        return "matching/detail";
    }
}