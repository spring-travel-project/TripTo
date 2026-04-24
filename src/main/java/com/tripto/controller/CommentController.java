package com.tripto.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tripto.dto.BoardCommentDTO;
import com.tripto.dto.TravelCommentDTO;
import com.tripto.service.CommentService;

@Controller
public class CommentController {

    @Autowired
    private CommentService service;

    @PostMapping("/comment/add.do")
    public String add(BoardCommentDTO dto,
            HttpServletRequest request,
            RedirectAttributes rttr) throws Exception {
		
		request.setCharacterEncoding("UTF-8");
		
		// 임시 로그인 사용자
		dto.setSeqMember(1);
		
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

        int currentSeqMember = 1;

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

        int currentSeqMember = 1;
        boolean isAdmin = false;

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
    	
        dto.setSeqMember(1);

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

        int currentSeqMember = 1;

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

        int currentSeqMember = 1;
        boolean isAdmin = false;

        int result = service.travelDelete(seqTravelComment, currentSeqMember, isAdmin);

        if (result == 1) {
            rttr.addFlashAttribute("message", "댓글이 삭제되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "댓글 삭제 권한이 없습니다.");
        }

        return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
    }
}