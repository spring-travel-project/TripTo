package com.tripto.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tripto.dto.BoardCategoryDTO;
import com.tripto.dto.BoardPostDTO;
import com.tripto.service.BoardPostService;
import com.tripto.service.CommentService;

@Controller
public class BoardPostController {

    @Autowired
    private BoardPostService service;

    @Autowired
    private CommentService commentService;

    // 게시글 목록
    @GetMapping("/board/list.do")
    public String list(
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String searchWord,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model) {

        System.out.println("=== list controller 실행 ===");

        BoardPostDTO dto = new BoardPostDTO();
        dto.setCategory(category);
        dto.setSearchWord(searchWord);
        dto.setPage(page);

        int pageSize = 10;
        int begin = ((page - 1) * pageSize) + 1;
        int end = begin + pageSize - 1;

        dto.setBegin(begin);
        dto.setEnd(end);

        int totalCount = service.getTotalCount(dto);
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);

        List<BoardPostDTO> list = service.list(dto);
        List<BoardCategoryDTO> categoryList = service.categoryList();

        model.addAttribute("list", list);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("category", category);
        model.addAttribute("searchWord", searchWord);

        return "board/list";
    }

    // 글쓰기 화면
    @GetMapping("/board/write.do")
    public String write(Model model, HttpSession session) {

        // 로그인 체크 (현재 비활성화)
        /*
        if (session.getAttribute("seqMember") == null) {
            return "redirect:/member/login.do";
        }
        */

        model.addAttribute("categoryList", service.categoryList());
        return "board/write";
    }

    // 글쓰기 처리
    @PostMapping("/board/write.do")
    public String writeOk(BoardPostDTO dto,
                          HttpServletRequest req,
                          HttpSession session,
                          RedirectAttributes rttr) {

        // 로그인 체크 (현재 비활성화)
        /*
        Integer seqMember = (Integer) session.getAttribute("seqMember");

        if (seqMember == null) {
            return "redirect:/member/login.do";
        }

        dto.setSeqMember(seqMember);
        */

        // 테스트용 사용자
        dto.setSeqMember(1);

        int result = service.add(dto, req);

        if (result == 1) {
            rttr.addFlashAttribute("message", "게시글이 등록되었습니다.");
            return "redirect:/board/list.do";
        } else {
            rttr.addFlashAttribute("message", "게시글 등록 실패");
            return "redirect:/board/write.do";
        }
    }

    // 상세보기
    @GetMapping("/board/detail.do")
    public String detail(@RequestParam int seqBoardPost,
                         Model model,
                         HttpSession session) {

        BoardPostDTO dto = service.get(seqBoardPost, true);

        if (dto == null) {
            return "redirect:/board/list.do";
        }

        // 테스트용
        int currentSeqMember = 1;
        boolean isAdmin = false;
        boolean isWriter = true;

        model.addAttribute("dto", dto);
        model.addAttribute("isWriter", isWriter);

        // 댓글
        model.addAttribute("commentList", commentService.list(seqBoardPost));
        model.addAttribute("currentSeqMember", currentSeqMember);
        model.addAttribute("isAdmin", isAdmin);

        return "board/detail";
    }

    // 수정 화면
    @GetMapping("/board/edit.do")
    public String edit(@RequestParam int seqBoardPost,
                       Model model,
                       HttpSession session,
                       RedirectAttributes rttr) {

        BoardPostDTO dto = service.get(seqBoardPost, false);

        model.addAttribute("dto", dto);
        model.addAttribute("categoryList", service.categoryList());

        return "board/edit";
    }

    // 수정 처리
    @PostMapping("/board/edit.do")
    public String editOk(BoardPostDTO dto,
                         HttpServletRequest req,
                         HttpSession session,
                         RedirectAttributes rttr) throws Exception {

        req.setCharacterEncoding("UTF-8");

        dto.setSeqMember(1);

        int result = service.edit(dto, req);

        if (result == 1) {
            rttr.addFlashAttribute("message", "게시글이 수정되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "게시글 수정 실패");
        }

        return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqBoardPost();
    }

    // 삭제
    @PostMapping("/board/delete.do")
    public String delete(@RequestParam int seqBoardPost,
                         RedirectAttributes rttr) {

        int result = service.delete(seqBoardPost);

        if (result == 1) {
            rttr.addFlashAttribute("message", "게시글이 삭제되었습니다.");
            return "redirect:/board/list.do";
        } else {
            rttr.addFlashAttribute("message", "게시글 삭제 실패");
            return "redirect:/board/detail.do?seqBoardPost=" + seqBoardPost;
        }
    }

    // 이미지 업로드
    @PostMapping("/board/imageUpload.do")
    @ResponseBody
    public Map<String, String> imageUpload(@RequestParam("file") MultipartFile file,
                                           HttpServletRequest req) {

        Map<String, String> result = new HashMap<>();

        try {
            String uploadPath = req.getServletContext().getRealPath("/resources/upload/board");

            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalName = file.getOriginalFilename();

            String ext = "";
            if (originalName != null && originalName.lastIndexOf(".") != -1) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }

            String savedName = UUID.randomUUID().toString() + ext;

            File target = new File(dir, savedName);
            file.transferTo(target);

            String url = req.getContextPath() + "/resources/upload/board/" + savedName;
            result.put("url", url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}