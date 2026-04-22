<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
            
            <c:if test="${not empty auth}">
                <div class="badge ${authDto.type == 1 ? 'badge-warning' : 'badge-info'} badge-outline ml-2 gap-1">
                    <small>${authDto.type == 1 ? 'ADMIN' : 'USER'}</small>
                    <span class="font-bold">${auth}</span>
                </div>
            </c:if>
        </div>

        <div class="flex-none h-full">
            <ul class="menu menu-horizontal px-1 h-full gap-1 font-semibold text-slate-600">
                
                <li><a href="/teamtwo/companion/list.do" class="h-full flex items-center ${uri.contains('/companion/') ? 'active' : ''}">동행</a></li>
                <li><a href="/teamtwo/matching/list.do" class="h-full flex items-center ${uri.contains('/matching/') ? 'active' : ''}">매칭</a></li>
                
                
                <li class="dropdown dropdown-hover h-full"> <div tabindex="0" role="button" class="h-full flex items-center px-4 cursor-pointer ${uri.contains('/board/') ? 'active' : ''}">
				        커뮤니티
				    </div>
				    
				    <ul tabindex="0" class="dropdown-content z-[50] menu p-2 shadow bg-base-100 rounded-box w-40 border border-base-200 top-[25px] pt-4">
				        <li><a href="/teamtwo/board/list.do?category=free" class="whitespace-nowrap">자유 게시판</a></li>
				        <li><a href="/teamtwo/board/list.do?category=review" class="whitespace-nowrap">정보 게시판</a></li>
				        <li><a href="/teamtwo/board/list.do?category=qna" class="whitespace-nowrap">후기 게시판</a></li>
				        <li><a href="/teamtwo/board/list.do?category=qna" class="whitespace-nowrap">추천 게시판</a></li>
				        <li><a href="/teamtwo/board/list.do?category=qna" class="whitespace-nowrap">가이드 게시판</a></li>
				        <li><a href="/teamtwo/board/list.do?category=qna" class="whitespace-nowrap">숙소 게시판</a></li>
				        <li><a href="/teamtwo/board/list.do?category=qna" class="whitespace-nowrap">맛집 게시판</a></li>
				    </ul>
				</li>

                <li><a href="/teamtwo/chat/list.do" class="h-full flex items-center ${uri.contains('/chat/') ? 'active' : ''}">채팅</a></li>
                
                <li><a href="/teamtwo/chat/list.do" class="h-full flex items-center ${uri.contains('/user/') ? 'active' : ''}">마이페이지</a></li>

                <div class="divider divider-horizontal mx-1"></div>

                <c:choose>
                    <c:when test="${empty auth}">
                        <li><a href="/teamtwo/user/login.do" class="btn btn-ghost btn-sm h-full flex items-center">로그인</a></li>
                        <li><a href="/teamtwo/user/register.do" class="btn btn-primary btn-sm text-white">회원가입</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/teamtwo/user/mypage.do" class="${uri.contains('/user/mypage') ? 'active' : ''}">마이페이지</a></li>
                        <li><a href="/teamtwo/user/logout.do" class="text-error">로그아웃</a></li>
                        
                        <c:if test="${authDto.type == 1}">
                            <li><a href="/teamtwo/admin/main.do" class="btn btn-outline btn-error btn-sm ml-2">관리자</a></li>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</header>