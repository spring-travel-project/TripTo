<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="uri" value="${not empty requestScope['javax.servlet.forward.request_uri'] ? requestScope['javax.servlet.forward.request_uri'] : pageContext.request.requestURI}" />

<style>
.nav-active {
    font-weight: 800 !important;
    color: #334155 !important;
}

.nav-active::after {
    content: '';
    position: absolute;
    bottom: 0px;
    left: 0;
    width: 100%;
    height: 3px;
    background-color: #2563eb;
}
</style>

<header class="bg-white border-b border-base-200 sticky top-0 z-50">
    <div class="navbar max-w-6xl mx-auto h-16 min-h-0 px-4">
        
        <div class="flex-1">
            <a href="${pageContext.request.contextPath}/index.do" class="btn btn-ghost text-2xl font-bold text-primary gap-2 px-2 hover:bg-transparent">
                <img src="${pageContext.request.contextPath}/resources/img/trip_icon.png" alt="로고" class="w-8 h-8">
                <span class="tracking-tight">trip</span>
            </a>
            
            <sec:authorize access="isAuthenticated()">
                <div class="badge badge-outline ml-2 gap-1 
                    <sec:authorize access="hasRole('ROLE_ADMIN')">badge-warning</sec:authorize>
                    <sec:authorize access="hasRole('ROLE_MEMBER')">badge-info</sec:authorize>">
                    
                    <small>
                        <sec:authorize access="hasRole('ROLE_ADMIN')">ADMIN</sec:authorize>
                        <sec:authorize access="hasRole('ROLE_MEMBER')">USER</sec:authorize>
                    </small>
                    <span class="font-bold"><sec:authentication property="principal.username"/></span>
                </div>
            </sec:authorize>
        </div>

        <div class="flex-none h-full">
            <ul class="menu menu-horizontal px-1 h-full gap-1 font-semibold text-slate-600">
                
                <li><a href="${pageContext.request.contextPath}/companion/list.do" class="h-full flex items-center ${uri.contains('/companion/') ? 'active' : ''}">동행</a></li>
                <li><a href="${pageContext.request.contextPath}/matching/list.do" class="h-full flex items-center ${uri.contains('/matching/') ? 'active' : ''}">매칭</a></li>
                
                <li class="dropdown dropdown-hover h-full"> 
                    <div tabindex="0" role="button" class="h-full flex items-center px-4 cursor-pointer ${uri.contains('/board/') ? 'active' : ''}">
                        커뮤니티
                    </div>
                    <ul tabindex="0" class="dropdown-content z-[50] menu p-2 shadow bg-base-100 rounded-box w-40 border border-base-200 top-[25px] pt-4">
                        <li><a href="${pageContext.request.contextPath}/board/list.do?category=free" class="whitespace-nowrap">자유 게시판</a></li>
                        <li><a href="${pageContext.request.contextPath}/board/list.do?category=review" class="whitespace-nowrap">정보 게시판</a></li>
                        <li><a href="${pageContext.request.contextPath}/board/list.do?category=qna" class="whitespace-nowrap">후기 게시판</a></li>
                        <li><a href="${pageContext.request.contextPath}/board/list.do?category=recommend" class="whitespace-nowrap">추천 게시판</a></li>
                        <li><a href="${pageContext.request.contextPath}/board/list.do?category=guide" class="whitespace-nowrap">가이드 게시판</a></li>
                        <li><a href="${pageContext.request.contextPath}/board/list.do?category=stay" class="whitespace-nowrap">숙소 게시판</a></li>
                        <li><a href="${pageContext.request.contextPath}/board/list.do?category=food" class="whitespace-nowrap">맛집 게시판</a></li>
                    </ul>
                </li>

                <li><a href="${pageContext.request.contextPath}/chat/list.do" class="h-full flex items-center ${uri.contains('/chat/') ? 'active' : ''}">채팅</a></li>
                
                <div class="divider divider-horizontal mx-1"></div>

                <sec:authorize access="isAnonymous()">
                    <li><a href="${pageContext.request.contextPath}/member/login.do" class="btn btn-ghost btn-sm h-full flex items-center">로그인</a></li>
                    <li><a href="${pageContext.request.contextPath}/member/join.do" class="btn btn-primary btn-sm text-white">회원가입</a></li>
                </sec:authorize>

                <sec:authorize access="isAuthenticated()">
                    <li><a href="${pageContext.request.contextPath}/member/mypage.do" class="${uri.contains('/member/mypage') ? 'active' : ''}">마이페이지</a></li>
                    
                    <li>
                        <form action="${pageContext.request.contextPath}/logout" method="POST" class="p-0 m-0 w-full h-full">
                            <button type="submit" class="text-error w-full h-full text-left px-4 hover:bg-base-200 bg-transparent border-none cursor-pointer">로그아웃</button>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        </form>
                    </li>
                    
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <li><a href="${pageContext.request.contextPath}/admin/main.do" class="btn btn-outline btn-error btn-sm ml-2">관리자</a></li>
                    </sec:authorize>
                </sec:authorize>
            </ul>
        </div>
    </div>
</header>

