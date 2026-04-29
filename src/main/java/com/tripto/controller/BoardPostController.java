package com.tripto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.tripto.dto.BoardCategoryDTO;
import com.tripto.dto.BoardPostDTO;
import com.tripto.dto.MemberDTO;
import com.tripto.service.BoardPostService;
import com.tripto.service.CommentService;
import com.tripto.service.MainRecommendService;
import com.tripto.service.MemberService;

@Controller
public class BoardPostController {

    private final BoardPostService service;
    private final CommentService commentService;
    private final MemberService memberService;
    private final MainRecommendService mainRecommendService;

    public BoardPostController(BoardPostService service,
                               CommentService commentService,
                               MemberService memberService,
                               MainRecommendService mainRecommendService) {
        this.service = service;
        this.commentService = commentService;
        this.memberService = memberService;
        this.mainRecommendService = mainRecommendService;
    }

    private MemberDTO getLoginMember() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String loggedInId = auth.getName();

        return memberService.getMemberById(loggedInId);
    }
    
    private String extractFirstImageUrl(String content) {

        if (content == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    // 게시글 목록
    @GetMapping("/board/list.do")
    public String list(
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String searchWord,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model) {
    	
    	MemberDTO loginMember = getLoginMember();
    	
    	if (loginMember == null) {
            return "redirect:/member/login.do";
        }

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
    public String write(Model model) {

        MemberDTO loginMember = getLoginMember();
        
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        model.addAttribute("categoryList", service.categoryList());

        return "board/write";
    }

    // 글쓰기 처리
    @PostMapping("/board/write.do")
    public String writeOk(BoardPostDTO dto,
                          HttpServletRequest req,
                          RedirectAttributes rttr) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        dto.setSeqMember(loginMember.getSeqMember());
        dto.setThumbnailUrl(extractFirstImageUrl(dto.getContent()));

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
                         Model model) {
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/member/login.do";
        }

        BoardPostDTO dto = service.get(seqBoardPost, true);

        if (dto == null) {
            return "redirect:/board/list.do";
        }

        MemberDTO loginMember = getLoginMember();

        boolean isLogin = loginMember != null;

        boolean isWriter = isLogin
                && loginMember.getSeqMember() == dto.getSeqMember();

		/*
		 * 컨트롤러와 구조가 달라서 에러 발생 (DB에서 조회하기에 보안적으로 문제 X)
		 * boolean isAdmin = auth != null && auth.isAuthenticated() &&
		 * !"anonymousUser".equals(auth.getPrincipal()) &&
		 * auth.getAuthorities().stream() .anyMatch(a ->
		 * "ROLE_ADMIN".equals(a.getAuthority()));   
		 */
        
        boolean isAdmin = isLogin && loginMember.getType() == 1;

        boolean isRecommended = mainRecommendService.isRecommended("BOARD", seqBoardPost);

        model.addAttribute("dto", dto);
        model.addAttribute("commentList", commentService.list(seqBoardPost));

        model.addAttribute("isLogin", isLogin);
        model.addAttribute("isWriter", isWriter);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentSeqMember", isLogin ? loginMember.getSeqMember() : null);
        model.addAttribute("isRecommended", isRecommended);
        
        System.out.println("login seq = " + (loginMember != null ? loginMember.getSeqMember() : null));
        System.out.println("post writer seq = " + dto.getSeqMember());
        System.out.println("isWriter = " + isWriter);

        return "board/detail";
    }

    // 수정 화면
    @GetMapping("/board/edit.do")
    public String edit(@RequestParam int seqBoardPost,
                       Model model,
                       RedirectAttributes rttr) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        BoardPostDTO dto = service.get(seqBoardPost, false);

        if (dto == null) {
            return "redirect:/board/list.do";
        }

        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(dto.getSeqMember());
        boolean isAdmin = "ROLE_ADMIN".equals(loginMember.getGrade());

        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/board/detail.do?seqBoardPost=" + seqBoardPost;
        }

        model.addAttribute("dto", dto);
        model.addAttribute("categoryList", service.categoryList());

        return "board/edit";
    }

    // 수정 처리
    @PostMapping("/board/edit.do")
    public String editOk(BoardPostDTO dto,
                         HttpServletRequest req,
                         RedirectAttributes rttr) throws Exception {

        req.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        BoardPostDTO originDto = service.get(dto.getSeqBoardPost(), false);

        if (originDto == null) {
            return "redirect:/board/list.do";
        }

        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(originDto.getSeqMember());
        boolean isAdmin = "ROLE_ADMIN".equals(loginMember.getGrade());

        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/board/detail.do?seqBoardPost=" + dto.getSeqBoardPost();
        }

        dto.setSeqMember(loginMember.getSeqMember());
        dto.setThumbnailUrl(extractFirstImageUrl(dto.getContent()));

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

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        BoardPostDTO dto = service.get(seqBoardPost, false);

        if (dto == null) {
            return "redirect:/board/list.do";
        }

        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(dto.getSeqMember());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())
                && auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "삭제 권한이 없습니다.");
            return "redirect:/board/detail.do?seqBoardPost=" + seqBoardPost;
        }

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
    public Map<String, Object> imageUpload(@RequestParam("attach") MultipartFile file) {

        Map<String, Object> result = new HashMap<>();

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            result.put("error", "login");
            return result;
        }

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
            result.put("error", "upload");
        }

        return result;
    }
}