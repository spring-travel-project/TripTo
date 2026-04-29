package com.tripto.controller;

import java.io.File;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tripto.dto.MatchDTO;
import com.tripto.dto.MemberDTO;
import com.tripto.dto.MyActivityDTO;
import com.tripto.service.MatchingService;
import com.tripto.service.MemberService;

@Controller
@RequestMapping("/member")
public class MyPageController {

	@Autowired
	private MemberService memberService;

	// 프로필 정보를 가져오는 로직을 사용하기 위해 의존 주입
	@Autowired
	private MatchingService matchingService;

	// 비밀번호 검증을 위해 의존 주입
	@Autowired
	private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

	// 1. 마이페이지에 현재 로그인한 회원의 정보를 출력
	@GetMapping("/mypage.do")
	public String mypage(Principal principal, Model model) {

		// 1) 시큐리티를 통해 현재 로그인한 사용자의 아이디(id)를 가져옴
		String id = principal.getName();

		// 2) 회원 기본 정보(MemberDTO) 가져오기
		MemberDTO member = memberService.getMemberById(id);

		// 3) 연령대 계산 로직 (birth: "2001-01-06" 형태)
		String ageGroup = "비공개";
		if (member.getBirth() != null && member.getBirth().length() >= 4) {
			int birthYear = Integer.parseInt(member.getBirth().substring(0, 4));
			int currentYear = LocalDate.now().getYear();
			int age = currentYear - birthYear;

			// 25살 -> 20, 31살 -> 30으로 계산
			int ageGroupNum = (age / 10) * 10;
			ageGroup = ageGroupNum + "대";
		}

		// 4) 프로필 상세 정보(MatchDTO) 가져오기
		// (프로필 작성을 안 한 회원이면 null이 들어감)
		MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());

		// 5) 화면(JSP)으로 데이터 전송
		model.addAttribute("member", member);
		model.addAttribute("ageGroup", ageGroup);
		model.addAttribute("profile", profile);

		return "member/mypage";
	}

	// 2-1. 비밀번호 변경 - 현재 비밀번호 확인 (AJAX)
	@PostMapping("/checkCurrentPw.do")
	@ResponseBody
	public String checkCurrentPw(@RequestParam("currentPw") String currentPw, Principal principal) {
		// DB에 저장된 진짜 암호화된 비밀번호 가져오기
		String dbPw = memberService.getCurrentPw(principal.getName());

		// passwordEncoder.matches(입력한 암호화 전 비밀번호, DB의 암호화된 비밀번호)로 비교
		if (passwordEncoder.matches(currentPw, dbPw)) {
			return "MATCH";
		}
		return "MISMATCH";
	}

	// 2-2. 비밀번호 변경 - 새 비밀번호로 변경
	@PostMapping("/changePw.do")
	public String changePw(@RequestParam("newPw") String newPw, Principal principal,
			javax.servlet.http.HttpServletRequest request) { // request 객체 추가
		String encodedPw = passwordEncoder.encode(newPw);

		Map<String, String> map = new HashMap<>();
		map.put("id", principal.getName());
		map.put("pw", encodedPw);

		// 기존에 만들어둔 비밀번호 업데이트 메서드 재활용
		memberService.updatePw(map);

		// 스프링 시큐리티 문법으로 서버 단에서 강제 로그아웃 시키기
		try {
			request.logout();
		} catch (javax.servlet.ServletException e) {
			e.printStackTrace();
		}

		// 로그아웃이 완료되었으니, 로그인 페이지로 돌려보냄
		return "redirect:/member/login.do";
	}

	// 3. 계정 탈퇴 (비식별화)
	@PostMapping("/deactivate.do")
	public String deactivate(Principal principal, javax.servlet.http.HttpServletRequest request)
			throws javax.servlet.ServletException {
		// 비식별화 쿼리 실행
		memberService.deactivateMember(principal.getName());

		// 스프링 시큐리티 강제 로그아웃 처리
		request.logout();

		return "redirect:/index.do";
	}

	// 4. 프로필 미작성자는 프로필 작성 안내 페이지로 이동
	@GetMapping("/needProfile.do")
	public String needProfile() {
		return "member/needProfile";
	}

	// 5-1. 내 정보 수정 화면 띄우기 (GET)
	@GetMapping("/editInfo.do")
	public String infoEdit(Principal principal, Model model) {
		// 로그인한 유저 아이디로 정보 가져와서 화면(editInfo.jsp)에 출력
		String id = principal.getName();
		MemberDTO member = memberService.getMemberById(id);
		model.addAttribute("member", member);

		return "member/editInfo";
	}

	// 5-2. 폼 제출: 내 정보 수정 완료 처리 (POST)
	@PostMapping("/editInfo.do")
	public String infoEditComplete(MemberDTO dto, @RequestParam("picFile") MultipartFile picFile, Principal principal) {

		dto.setId(principal.getName());


		if (picFile != null && !picFile.isEmpty()) {
			try {
				// 🌟 클라우드 설정 (태훈님 키 입력!)
				Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "dh5p4lvo2", "api_key",
						"283127846695383", "api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k", "secure", true));

				// 🌟 업로드 후 URL 받기
				Map uploadResult = cloudinary.uploader().upload(picFile.getBytes(), ObjectUtils.emptyMap());
				String imageUrl = (String) uploadResult.get("secure_url");

				dto.setPic(imageUrl); // DB에는 이제 URL 주소가 저장됩니다.
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	    if (picFile != null && !picFile.isEmpty()) {
	        try {
	            // 클라우드 설정 (키 입력)
	            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
	            		 "cloud_name", "dh5p4lvo2",
	                     "api_key", "283127846695383",
	                     "api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k",
	                     "secure", true
	            ));

	            // 업로드 후 URL 받기
	            Map uploadResult = cloudinary.uploader().upload(picFile.getBytes(), ObjectUtils.emptyMap());
	            String imageUrl = (String) uploadResult.get("secure_url");

	            dto.setPic(imageUrl); // DB에는 이제 URL 주소가 저장됨
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }


		memberService.updateMemberInfo(dto);
		return "redirect:/member/mypage.do";
	}

	// 6-1. 프로필 작성/수정 화면 띄우기 (GET)
	@GetMapping("/editProfile.do")
	public String editProfile(Principal principal, Model model) {
		MemberDTO member = memberService.getMemberById(principal.getName());
		MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());

		model.addAttribute("profile", profile); // 기존 데이터가 있으면 화면에 출력
		return "member/editProfile";
	}

	// 6-2. 프로필 저장 완료 처리 (POST)
	@PostMapping("/editProfile.do")
	public String editProfileComplete(MatchDTO profileDto,
			@RequestParam(value = "staySeqs", required = false) List<Integer> staySeqs,
			@RequestParam(value = "languageSeqs", required = false) List<Integer> languageSeqs,
			@RequestParam(value = "ageGroupSeqs", required = false) List<Integer> ageGroupSeqs,
			@RequestParam("coverFile") MultipartFile coverFile, Principal principal) {

		MemberDTO member = memberService.getMemberById(principal.getName());
		profileDto.setSeqMember(member.getSeqMember());

		if (coverFile != null && !coverFile.isEmpty()) {
			try {
				// 클라우드 설정
				Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "dh5p4lvo2", "api_key",
						"283127846695383", "api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k", "secure", true));

				// 업로드 후 URL 받기
				Map uploadResult = cloudinary.uploader().upload(coverFile.getBytes(), ObjectUtils.emptyMap());
				String imageUrl = (String) uploadResult.get("secure_url");

				profileDto.setCoverPic(imageUrl); // DB에 URL 저장
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		matchingService.saveMyProfile(profileDto, staySeqs, languageSeqs, ageGroupSeqs);
		return "redirect:/member/mypage.do";
	}

	// 7. '내 활동 내역'(내가 작성한 게시글, 댓글) 전용 페이지 컨트롤러
	@GetMapping("/myactivity.do")
	public String myActivity(@RequestParam(value = "tab", defaultValue = "ALL") String tab, Model model,
			Principal principal) {

		String loginId = principal.getName();
		MemberDTO member = memberService.getMemberById(loginId);

		// 1) 마이페이지 구현에 썼던 연령대 계산 로직
		String ageGroup = "비공개";
		if (member.getBirth() != null && member.getBirth().length() >= 4) {
			int birthYear = Integer.parseInt(member.getBirth().substring(0, 4));
			int currentYear = LocalDate.now().getYear();
			int age = currentYear - birthYear;

			int ageGroupNum = (age / 10) * 10;
			ageGroup = ageGroupNum + "대";
		}

		// 2) MBTI 정보를 가져오기 위해 프로필 정보 조회
		MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());

		// 3) 내 활동 내역 가져오기
		List<MyActivityDTO> myActivities = memberService.getMyActivities(member.getSeqMember(), tab, 1);

		model.addAttribute("myActivities", myActivities);
		model.addAttribute("currentTab", tab); // JSP에서 어떤 탭을 열지 알려주기 위함

		// 4) 계산된 내 정보들을 JSP로 전달
		model.addAttribute("member", member);
		model.addAttribute("ageGroup", ageGroup);
		model.addAttribute("profile", profile);

		return "member/myactivity"; // JSP 파일로 연결
	}

	// 7-1. 무한 스크롤을 위한 API (JSON 데이터만 보내주는 역할)
	@GetMapping("/api/myactivity/more")
	@ResponseBody // 화면 이동 없이 데이터만 리턴
	public List<MyActivityDTO> getMoreActivities(@RequestParam(value = "tab", defaultValue = "ALL") String tab,
			@RequestParam(value = "page", defaultValue = "1") int page, Principal principal) {

		MemberDTO member = memberService.getMemberById(principal.getName());

		// 요청받은 페이지의 다음 10개 데이터를 DB에서 긁어서 리턴
		return memberService.getMyActivities(member.getSeqMember(), tab, page);
	}
}