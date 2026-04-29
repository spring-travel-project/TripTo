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

		// passwordEncoder.matches(입력한 생비번, DB의 암호화된 비번) 로 비교!
		if (passwordEncoder.matches(currentPw, dbPw)) {
			return "MATCH";
		}
		return "MISMATCH";
	}

	// 2-2. 비밀번호 변경 - 새 비밀번호로 변경
	@PostMapping("/changePw.do")
	public String changePw(@RequestParam("newPw") String newPw, Principal principal) {
		String encodedPw = passwordEncoder.encode(newPw);

		Map<String, String> map = new HashMap<>();
		map.put("id", principal.getName());
		map.put("pw", encodedPw);

		// 기존에 만들어둔 비밀번호 업데이트 메서드 재활용
		memberService.updatePw(map);

		// 비밀번호 변경 후 강제 로그아웃 시키기
		return "redirect:/logout";
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
	public String infoEditComplete(MemberDTO dto, @RequestParam("picFile") MultipartFile picFile, Principal principal,
			HttpServletRequest request) {

		// 1) 누구를 수정할지 기준(id) 세팅
		dto.setId(principal.getName());

		// 2) 프로필 사진 파일 업로드 처리 (회원가입 로직 100% 재활용)
		if (!picFile.isEmpty()) {
			try {
				String path = request.getServletContext().getRealPath("/resources/upload/profile/");
				File dir = new File(path);
				if (!dir.exists())
					dir.mkdirs();

				String originalName = picFile.getOriginalFilename();
				String uuid = UUID.randomUUID().toString();
				String savedName = uuid + "_" + originalName;

				File target = new File(path, savedName);
				picFile.transferTo(target);

				dto.setPic(savedName); // 새로 업로드한 파일명 DTO에 꽂아넣기
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// (사진을 안 올렸으면 jsp에서 picFile이 비어있으므로 동적 쿼리에 의해 pic 컬럼은 건드리지 않음)

		// 3) DB 업데이트 실행
		memberService.updateMemberInfo(dto);

		// 4) 수정 완료 후 다시 마이페이지 메인으로 부드럽게 복귀
		return "redirect:/member/mypage.do";
	}

	// 6-1. 프로필 작성/수정 화면 띄우기 (GET)
	@GetMapping("/editProfile.do")
	public String editProfile(Principal principal, Model model) {
		MemberDTO member = memberService.getMemberById(principal.getName());
		MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());

		model.addAttribute("profile", profile); // 기존 데이터가 있으면 화면에 뿌려줌
		return "member/editProfile";
	}

	// 6-2. 프로필 저장 완료 처리 (POST)
	@PostMapping("/editProfile.do")
	public String editProfileComplete(MatchDTO profileDto,
			@RequestParam(value = "staySeqs", required = false) List<Integer> staySeqs,
			@RequestParam(value = "languageSeqs", required = false) List<Integer> languageSeqs,
			@RequestParam(value = "ageGroupSeqs", required = false) List<Integer> ageGroupSeqs,
			@RequestParam("coverFile") MultipartFile coverFile, Principal principal,
			javax.servlet.http.HttpServletRequest request) {

		// 1) 로그인한 유저의 seqMember 가져와서 DTO에 세팅
		MemberDTO member = memberService.getMemberById(principal.getName());
		profileDto.setSeqMember(member.getSeqMember());

		// 2) 배경 사진(Cover) 업로드 처리
		if (!coverFile.isEmpty()) {
			try {
				String path = request.getServletContext().getRealPath("/resources/upload/cover/"); // 배경 사진 전용 폴더
				File dir = new File(path);
				if (!dir.exists())
					dir.mkdirs();

				String uuid = UUID.randomUUID().toString();
				String savedName = uuid + "_" + coverFile.getOriginalFilename();

				File target = new File(path, savedName);
				coverFile.transferTo(target);

				profileDto.setCoverPic(savedName); // DB에 넣을 파일명 세팅
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 3) 서비스 호출 (프로필 + 다중선택 배열 한 번에 넘기기)
		matchingService.saveMyProfile(profileDto, staySeqs, languageSeqs, ageGroupSeqs);

		// 4) 작성 완료 후 마이페이지로 이동
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