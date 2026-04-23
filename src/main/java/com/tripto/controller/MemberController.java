package com.tripto.controller;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tripto.dto.MemberDTO;
import com.tripto.service.MailService;
import com.tripto.service.MemberService;

@Controller
@RequestMapping("/member")
public class MemberController {

	// MailService 의존 주입
	@Autowired
	private MailService mailService;

	// MemberService 의존 주입
	@Autowired
	private MemberService memberService;

	// security-context.xml 에 등록된 암호화 객체를 의존 주입
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// 1. 회원가입 폼 화면 보여주기 (GET 방식)
	@GetMapping("/join.do")
	public String join() {
		return "member/join";
	}

	// 2. 이메일 인증번호 발송 요청 처리 (AJAX)
	@PostMapping("/sendAuthEmail.do")
	@ResponseBody
	public String sendAuthEmail(@RequestParam String email, HttpSession session) {

		// 1) 이메일 중복 검사를 가장 먼저 실행
		int count = memberService.checkEmail(email);
		if (count > 0) {
			return "DUPLICATE"; // 이미 가입된 이메일이면 더 이상 진행할 수 없음
		}

		// 2) 중복이 아니면 정상적으로 메일 발송 진행
		String authCode = mailService.sendAuthEmail(email);

		if ("FAIL".equals(authCode)) {
			return "FAIL";
		}

		session.setAttribute("authCode", authCode);
		session.setAttribute("authCodeTime", System.currentTimeMillis());

		return "SUCCESS";
	}

	// 3. 인증번호 확인 로직
	@PostMapping("/verifyAuthCode.do")
	@ResponseBody
	public String verifyAuthCode(@RequestParam String inputCode, HttpSession session) {
		String sessionCode = (String) session.getAttribute("authCode");
		Long sessionTime = (Long) session.getAttribute("authCodeTime");

		// 1) 세션에 인증번호가 아예 없는 경우
		if (sessionCode == null || sessionTime == null) {
			return "NOT_FOUND";
		}

		// 2) 시간 체크: 10분이 지났는지 확인 (10분 = 600,000 밀리초)
		long currentTime = System.currentTimeMillis();
		if (currentTime - sessionTime > 600000) {
			// 만료되었으면 세션에서 파기
			session.removeAttribute("authCode");
			session.removeAttribute("authCodeTime");
			return "EXPIRED";
		}

		// 3) 번호 일치 여부 확인
		if (sessionCode.equals(inputCode)) {
			// 인증 성공 시, 회원가입 완료를 위해 '인증됨' 상태를 세션에 남겨둠 (보안)
			session.setAttribute("isEmailVerified", true);
			return "MATCH";
		} else {
			return "MISMATCH";
		}
	}

	// 4. 회원가입 시 아이디 중복확인 (AJAX)
	@PostMapping("/checkId.do")
	@ResponseBody
	public String checkId(@RequestParam String id) {

		int count = memberService.checkId(id);

		if (count > 0) {
			return "DUPLICATE"; // DB에 이미 입력받은 아이디가 1개 이상 존재함
		} else {
			return "AVAILABLE"; // DB에 없음 (사용 가능)
		}
	}

	// 5. 회원가입 폼 제출 처리
	@PostMapping("/join.do")
	public String joinComplete(MemberDTO dto, @RequestParam("picFile") MultipartFile picFile) {

		// 1) 비밀번호 암호화 (사용자가 친 1111 -> $2a$10$ 복잡한 문자열로 변환)
		String encodedPw = passwordEncoder.encode(dto.getPw());
		dto.setPw(encodedPw);

		// 2) 프로필 사진 파일 업로드 처리
		if (picFile.isEmpty()) {
			// 파일이 없으면 기본 이미지 세팅
			dto.setPic("pic.png");
		} else {
			try {
				// "C드라이브 절대 경로"에 저장
				// (이클립스 서버 재시작 시 사진이 날아가는 것을 방지)
				String path = "C:/tripto_upload/profile/";
				File dir = new File(path);
				if (!dir.exists())
					dir.mkdirs(); // 폴더가 없으면 생성

				// 사진 이름이 겹치지 않게 UUID(랜덤문자열)를 붙임
				String originalName = picFile.getOriginalFilename();
				String uuid = UUID.randomUUID().toString();
				String savedName = uuid + "_" + originalName; // 예: 123e4567_홍길동.jpg

				// 실제 지정한 폴더로 파일 복사(저장)
				File target = new File(path, savedName);
				picFile.transferTo(target);

				// DB에 들어갈 파일명 DTO에 세팅
				dto.setPic(savedName);

			} catch (Exception e) {
				e.printStackTrace();
				dto.setPic("pic.png"); // 에러 발생 시 기본 이미지로 대체해 출력
			}
		}

		// 3) 서비스로 넘겨서 DB INSERT 실행
		memberService.joinMember(dto);

		// 가입이 완료되면 로그인 페이지로 돌려보냄
		return "redirect:/member/login.do";
	}

	// 6. 로그인 폼 화면 보여주기
	@GetMapping("/login.do")
	public String login() {
		return "member/login";
	}

}