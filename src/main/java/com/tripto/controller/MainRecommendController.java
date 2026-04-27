package com.tripto.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tripto.service.MainRecommendService;

@Controller
public class MainRecommendController {

    private final MainRecommendService service;

    public MainRecommendController(MainRecommendService service) {
        this.service = service;
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())
                && auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @PostMapping("/main/recommend/add.do")
    public String add(@RequestParam String targetType,
                      @RequestParam int seqTarget,
                      RedirectAttributes rttr) {

        if (!isAdmin()) {
            rttr.addFlashAttribute("message", "관리자만 사용할 수 있습니다.");
            return "redirect:/member/login.do";
        }

        service.addRecommend(targetType, seqTarget);

        rttr.addFlashAttribute("message", "메인화면에 등록되었습니다.");

        if ("TRAVEL".equals(targetType)) {
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTarget;
        } else if ("BOARD".equals(targetType)) {
            return "redirect:/board/detail.do?seqBoardPost=" + seqTarget;
        }

        return "redirect:/index.do";
    }

    @PostMapping("/main/recommend/remove.do")
    public String remove(@RequestParam String targetType,
                         @RequestParam int seqTarget,
                         RedirectAttributes rttr) {

        if (!isAdmin()) {
            rttr.addFlashAttribute("message", "관리자만 사용할 수 있습니다.");
            return "redirect:/member/login.do";
        }

        service.removeRecommend(targetType, seqTarget);

        rttr.addFlashAttribute("message", "메인화면에서 해제되었습니다.");

        if ("TRAVEL".equals(targetType)) {
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTarget;
        } else if ("BOARD".equals(targetType)) {
            return "redirect:/board/detail.do?seqBoardPost=" + seqTarget;
        }

        return "redirect:/index.do";
    }
}