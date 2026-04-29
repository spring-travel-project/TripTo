package com.tripto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.tripto.dto.MemberDTO;
import com.tripto.dto.TravelPostDTO;
import com.tripto.service.CommentService;
import com.tripto.service.MainRecommendService;
import com.tripto.service.MemberService;
import com.tripto.service.TravelPostService;
import com.tripto.service.ChatService;
import com.tripto.dto.RoutineDTO;

@Controller
public class TravelPostController {
	
	@Autowired
	private ChatService chatService;
	
    private final TravelPostService service;
    private final CommentService commentService;
    private final MemberService memberService;
    private final MainRecommendService mainRecommendService;

    public TravelPostController(TravelPostService service,
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
    @GetMapping("/travel/list.do")
    public String list(
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String searchWord,
            @RequestParam(required = false, defaultValue = "1") int page,
            Model model) {
    	
    	MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

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
        model.addAttribute("category", category);

        return "travel/list";
    }

    // 글쓰기 화면
    @GetMapping("/travel/write.do")
    public String write() {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        return "travel/write";
    }

    // 글쓰기 처리
    @PostMapping("/travel/write.do")
    public String writeOk(TravelPostDTO dto,
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
            return "redirect:/travel/list.do";
        } else {
            rttr.addFlashAttribute("message", "게시글 등록 실패");
            return "redirect:/travel/write.do";
        }
    }

    // 상세보기
    @GetMapping("/travel/detail.do")
    public String detail(@RequestParam int seqTravelPost,
                         Model model) {
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/member/login.do";
        }
    	
    	boolean isRecommended = mainRecommendService.isRecommended("TRAVEL", seqTravelPost);
    	model.addAttribute("isRecommended", isRecommended);

        TravelPostDTO dto = service.get(seqTravelPost, true);

        if (dto == null) {
            return "redirect:/travel/list.do";
        }

        MemberDTO loginMember = getLoginMember();

        boolean isLogin = loginMember != null;

        boolean isWriter = isLogin
                && Integer.valueOf(loginMember.getSeqMember()).equals(dto.getSeqMember());

        // ⭐ 관리자 판별 (시큐리티 기준)
        boolean isAdmin = false;

        List<RoutineDTO> routineLocationList =
                chatService.getRoutineLocationListByTravelPost(seqTravelPost);
        
        //디버깅용
        System.out.println("seqTravelPost = " + seqTravelPost);
        System.out.println("routineLocationList size = "
                + (routineLocationList == null ? "null" : routineLocationList.size()));
        System.out.println("routineLocationList = " + routineLocationList);
        
        
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }

        model.addAttribute("dto", dto);
        model.addAttribute("locationList", service.locationListByTravelPost(seqTravelPost));
        model.addAttribute("routineLocationList", routineLocationList);
        model.addAttribute("commentList", commentService.travelList(seqTravelPost));

        model.addAttribute("currentSeqMember", isLogin ? loginMember.getSeqMember() : null);
        model.addAttribute("isLogin", isLogin);
        model.addAttribute("isWriter", isWriter);
        model.addAttribute("isAdmin", isAdmin);

        return "travel/detail";
    }

    // 수정 화면
    @GetMapping("/travel/edit.do")
    public String edit(@RequestParam int seqTravelPost,
                       Model model,
                       RedirectAttributes rttr) {

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        TravelPostDTO dto = service.get(seqTravelPost, false);

        if (dto == null) {
            return "redirect:/travel/list.do";
        }

        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(dto.getSeqMember());
        boolean isAdmin = "ROLE_ADMIN".equals(loginMember.getGrade());

        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
        }

        model.addAttribute("dto", dto);

        return "travel/edit";
    }

    // 수정 처리
    @PostMapping("/travel/edit.do")
    public String editOk(TravelPostDTO dto,
                         HttpServletRequest req,
                         RedirectAttributes rttr) throws Exception {

        req.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        TravelPostDTO originDto = service.get(dto.getSeqTravelPost(), false);

        if (originDto == null) {
            return "redirect:/travel/list.do";
        }

        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(originDto.getSeqMember());
        boolean isAdmin = "ROLE_ADMIN".equals(loginMember.getGrade());

        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTravelPost();
        }

        dto.setSeqMember(loginMember.getSeqMember());

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

        MemberDTO loginMember = getLoginMember();

        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        TravelPostDTO dto = service.get(seqTravelPost, false);

        if (dto == null) {
            return "redirect:/travel/list.do";
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
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
        }

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