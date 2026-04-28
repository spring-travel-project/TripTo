package com.tripto.controller;

import java.io.File;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	public String infoEditComplete(MemberDTO dto, @RequestParam("picFile") MultipartFile picFile, Principal principal) {

		// 1) 누구를 수정할지 기준(id) 세팅
		dto.setId(principal.getName());

		// 2) 프로필 사진 파일 업로드 처리 (회원가입 로직 100% 재활용)
		if (!picFile.isEmpty()) {
			try {
				String path = "C:/tripto_upload/profile/";
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
		// (사진을 안 올렸으면 jsp에서 picFile이 비어있으므로 동적 쿼리에 의해 pic 컬럼은 건드리지 않음!)

		// 3) DB 업데이트 실행
		memberService.updateMemberInfo(dto);

		// 4) 수정 완료 후 다시 마이페이지 메인으로 부드럽게 복귀
		return "redirect:/member/mypage.do";
	}
}