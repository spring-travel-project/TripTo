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

<header
	class="bg-white border-b border-slate-200 relative z-50 h-[64px]">
	<div class="navbar max-w-6xl w-full mx-auto px-4 py-0 h-full min-h-0">

		<div class="flex-1 items-center h-full flex">
			<a href="/teamtwo/index.do" class="text-2xl font-bold text-brand flex items-center gap-2">
    <img src="/teamtwo/asset/pic/logo.png" alt="deverytime 로고" class="w-8 h-8 object-contain">
    deverytime
</a>
			<c:if test="${not empty auth}">
				<span
					class="ml-3 chip ${authDto.type == 1 ? 'chip-amber' : 'chip-blue'}">
					${authDto.type == 1 ? '관리자' : '일반'} | ${auth} </span>
			</c:if>
		</div>

		<div class="flex-none h-full">
			<ul class="flex h-full text-slate-700 font-semibold items-center">

				<li class="h-full"><a href="/teamtwo/plan/list.do"
					class="relative h-full flex items-center px-4 hover:bg-slate-50 transition-colors ${uri.contains('/plan/') ? 'nav-active' : ''}">학습계획</a>
				</li>
				<li class="h-full"><a href="/teamtwo/study/study-list.do"
					class="relative h-full flex items-center px-4 hover:bg-slate-50 transition-colors ${uri.contains('/study/') ? 'nav-active' : ''}">스터디</a>
				</li>

				<li class="dropdown dropdown-hover h-full"><a
					href="/teamtwo/board/trendingboard/list.do"
					class="relative h-full flex items-center px-4 hover:bg-slate-50 transition-colors cursor-pointer ${uri.contains('/board/') ? 'nav-active' : ''}">게시판</a>

					<ul tabindex="0"
						class="dropdown-content menu bg-white rounded-xl w-48 p-2 shadow-md border border-slate-200 top-full left-1/2 -translate-x-1/2 z-[50]">
						<li><a href="/teamtwo/board/trendingboard/list.do">인기글
								게시판</a></li>
						<li><a href="/teamtwo/board/list.do?board=1">자유 게시판</a></li>
						<li><a href="/teamtwo/board/list.do?board=2">질문 게시판</a></li>
						<li><a href="/teamtwo/board/list.do?board=3">자료
								공유 게시판</a></li>
						<li><a href="/teamtwo/board/list.do?board=4">학습
								공유 게시판</a></li>
						<li><a href="/teamtwo/board/request/list.do">문의 게시판</a></li>
					</ul></li>

				<c:choose>
					<c:when test="${empty auth}">
						<li class="h-full"><a href="/teamtwo/user/login.do"
							class="relative h-full flex items-center px-4 hover:bg-slate-50 transition-colors ${uri.contains('login') || uri.contains('register') || uri.contains('find') ? 'nav-active' : ''}">로그인/회원가입</a></li>
					</c:when>
					<c:otherwise>
						<li class="h-full"><a href="/teamtwo/user/mypage.do"
							class="relative h-full flex items-center px-4 hover:bg-slate-50 transition-colors ${uri.contains('/user/') && !uri.contains('logout') ? 'nav-active' : ''}">
								마이페이지 </a></li>
						<li class="h-full"><a href="/teamtwo/user/logout.do"
							class="relative h-full flex items-center px-4 hover:bg-slate-50 transition-colors">로그아웃</a></li>

						<c:if test="${authDto.type == 1}">
							<li class="h-full"><a href="/teamtwo/admin/admin.do"
								class="relative h-full flex items-center px-4 text-point-500 hover:bg-slate-50 transition-colors ${uri.contains('/admin/') ? 'nav-active' : ''}">관리자
									메뉴</a></li>
						</c:if>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>

	</div>
</header>