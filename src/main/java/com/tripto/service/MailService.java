package com.tripto.service;

import java.util.Random;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// 6자리 난수를 생성해 메일을 발송하는 기능 구현을 위한 MailService
@Service
public class MailService {

	// root-context.xml에서 세팅해둔 메일 서버 객체를 스프링이 알아서 주입(연결)해줌
	@Autowired
	private JavaMailSender mailSender;

	// 1. 6자리 난수(인증번호) 생성 메서드
	private String makeRandomNumber() {
		Random random = new Random();
		// 111111 ~ 999999 범위의 6자리 숫자 생성
		int checkNum = random.nextInt(888888) + 111111;
		return String.valueOf(checkNum);
	}

	// 2. 이메일 발송 메서드 (컨트롤러가 호출할 예정)
	public String sendAuthEmail(String toEmail) {
		// 방금 만든 메서드로 6자리 인증번호를 뽑아냄
		String authCode = makeRandomNumber();

		// HTML로 이메일 제목 및 내용 세팅
		String subject = "[TripTo] 요청하신 인증번호를 안내해 드립니다.";
		String content = "안녕하세요, TripTo입니다.<br><br>" + "요청하신 인증번호는 <strong>[" + authCode + "]</strong> 입니다.<br>"
				+ "해당 인증번호를 화면에 입력해 주세요.";

		try {
			// MimeMessage 객체 생성 (스프링이 제공하는 메일 포맷)
			MimeMessage message = mailSender.createMimeMessage();

			// MimeMessageHelper를 쓰면 파일 첨부나 HTML 메일 작성이 쉬움
			// (true는 멀티파트 활성화, "utf-8"은 한글 깨짐 방지 인코딩)
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

			// javax.mail.internet.InternetAddress 객체를 사용해서 인코딩을 강제
			// 사용자에게 보여질 발신자 이름 (실제 발송은 properties 계정으로 됨)
			helper.setFrom(new javax.mail.internet.InternetAddress("no-reply@tripto.com", "TripTo관리자", "UTF-8"));
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(content, true); // 두 번째 인자를 true로 줘야 HTML 태그가 적용됨

			// 메일 발송하기
			mailSender.send(message);
			System.out.println("메일 전송 성공: " + toEmail + " / 발급된 인증번호: " + authCode);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("메일 전송 실패! (계정 설정이나 방화벽을 확인하세요)");
			return "FAIL";
		}

		// 나중에 컨트롤러가 이 번호를 받아서 세션(Session)에 저장해야 하므로, 생성된 번호를 리턴함
		return authCode;
	}

	// 2. 찾은 아이디를 이메일로 발송하는 메서드
	public void sendIdEmail(String email, String id) {
		String subject = "[TripTo] 요청하신 아이디 안내입니다.";
		String content = "안녕하세요, TripTo입니다.<br><br>" + "회원님이 가입하신 아이디는 <strong>[" + id + "]</strong> 입니다.<br>";

		try {
			// MimeMessage 객체 생성 (스프링이 제공하는 메일 포맷)
			MimeMessage message = mailSender.createMimeMessage();

			// MimeMessageHelper를 쓰면 파일 첨부나 HTML 메일 작성이 쉬움
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
			
			// 사용자에게 보여질 발신자 이름
			// javax.mail.internet.InternetAddress 객체를 사용해서 인코딩을 강제
			helper.setFrom(new javax.mail.internet.InternetAddress("no-reply@tripto.com", "TripTo관리자", "UTF-8"));
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(content, true);

			// 메일 발송하기
			mailSender.send(message);
			System.out.println("아이디 안내 메일 전송 성공: " + email + " / 찾은 아이디: " + id);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("아이디 안내 메일 전송 실패! (계정 설정이나 방화벽을 확인하세요)");
		}
	}
}