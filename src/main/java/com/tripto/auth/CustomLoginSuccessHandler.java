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

		// 방법 3. 로그인 이전에 머물렀던 페이지로 보내기
		// 로그인 이전에 머물렀던 페이지의 URL을 객체 sr에 담는다
		SavedRequest sr = requestCache.getRequest(request, response);

		if (sr != null) {
			// 로그인 이전에 방문했던 URL이 존재한다면 ~~
			// 권한이 없는 페이지를 눌렀다가 로그인 성공
			response.sendRedirect(sr.getRedirectUrl());
		} else {
			// 이녀석은 처음부터 login.do 눌러서 로그인을 한 녀석
			response.sendRedirect(request.getContextPath() + "/index.do");
		}

	}
}
