package com.tripto.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tripto.service.MailService;

@Controller
@RequestMapping("/member")
public class MemberController {

	// MailService 의존 주입
	@Autowired
	private MailService mailService;

	// 1. 회원가입 폼 화면 보여주기 (GET 방식)
	@GetMapping("/join.do")
	public String join() {
		return "member/join";
	}

	// 2. 이메일 인증번호 발송 요청 처리 (AJAX)
	@PostMapping("/sendAuthEmail.do")
	@ResponseBody
	public String sendAuthEmail(@RequestParam String email, HttpSession session) {

		String authCode = mailService.sendAuthEmail(email);

		if ("FAIL".equals(authCode)) {
			return "FAIL";
		}

		// 인증번호와 함께 발송된 인증번호가 10분이 지나면 유효하지 않게 바뀌도록
		// '발급된 현재 시간'도 세션에 저장함
		session.setAttribute("authCode", authCode);
		// 현재 시간(밀리초) 저장
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

		// 2) 시간 체크: 10분이 지났는지 확인 (10분 = 10 * 60 * 1000 = 600,000 밀리초)
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
}