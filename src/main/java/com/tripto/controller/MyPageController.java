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

	@Autowired
	private MatchingService matchingService;

	@Autowired
	private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

	// 1. 마이페이지 정보 출력
	@GetMapping("/mypage.do")
	public String mypage(Principal principal, Model model) {
		String id = principal.getName();
		MemberDTO member = memberService.getMemberById(id);

		String ageGroup = "비공개";
		if (member.getBirth() != null && member.getBirth().length() >= 4) {
			int birthYear = Integer.parseInt(member.getBirth().substring(0, 4));
			int currentYear = LocalDate.now().getYear();
			int age = currentYear - birthYear;
			int ageGroupNum = (age / 10) * 10;
			ageGroup = ageGroupNum + "대";
		}

		MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());

		model.addAttribute("member", member);
		model.addAttribute("ageGroup", ageGroup);
		model.addAttribute("profile", profile);

		return "member/mypage";
	}

	// 2-1. 비밀번호 변경 - 현재 비밀번호 확인 (AJAX)
	@PostMapping("/checkCurrentPw.do")
	@ResponseBody
	public String checkCurrentPw(@RequestParam("currentPw") String currentPw, Principal principal) {
		String dbPw = memberService.getCurrentPw(principal.getName());
		if (passwordEncoder.matches(currentPw, dbPw)) {
			return "MATCH";
		}
		return "MISMATCH";
	}

	// 2-2. 비밀번호 변경 처리
	@PostMapping("/changePw.do")
	public String changePw(@RequestParam("newPw") String newPw, Principal principal,
			HttpServletRequest request) { 
		String encodedPw = passwordEncoder.encode(newPw);

		Map<String, String> map = new HashMap<>();
		map.put("id", principal.getName());
		map.put("pw", encodedPw);

		memberService.updatePw(map);

		try {
			request.logout();
		} catch (javax.servlet.ServletException e) {
			e.printStackTrace();
		}

		return "redirect:/member/login.do";
	}

	// 3. 계정 탈퇴
	@PostMapping("/deactivate.do")
	public String deactivate(Principal principal, HttpServletRequest request)
			throws javax.servlet.ServletException {
		memberService.deactivateMember(principal.getName());
		request.logout();
		return "redirect:/index.do";
	}

	// 4. 프로필 미작성자 안내
	@GetMapping("/needProfile.do")
	public String needProfile() {
		return "member/needProfile";
	}

	// 5-1. 내 정보 수정 화면 (GET)
	@GetMapping("/editInfo.do")
	public String infoEdit(Principal principal, Model model) {
		String id = principal.getName();
		MemberDTO member = memberService.getMemberById(id);
		model.addAttribute("member", member);
		return "member/editInfo";
	}

	// 5-2. 내 정보 수정 완료 (POST)
	@PostMapping("/editInfo.do")
	public String infoEditComplete(MemberDTO dto, @RequestParam("picFile") MultipartFile picFile, Principal principal) {
		dto.setId(principal.getName());

		// 🌟 [충돌 해결: 중복 제거 및 통합] 프로필 사진 Cloudinary 업로드
		if (picFile != null && !picFile.isEmpty()) {
			try {
				Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
						"cloud_name", "dh5p4lvo2", 
						"api_key", "283127846695383", 
						"api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k", 
						"secure", true));

				Map uploadResult = cloudinary.uploader().upload(picFile.getBytes(), ObjectUtils.emptyMap());
				String imageUrl = (String) uploadResult.get("secure_url");

				dto.setPic(imageUrl); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		memberService.updateMemberInfo(dto);
		return "redirect:/member/mypage.do";
	}

	// 6-1. 프로필 수정 화면 (GET)
	@GetMapping("/editProfile.do")
	public String editProfile(Principal principal, Model model) {
		MemberDTO member = memberService.getMemberById(principal.getName());
		MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());
		model.addAttribute("profile", profile);
		return "member/editProfile";
	}

	// 6-2. 프로필 저장 완료 (POST)
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
				Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
						"cloud_name", "dh5p4lvo2", 
						"api_key", "283127846695383", 
						"api_secret", "eYnsfyRDN0ssk_wsyCTugTgKl3k", 
						"secure", true));

				Map uploadResult = cloudinary.uploader().upload(coverFile.getBytes(), ObjectUtils.emptyMap());
				String imageUrl = (String) uploadResult.get("secure_url");

				profileDto.setCoverPic(imageUrl); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		matchingService.saveMyProfile(profileDto, staySeqs, languageSeqs, ageGroupSeqs);
		return "redirect:/member/mypage.do";
	}

	// 7. 내 활동 내역
	@GetMapping("/myactivity.do")
	public String myActivity(@RequestParam(value = "tab", defaultValue = "ALL") String tab, Model model,
			Principal principal) {

		String loginId = principal.getName();
		MemberDTO member = memberService.getMemberById(loginId);

		String ageGroup = "비공개";
		if (member.getBirth() != null && member.getBirth().length() >= 4) {
			int birthYear = Integer.parseInt(member.getBirth().substring(0, 4));
			int currentYear = LocalDate.now().getYear();
			int age = currentYear - birthYear;
			int ageGroupNum = (age / 10) * 10;
			ageGroup = ageGroupNum + "대";
		}

		MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());
		List<MyActivityDTO> myActivities = memberService.getMyActivities(member.getSeqMember(), tab, 1);

		model.addAttribute("myActivities", myActivities);
		model.addAttribute("currentTab", tab); 
		model.addAttribute("member", member);
		model.addAttribute("ageGroup", ageGroup);
		model.addAttribute("profile", profile);

		return "member/myactivity";
	}

	// 7-1. 무한 스크롤 API
	// @ResponseBody를 사용해 화면(JSP)은 빼고 
	// 순수한 데이터(JSON)만 던져주는 
	// 전용 주소(/api/myactivity/more)를 사용
	@GetMapping("/api/myactivity/more")
	@ResponseBody 
	public List<MyActivityDTO> getMoreActivities(
			@RequestParam(value = "tab", defaultValue = "ALL") String tab,
			@RequestParam(value = "page", defaultValue = "1") int page, 
			Principal principal) {

		MemberDTO member = memberService.getMemberById(principal.getName());
		return memberService.getMyActivities(member.getSeqMember(), tab, page);
	}
}