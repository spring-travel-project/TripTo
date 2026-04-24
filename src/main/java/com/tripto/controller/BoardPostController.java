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

    // 紐⑸줉
    @GetMapping("/board/list.do")
    public String list(
        @RequestParam(required = false, defaultValue = "") String category,
        @RequestParam(required = false, defaultValue = "") String searchWord,
        @RequestParam(required = false, defaultValue = "1") int page,
        Model model) {
    	
    	System.out.println("=== list controller �떎�뻾 ===");

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
        int totalPage = (int)Math.ceil((double)totalCount / pageSize);

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

    // 湲��벐湲� �솕硫�
    @GetMapping("/board/write.do")
    public String write(Model model, HttpSession session) {

        // �쓬 濡쒓렇�씤 泥댄겕 (�엫�떆 鍮꾪솢�꽦�솕)
        /*
        if (session.getAttribute("seqMember") == null) {
            return "redirect:/member/login.do";
        }
        */

        model.addAttribute("categoryList", service.categoryList());
        return "board/write";
    }

    // 湲��벐湲� 泥섎━
    @PostMapping("/board/write.do")
    public String writeOk(BoardPostDTO dto,
                          HttpServletRequest req,
                          HttpSession session,
                          RedirectAttributes rttr) {

        // �쓬 濡쒓렇�씤 泥댄겕 (�엫�떆 鍮꾪솢�꽦�솕)
        /*
        Integer seqMember = (Integer) session.getAttribute("seqMember");

        if (seqMember == null) {
            return "redirect:/member/login.do";
        }

        dto.setSeqMember(seqMember);
        */

        // �쐟 �엫�떆 �궗�슜�옄 (�뀒�뒪�듃�슜)
        dto.setSeqMember(1);
        
        int result = service.add(dto, req);

        if (result == 1) {
            rttr.addFlashAttribute("message", "寃뚯떆湲��씠 �벑濡앸릺�뿀�뒿�땲�떎.");
            return "redirect:/board/list.do";
        } else {
            rttr.addFlashAttribute("message", "寃뚯떆湲� �벑濡� �떎�뙣");
            return "redirect:/board/write.do";
        }
    }

    // �긽�꽭蹂닿린
    @GetMapping("/board/detail.do")
    public String detail(@RequestParam(value = "seqBoardPost") int seqBoardPost,
                         Model model,
                         HttpSession session) {

        BoardPostDTO dto = service.get(seqBoardPost, true);

        if (dto == null) {
            return "redirect:/board/list.do";
        }

        // �쓬 濡쒓렇�씤 湲곕컲 �옉�꽦�옄 泥댄겕 (�엫�떆 鍮꾪솢�꽦�솕)
        /*
        Integer seqMember = (Integer) session.getAttribute("seqMember");
        boolean isWriter = false;

        if (seqMember != null) {
            isWriter = service.isWriter(seqBoardPost, seqMember);
        }
        */

        // �쐟 �뀒�뒪�듃�슜: �옉�꽦�옄�씪怨� 媛��젙
        int currentSeqMember = 1;
        boolean isAdmin = false;
        boolean isWriter = true;

        model.addAttribute("dto", dto);
        model.addAttribute("isWriter", isWriter);

        // 댓글 관련
        model.addAttribute("commentList", commentService.list(seqBoardPost));
        model.addAttribute("currentSeqMember", 1);
        model.addAttribute("isAdmin", false);

        return "board/detail";
    }

    // �닔�젙 �솕硫�
    @GetMapping("/board/edit.do")
    public String edit(@RequestParam int seqBoardPost,
                       Model model,
                       HttpSession session,
                       RedirectAttributes rttr) {

        // �쓬 濡쒓렇�씤 + 沅뚰븳 泥댄겕 鍮꾪솢�꽦�솕
        /*
        Integer seqMember = (Integer) session.getAttribute("seqMember");

        if (seqMember == null || !service.isWriter(seqBoardPost, seqMember)) {
            rttr.addFlashAttribute("message", "�닔�젙 沅뚰븳�씠 �뾾�뒿�땲�떎.");
            return "redirect:/board/detail.do?seqBoardPost=" + seqBoardPost;
        }
        */

        BoardPostDTO dto = service.get(seqBoardPost, false);

        model.addAttribute("dto", dto);
        model.addAttribute("categoryList", service.categoryList());

        return "board/edit";
    }

    // �닔�젙 泥섎━
    @PostMapping("/board/edit.do")
    public String editOk(BoardPostDTO dto,
                         HttpServletRequest req,
                         HttpSession session,
                         RedirectAttributes rttr) {


        // �쓬 濡쒓렇�씤 + 沅뚰븳 泥댄겕 鍮꾪솢�꽦�솕
        /*
        Integer seqMember = (Integer) session.getAttribute("seqMember");

        if (seqMember == null || !service.isWriter(dto.getSeqBoardPost(), seqMember)) {
            rttr.addFlashAttribute("message", "�닔�젙 沅뚰븳�씠 �뾾�뒿�땲�떎.");
            return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqBoardPost();
        }
        */

    	dto.setSeqMember(1);

    	int result = service.edit(dto, req);

        if (result == 1) {
            rttr.addFlashAttribute("message", "寃뚯떆湲��씠 �닔�젙�릺�뿀�뒿�땲�떎.");
        } else {
            rttr.addFlashAttribute("message", "寃뚯떆湲� �닔�젙 �떎�뙣");
        }

        return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqBoardPost();
    }

    // �궘�젣
    @PostMapping("/board/delete.do")
    public String delete(@RequestParam int seqBoardPost,
                         HttpSession session,
                         RedirectAttributes rttr) {

        // �쓬 濡쒓렇�씤 + 沅뚰븳 泥댄겕 鍮꾪솢�꽦�솕
        /*
        Integer seqMember = (Integer) session.getAttribute("seqMember");

        if (seqMember == null || !service.isWriter(seqBoardPost, seqMember)) {
            rttr.addFlashAttribute("message", "�궘�젣 沅뚰븳�씠 �뾾�뒿�땲�떎.");
            return "redirect:/board/detail.do?seqBoardPost=" + seqBoardPost;
        }
        */

        int result = service.delete(seqBoardPost);

        if (result == 1) {
            rttr.addFlashAttribute("message", "寃뚯떆湲��씠 �궘�젣�릺�뿀�뒿�땲�떎.");
            return "redirect:/board/list.do";
        } else {
            rttr.addFlashAttribute("message", "寃뚯떆湲� �궘�젣 �떎�뙣");
            return "redirect:/board/detail.do?seqBoardPost=" + seqBoardPost;
        }
    }
    
    @PostMapping("/board/imageUpload.do")
    @ResponseBody
    public Map<String, String> imageUpload(@RequestParam("file") MultipartFile file,
                                           HttpServletRequest req) {

        Map<String, String> result = new HashMap<>();

        try {
            // 狩� �빑�떖: �떎�젣 諛고룷 寃쎈줈
            String uploadPath = req.getServletContext().getRealPath("/resources/upload/board");

            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalName = file.getOriginalFilename();
            String savedName = UUID.randomUUID() + "_" + originalName;

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