package com.tripto.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	// 직접 접속했던 기록을 제공
	private HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		System.out.println("CustomLoginSuccessHandler이 호출되었습니다.");
		
		// 방법 1. 모든 로그인을 성공한 사람들을 시작 페이지(index)로 보내버린다
//		response.sendRedirect("/java/index.do");
		
		// 방법 2. 권한별 조치
		// 회원이면 member.do로 이동
		// 관리자면 admin.do로 이동
		
		// 현재 로그인을 성공한 유저의 권한을 배열에 저장한다
//		List<String> roleNames = new ArrayList<String>();
//		
//		// Granted Authorities: ROLE_MEMBER;
//		// System.out.println(authentication);
//		
//		authentication.getAuthorities().forEach(authority -> {
//			// 지금 로그인한 사람의 ROLE을 출력함
//			// ROLE_MEMBER는 그냥 문자열임. 스프링이
//			// ADMIN 이 뭐고 MEMBER가 뭔지 구별 못함
//			// System.out.println(authority);
//			// 얻은 ROLE을 배열에 넣어둠
//			roleNames.add(authority.getAuthority());
//		});
//		
//		// roleNames 배열에 ROLE_ADMIN 이 있냐고 물어봄
//		// 있으면 걔가 관리자
//		if (roleNames.contains("ROLE_ADMIN")) {
//			response.sendRedirect("java/admin.do");
//			return;
//		}
//		
//		if (roleNames.contains("ROLE_MEMBER")) {
//			response.sendRedirect("java/member.do");
//			return;
//		}
//		
//		response.sendRedirect("java/index.do");
		
		// 방법 3. 로그인 이전에 머물렀던 페이지로 보내기
		// 로그인 이전에 머물렀던 페이지의 URL을 객체 sr에 담는다
		SavedRequest sr = requestCache.getRequest(request, response);
		
		if (sr != null) {
			// 로그인 이전에 방문했던 URL이 존재한다면 ~~
			// 권한이 없는 페이지를 눌렀다가 로그인 성공
			response.sendRedirect(sr.getRedirectUrl());
		} else {
			// 이녀석은 처음부터 login.do 눌러서 로그인을 한 녀석
			response.sendRedirect("/index.do");
		}
		
	}
}
