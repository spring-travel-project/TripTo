package com.tripto.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tripto.dto.ReportDTO;
import com.tripto.service.ReportService;

@Controller
public class ReportController {

    @Autowired
    private ReportService service;

    @PostMapping("/report/add.do")
    public String add(ReportDTO dto, HttpSession session, RedirectAttributes rttr) {

        Integer seqMember = (Integer) session.getAttribute("seqMember");

        if (seqMember == null) {
            return "redirect:/member/login.do";
        }

        dto.setSeqMember(seqMember);

        int result = service.add(dto);

        if (result == 1) {
            rttr.addFlashAttribute("message", "신고가 접수되었습니다.");
        } else if (result == -1) {
            rttr.addFlashAttribute("message", "이미 신고한 게시글입니다.");
        } else {
            rttr.addFlashAttribute("message", "신고 접수 실패");
        }

        if ("BOARD".equals(dto.getTargetType())) {
            return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqTarget();
        }

        if ("TRAVEL".equals(dto.getTargetType())) {
            return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTarget();
        }

        return "redirect:/board/list.do";
    }
}