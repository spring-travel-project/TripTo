package com.tripto.interceptor;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import com.tripto.dto.MatchDTO;
import com.tripto.dto.MemberDTO;
import com.tripto.service.MatchingService;
import com.tripto.service.MemberService;

public class ProfileCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MatchingService matchingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 1. 시큐리티를 통해 로그인한 유저 정보 가져오기
        Principal principal = request.getUserPrincipal();

        // 로그인이 안 된 상태면 그냥 통과(시큐리티가 알아서 로그인 창으로 튕겨냄)
        if (principal == null) {
            return true;
        }

        // 2. DB에서 유저 정보와 프로필 정보 조회
        String id = principal.getName();
        MemberDTO member = memberService.getMemberById(id);

        if (member != null) {
            MatchDTO profile = matchingService.getMyProfile(member.getSeqMember());

            // 3. 프로필 정보가 아예 없거나, MBTI 같은 필수 값이 비어있다면?
            if (profile == null || profile.getMbti() == null || profile.getMbti().trim().isEmpty()) {
                
                // AJAX 통신으로 요청한 경우 리다이렉트가 안 먹히므로 에러 코드로 반환
                String ajaxHeader = request.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equals(ajaxHeader)) {
                    response.sendError(403, "PROFILE_REQUIRED");
                    return false;
                }

                // 일반 화면 접근 시 '프로필 작성 안내 페이지'로 Redirect
                response.sendRedirect(request.getContextPath() + "/member/needProfile.do");
                return false; // 원래 가려던 컨트롤러 실행을 중단시킴
            }
        }
        
        // 프로필이 작성된 회원이라면 통과
        return true; 
    }
}