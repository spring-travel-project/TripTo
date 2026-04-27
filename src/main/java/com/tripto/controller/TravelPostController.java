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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tripto.dto.TravelPostDTO;
import com.tripto.service.CommentService;
import com.tripto.service.TravelPostService;

@Controller
public class TravelPostController {

    @Autowired
    private TravelPostService service;

    @Autowired
    private CommentService commentService;

    // 게시글 목록
    @GetMapping("/travel/list.do")
    public String list(
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String searchWord,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model) {

        TravelPostDTO dto = new TravelPostDTO();
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

        List<TravelPostDTO> list = service.list(dto);

        model.addAttribute("list", list);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("searchWord", searchWord);

        return "travel/list";
    }

    // 글쓰기 화면
    @GetMapping("/travel/write.do")
    public String write(Model model, HttpSession session) {

        // 로그인 체크 (현재 비활성화)
        /*
        if (session.getAttribute("seqMember") == null) {
            return "redirect:/member/login.do";
        }
        */

        return "travel/write";
    }

    // 글쓰기 처리
    @PostMapping("/travel/write.do")
    public String writeOk(TravelPostDTO dto,
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
            return "redirect:/travel/list.do";
        } else {
            rttr.addFlashAttribute("message", "게시글 등록 실패");
            return "redirect:/travel/write.do";
        }
    }

    // 상세보기
    @GetMapping("/travel/detail.do")
    public String detail(@RequestParam int seqTravelPost,
                         Model model,
                         HttpSession session) {

        TravelPostDTO dto = service.get(seqTravelPost, true);

        if (dto == null) {
            return "redirect:/travel/list.do";
        }

        // 테스트용
        int currentSeqMember = 1;
        boolean isAdmin = false;
        boolean isWriter = true;

        model.addAttribute("dto", dto);
        model.addAttribute("locationList", service.locationListByTravelPost(seqTravelPost));
        model.addAttribute("isWriter", isWriter);

        // 댓글
        model.addAttribute("commentList", commentService.travelList(seqTravelPost));
        model.addAttribute("currentSeqMember", currentSeqMember);
        model.addAttribute("isAdmin", isAdmin);

        return "travel/detail";
    }

    // 수정 화면
    @GetMapping("/travel/edit.do")
    public String edit(@RequestParam int seqTravelPost,
                       Model model,
                       HttpSession session,
                       RedirectAttributes rttr) {

        TravelPostDTO dto = service.get(seqTravelPost, false);

        model.addAttribute("dto", dto);

        return "travel/edit";
    }

    // 수정 처리
    @PostMapping("/travel/edit.do")
    public String editOk(TravelPostDTO dto,
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

        return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTravelPost();
    }

    // 삭제
    @PostMapping("/travel/delete.do")
    public String delete(@RequestParam int seqTravelPost,
                         RedirectAttributes rttr) {

        int result = service.delete(seqTravelPost);

        if (result == 1) {
            rttr.addFlashAttribute("message", "게시글이 삭제되었습니다.");
            return "redirect:/travel/list.do";
        } else {
            rttr.addFlashAttribute("message", "게시글 삭제 실패");
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
        }
    }

    // 이미지 업로드
    @PostMapping("/travel/imageUpload.do")
    @ResponseBody
    public Map<String, Object> imageUpload(@RequestParam("attach") MultipartFile file) {

        Map<String, Object> result = new HashMap<>();

        try {

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "df2o0mjgj",
                    "api_key", "154321363337894",
                    "api_secret", "Z3WzpCWRQ4tBgwXQ-J1lYZc44XU"
            ));

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            String url = uploadResult.get("secure_url").toString();

            result.put("url", url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}