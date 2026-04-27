package com.tripto.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tripto.dto.BoardCommentDTO;
import com.tripto.dto.MemberDTO;
import com.tripto.dto.TravelCommentDTO;
import com.tripto.service.CommentService;
import com.tripto.service.MemberService;

@Controller
public class CommentController {

    private final CommentService service;
    private final MemberService memberService;

    public CommentController(CommentService service, MemberService memberService) {
        this.service = service;
        this.memberService = memberService;
    }

    private MemberDTO getLoginMember() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String loggedInId = auth.getName();

        return memberService.getMemberById(loggedInId);
    }

    @PostMapping("/comment/add.do")
    public String add(BoardCommentDTO dto,
                      HttpServletRequest request,
                      RedirectAttributes rttr) throws Exception {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        dto.setSeqMember(loginMember.getSeqMember());

        int result = service.add(dto);

        if (result == 1) {
            rttr.addFlashAttribute("message", "댓글이 등록되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "댓글 등록 실패");
        }

        return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqBoardPost();
    }

    @PostMapping("/comment/edit.do")
    public String edit(BoardCommentDTO dto,
                       HttpServletRequest request,
                       RedirectAttributes rttr) throws Exception {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int currentSeqMember = loginMember.getSeqMember();

        int result = service.edit(dto, currentSeqMember);

        if (result == 1) {
            rttr.addFlashAttribute("message", "댓글이 수정되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "댓글 수정 권한이 없습니다.");
        }

        return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqBoardPost();
    }

    @PostMapping("/comment/delete.do")
    public String delete(int seqBoardComment,
                         int seqBoardPost,
                         RedirectAttributes rttr) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int currentSeqMember = loginMember.getSeqMember();
        boolean isAdmin = "ADMIN".equals(loginMember.getGrade());

        int result = service.delete(seqBoardComment, currentSeqMember, isAdmin);

        if (result == 1) {
            rttr.addFlashAttribute("message", "댓글이 삭제되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "댓글 삭제 권한이 없습니다.");
        }

        return "redirect:/board/detail.do?seqBoardPost=" + seqBoardPost;
    }

    @PostMapping("/travel/comment/add.do")
    public String travelAdd(TravelCommentDTO dto,
                            HttpServletRequest request,
                            RedirectAttributes rttr) throws Exception {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        dto.setSeqMember(loginMember.getSeqMember());

        int result = service.travelAdd(dto);

        if (result == 1) {
            rttr.addFlashAttribute("message", "댓글이 등록되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "댓글 등록 실패");
        }

        String referer = request.getHeader("Referer");

        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }

        return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTravelPost();
    }

    @PostMapping("/travel/comment/edit.do")
    public String travelEdit(TravelCommentDTO dto,
                             HttpServletRequest request,
                             RedirectAttributes rttr) throws Exception {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int currentSeqMember = loginMember.getSeqMember();

        int result = service.travelEdit(dto, currentSeqMember);

        if (result == 1) {
            rttr.addFlashAttribute("message", "댓글이 수정되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "댓글 수정 권한이 없습니다.");
        }

        return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTravelPost();
    }

    @PostMapping("/travel/comment/delete.do")
    public String travelDelete(int seqTravelComment,
                               int seqTravelPost,
                               RedirectAttributes rttr) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        int currentSeqMember = loginMember.getSeqMember();
        boolean isAdmin = "ADMIN".equals(loginMember.getGrade());

        int result = service.travelDelete(seqTravelComment, currentSeqMember, isAdmin);

        if (result == 1) {
            rttr.addFlashAttribute("message", "댓글이 삭제되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "댓글 삭제 권한이 없습니다.");
        }

        return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
    }
}