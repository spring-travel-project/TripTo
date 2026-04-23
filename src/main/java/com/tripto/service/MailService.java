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
        String subject = "[TripTo] 회원가입 인증번호 안내";
        String content = "";
        content += "<div style='margin: 20px; font-family: sans-serif;'>";
        content += "<h2>TripTo 회원가입을 환영합니다!</h2>";
        content += "<p>아래의 인증번호를 진행 중인 회원가입 화면에 입력해 주세요.</p>";
        content += "<br>";
        content += "<div style='background-color: #f8fafc; border: 1px solid #e2e8f0; padding: 20px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px;'>";
        content += authCode;
        content += "</div>";
        content += "<br>";
        content += "<p>감사합니다.</p>";
        content += "</div>";

        try {
            // MimeMessage 객체 생성 (스프링이 제공하는 메일 포맷)
            MimeMessage message = mailSender.createMimeMessage();
            
            // MimeMessageHelper를 쓰면 파일 첨부나 HTML 메일 작성이 쉬움 
            // (true는 멀티파트 활성화, "utf-8"은 한글 깨짐 방지 인코딩)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            
            helper.setFrom("TripTo관리자<no-reply@tripto.com>"); // 사용자에게 보여질 발신자 이름 (실제 발송은 properties 계정으로 됨)
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
}