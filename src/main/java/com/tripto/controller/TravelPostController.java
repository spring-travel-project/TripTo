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
import com.tripto.dto.MemberDTO;
import com.tripto.dto.TravelPostDTO;
import com.tripto.service.CommentService;
import com.tripto.service.MainRecommendService;
import com.tripto.service.MemberService;
import com.tripto.service.TravelPostService;

@Controller
public class TravelPostController {

    // 여행 게시글 관련 비즈니스 로직을 처리하는 서비스
    private final TravelPostService service;

    // 댓글 목록 조회 등 댓글 관련 기능을 처리하는 서비스
    private final CommentService commentService;

    // 로그인 회원 정보 조회를 위한 서비스
    private final MemberService memberService;

    // 메인 추천 여부를 확인하기 위한 서비스
    private final MainRecommendService mainRecommendService;

    // 생성자 주입
    // Spring이 TravelPostController를 만들 때 필요한 Service 객체들을 자동으로 넣어준다.
    public TravelPostController(TravelPostService service,
                                CommentService commentService,
                                MemberService memberService,
                                MainRecommendService mainRecommendService) {
        this.service = service;
        this.commentService = commentService;
        this.memberService = memberService;
        this.mainRecommendService = mainRecommendService;
    }

    // 현재 로그인한 회원 정보를 가져오는 공통 메서드
    // 로그인하지 않은 상태라면 null을 반환한다.
    private MemberDTO getLoginMember() {

        // Spring Security에 저장된 현재 인증 정보를 가져온다.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나, 인증되지 않았거나, 익명 사용자라면 로그인하지 않은 상태로 판단한다.
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        // 로그인한 사용자의 아이디를 가져온다.
        String loggedInId = auth.getName();

        // 아이디를 기준으로 DB에서 회원 정보를 조회한다.
        return memberService.getMemberById(loggedInId);
    }
    
    // 게시글 내용(content) 안에 포함된 첫 번째 이미지 URL을 추출하는 메서드
    // 썸네일 이미지로 사용할 목적으로 사용된다.
    private String extractFirstImageUrl(String content) {

        // 게시글 내용이 없으면 추출할 이미지도 없으므로 null 반환
        if (content == null) {
            return null;
        }

        // HTML img 태그 안의 src 값을 찾기 위한 정규식
        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(content);

        // 첫 번째 이미지 태그가 발견되면 src 주소만 반환한다.
        if (matcher.find()) {
            return matcher.group(1);
        }

        // 이미지가 없는 경우 null 반환
        return null;
    }

    // 게시글 목록 화면
    @GetMapping("/travel/list.do")
    public String list(
            // 카테고리 검색 조건
            @RequestParam(required = false, defaultValue = "") String category,

            // 검색어
            @RequestParam(required = false, defaultValue = "") String searchWord,

            // 현재 페이지 번호
            @RequestParam(required = false, defaultValue = "1") int page,

            // JSP로 데이터를 전달하기 위한 객체
            Model model) {
    	
        // 현재 로그인한 회원 정보 조회
    	MemberDTO loginMember = getLoginMember();

        // 로그인하지 않은 사용자는 로그인 화면으로 이동
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        // 검색 조건과 페이징 정보를 담을 DTO 생성
        TravelPostDTO dto = new TravelPostDTO();
        dto.setCategory(category);
        dto.setSearchWord(searchWord);
        dto.setPage(page);

        // 한 페이지에 보여줄 게시글 수
        int pageSize = 10;

        // 현재 페이지에서 조회할 시작 row 번호
        int begin = ((page - 1) * pageSize) + 1;

        // 현재 페이지에서 조회할 마지막 row 번호
        int end = begin + pageSize - 1;

        // DTO에 페이징 범위 저장
        dto.setBegin(begin);
        dto.setEnd(end);

        // 검색 조건에 맞는 전체 게시글 개수 조회
        int totalCount = service.getTotalCount(dto);

        // 전체 페이지 수 계산
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);

        // 현재 페이지에 보여줄 게시글 목록 조회
        List<TravelPostDTO> list = service.list(dto);

        // JSP에서 사용할 데이터 저장
        model.addAttribute("list", list);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("category", category);

        // /WEB-INF/views/travel/list.jsp 화면으로 이동
        return "travel/list";
    }

    // 글쓰기 화면
    @GetMapping("/travel/write.do")
    public String write() {

        // 현재 로그인한 회원 정보 조회
        MemberDTO loginMember = getLoginMember();

        // 로그인하지 않은 사용자는 글쓰기 화면 접근 불가
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        // /WEB-INF/views/travel/write.jsp 화면으로 이동
        return "travel/write";
    }

    // 글쓰기 처리
    @PostMapping("/travel/write.do")
    public String writeOk(TravelPostDTO dto,
                          HttpServletRequest req,
                          RedirectAttributes rttr) {

        // 현재 로그인한 회원 정보 조회
        MemberDTO loginMember = getLoginMember();

        // 로그인하지 않은 사용자는 글 등록 불가
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        // 게시글 작성자를 현재 로그인 회원 번호로 설정
        dto.setSeqMember(loginMember.getSeqMember());

        // 게시글 내용에서 첫 번째 이미지 URL을 추출해 썸네일로 저장
        dto.setThumbnailUrl(extractFirstImageUrl(dto.getContent()));

        // 게시글 등록 처리
        int result = service.add(dto, req);

        // 등록 성공
        if (result == 1) {
            rttr.addFlashAttribute("message", "게시글이 등록되었습니다.");
            return "redirect:/travel/list.do";

        // 등록 실패
        } else {
            rttr.addFlashAttribute("message", "게시글 등록 실패");
            return "redirect:/travel/write.do";
        }
    }

    // 상세보기
    @GetMapping("/travel/detail.do")
    public String detail(@RequestParam int seqTravelPost,
                         Model model) {
    	
        // 현재 인증 정보 조회
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 로그인하지 않은 사용자는 상세보기 접근 불가
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/member/login.do";
        }
    	
        // 해당 여행 게시글이 메인 추천 게시글인지 확인
    	boolean isRecommended = mainRecommendService.isRecommended("TRAVEL", seqTravelPost);

        // JSP에서 추천 여부를 사용할 수 있도록 전달
    	model.addAttribute("isRecommended", isRecommended);

        // 게시글 상세 정보 조회
        // true는 조회수 증가 여부로 사용되는 값일 가능성이 높다.
        TravelPostDTO dto = service.get(seqTravelPost, true);

        // 게시글이 없으면 목록으로 이동
        if (dto == null) {
            return "redirect:/travel/list.do";
        }

        // 현재 로그인한 회원 정보 조회
        MemberDTO loginMember = getLoginMember();

        // 로그인 여부 저장
        boolean isLogin = loginMember != null;

        // 현재 로그인한 사용자가 이 게시글의 작성자인지 확인
        boolean isWriter = isLogin
                && Integer.valueOf(loginMember.getSeqMember()).equals(dto.getSeqMember());

        // 관리자 여부 기본값
        boolean isAdmin = false;

        // Spring Security 권한 목록 안에 ROLE_ADMIN이 있는지 확인
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }

        // JSP에서 사용할 게시글 정보 전달
        model.addAttribute("dto", dto);

        // 해당 게시글에 연결된 장소 목록 전달
        model.addAttribute("locationList", service.locationListByTravelPost(seqTravelPost));

        // 해당 게시글의 댓글 목록 전달
        model.addAttribute("commentList", commentService.travelList(seqTravelPost));

        // 현재 로그인한 회원 번호 전달
        model.addAttribute("currentSeqMember", isLogin ? loginMember.getSeqMember() : null);

        // 로그인 여부 전달
        model.addAttribute("isLogin", isLogin);

        // 작성자 여부 전달
        model.addAttribute("isWriter", isWriter);

        // 관리자 여부 전달
        model.addAttribute("isAdmin", isAdmin);

        // /WEB-INF/views/travel/detail.jsp 화면으로 이동
        return "travel/detail";
    }

    // 수정 화면
    @GetMapping("/travel/edit.do")
    public String edit(@RequestParam int seqTravelPost,
                       Model model,
                       RedirectAttributes rttr) {

        // 현재 로그인한 회원 정보 조회
        MemberDTO loginMember = getLoginMember();

        // 로그인하지 않은 사용자는 수정 화면 접근 불가
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        // 수정할 게시글 정보 조회
        // false는 조회수 증가 없이 조회하려는 목적일 가능성이 높다.
        TravelPostDTO dto = service.get(seqTravelPost, false);

        // 게시글이 없으면 목록으로 이동
        if (dto == null) {
            return "redirect:/travel/list.do";
        }

        // 현재 로그인한 사용자가 작성자인지 확인
        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(dto.getSeqMember());

        // 현재 로그인한 사용자가 관리자인지 확인
        boolean isAdmin = "ROLE_ADMIN".equals(loginMember.getGrade());

        // 작성자도 아니고 관리자도 아니면 수정 권한 없음
        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
        }

        // 수정 화면에 기존 게시글 정보를 전달
        model.addAttribute("dto", dto);

        // /WEB-INF/views/travel/edit.jsp 화면으로 이동
        return "travel/edit";
    }

    // 수정 처리
    @PostMapping("/travel/edit.do")
    public String editOk(TravelPostDTO dto,
                         HttpServletRequest req,
                         RedirectAttributes rttr) throws Exception {

        // 요청 데이터의 문자 인코딩을 UTF-8로 설정
        req.setCharacterEncoding("UTF-8");

        // 현재 로그인한 회원 정보 조회
        MemberDTO loginMember = getLoginMember();

        // 로그인하지 않은 사용자는 수정 처리 불가
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        // 기존 게시글 정보 조회
        TravelPostDTO originDto = service.get(dto.getSeqTravelPost(), false);

        // 기존 게시글이 없으면 목록으로 이동
        if (originDto == null) {
            return "redirect:/travel/list.do";
        }

        // 현재 로그인한 사용자가 기존 게시글 작성자인지 확인
        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(originDto.getSeqMember());

        // 현재 로그인한 사용자가 관리자인지 확인
        boolean isAdmin = "ROLE_ADMIN".equals(loginMember.getGrade());

        // 작성자도 아니고 관리자도 아니면 수정 불가
        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTravelPost();
        }

        // 수정 요청 DTO의 회원 번호를 현재 로그인 회원 번호로 설정
        dto.setSeqMember(loginMember.getSeqMember());

        // 게시글 수정 처리
        int result = service.edit(dto, req);

        // 수정 성공 여부에 따라 메시지 저장
        if (result == 1) {
            rttr.addFlashAttribute("message", "게시글이 수정되었습니다.");
        } else {
            rttr.addFlashAttribute("message", "게시글 수정 실패");
        }

        // 수정 후 상세보기 화면으로 이동
        return "redirect:/travel/detail.do?seqTravelPost=" + dto.getSeqTravelPost();
    }

    // 삭제
    @PostMapping("/travel/delete.do")
    public String delete(@RequestParam int seqTravelPost,
                         RedirectAttributes rttr) {

        // 현재 로그인한 회원 정보 조회
        MemberDTO loginMember = getLoginMember();

        // 로그인하지 않은 사용자는 삭제 불가
        if (loginMember == null) {
            return "redirect:/member/login.do";
        }

        // 삭제 대상 게시글 조회
        TravelPostDTO dto = service.get(seqTravelPost, false);

        // 게시글이 없으면 목록으로 이동
        if (dto == null) {
            return "redirect:/travel/list.do";
        }

        // 현재 로그인한 사용자가 게시글 작성자인지 확인
        boolean isWriter = Integer.valueOf(loginMember.getSeqMember()).equals(dto.getSeqMember());

        // 현재 인증 정보 조회
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Spring Security 권한 기준으로 관리자 여부 확인
        boolean isAdmin = auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())
                && auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        // 작성자도 아니고 관리자도 아니면 삭제 불가
        if (!isWriter && !isAdmin) {
            rttr.addFlashAttribute("message", "삭제 권한이 없습니다.");
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
        }

        // 게시글 삭제 처리
        int result = service.delete(seqTravelPost);

        // 삭제 성공
        if (result == 1) {
            rttr.addFlashAttribute("message", "게시글이 삭제되었습니다.");
            return "redirect:/travel/list.do";

        // 삭제 실패
        } else {
            rttr.addFlashAttribute("message", "게시글 삭제 실패");
            return "redirect:/travel/detail.do?seqTravelPost=" + seqTravelPost;
        }
    }

    // 이미지 업로드
    @PostMapping("/travel/imageUpload.do")
    @ResponseBody
    public Map<String, Object> imageUpload(@RequestParam("attach") MultipartFile file) {

        // Ajax 응답으로 반환할 Map
        Map<String, Object> result = new HashMap<>();

        // 현재 로그인한 회원 정보 조회
        MemberDTO loginMember = getLoginMember();

        // 로그인하지 않은 사용자는 이미지 업로드 불가
        if (loginMember == null) {
            result.put("error", "login");
            return result;
        }

        try {
            // Cloudinary 이미지 업로드를 위한 객체 생성
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "df2o0mjgj",
                    "api_key", "154321363337894",
                    "api_secret", "Z3WzpCWRQ4tBgwXQ-J1lYZc44XU"
            ));

            // 업로드된 MultipartFile을 byte 배열로 변환해서 Cloudinary에 업로드
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            // 업로드 결과에서 보안 URL 추출
            String url = uploadResult.get("secure_url").toString();

            // 클라이언트에게 이미지 URL 반환
            result.put("url", url);

        } catch (Exception e) {
            // 업로드 중 오류 발생 시 콘솔에 오류 출력
            e.printStackTrace();

            // 클라이언트에게 업로드 실패 응답 반환
            result.put("error", "upload");
        }

        // Ajax 응답으로 Map 반환
        return result;
    }
}