package com.tripto.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // 🌟 추가: 시큐리티 인증 정보
import org.springframework.security.core.context.SecurityContextHolder; // 🌟 추가: 시큐리티 금고
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tripto.dto.MemberDTO; // 🌟 추가
import com.tripto.dto.ReportDTO;
import com.tripto.service.MemberService; // 🌟 추가: DB에서 내 번호 찾기 용도
import com.tripto.service.ReportService;

@Controller
public class ReportController {

    @Autowired
    private ReportService service;

    // 🌟 내 회원 번호를 알아내기 위해 MemberService를 주입받습니다.
    @Autowired
    private MemberService memberService; 

    @PostMapping("/report/add.do")
    public String add(ReportDTO dto, HttpSession session, RedirectAttributes rttr) {

        // 1. 스프링 시큐리티 금고에서 현재 로그인한 사람의 인증서(Authentication) 꺼내기
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. 방어 코드: 로그인이 안 되어 있거나 익명 사용자(anonymousUser)인 경우 튕겨내기
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            rttr.addFlashAttribute("message", "로그인 후 이용할 수 있는 기능입니다.");
            return "redirect:/member/login.do";
        }

        // 3. 인증서에서 로그인한 회원의 아이디(String ID) 가져오기
        String loggedInId = auth.getName(); 

        // 4. 알아낸 아이디를 이용해 DB에서 내 정보(seqMember) 가져오기
        // (태훈님이 MemberService에 만들어두신 getMemberById 메서드를 여기서 써먹습니다!)
        MemberDTO loginUser = memberService.getMemberById(loggedInId);
        
        // 혹시라도 DB에서 회원이 삭제되었거나 못 찾을 경우 방어
        if (loginUser == null) {
            rttr.addFlashAttribute("message", "회원 정보를 찾을 수 없습니다. 다시 로그인해주세요.");
            return "redirect:/member/login.do";
        }

        // 5. 드디어 찾은 진짜 내 번호(seqMember)를 세팅!
        dto.setSeqMember(loginUser.getSeqMember());

        // 6. 신고 접수 서비스 호출
        int result = service.add(dto);

        // 7. 결과 알림창 세팅
        if (result == 1) {
            rttr.addFlashAttribute("message", "신고가 정상적으로 접수되었습니다.");
        } else if (result == -1) {
            rttr.addFlashAttribute("message", "이미 신고한 내역입니다.");
        } else {
            rttr.addFlashAttribute("message", "신고 접수에 실패했습니다.");
        }

        // 8. 분기 처리 (원래 있던 곳으로 돌려보내기)
        if ("BOARD".equals(dto.getTargetType())) {
            return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqTarget();
        }

        if ("TRAVEL".equals(dto.getTargetType())) {
            return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTarget();
        }
        
        if ("USER".equals(dto.getTargetType())) {
            return "redirect:/matching/detail?seqMember=" + dto.getSeqTarget();
        }

        return "redirect:/board/list.do";
    }
}