<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>매칭 커뮤니티 - TripTo</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
	<style>
		/* 가로 스크롤바 숨기기 */
		.hide-scrollbar::-webkit-scrollbar { display: none; }
		.hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
		
		/* 카드 호버 시 상단 짤림 방지 */
		.match-card { transition: box-shadow 0.3s ease; }
		.match-card:hover { box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1); }
	</style>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>
	
	<main class="page-wrap max-w-6xl mx-auto px-4 py-12">
		
		<div class="mb-12">
			<h1 class="text-3xl font-extrabold text-slate-800">매칭 커뮤니티</h1>
			<p class="text-slate-500 mt-2 text-lg">나와 취향이 찰떡인 여행 동행을 찾아보세요.</p>
		</div>

		<div class="mb-16">
			<div class="bg-white border border-slate-200 rounded-full px-8 py-4 mb-8 text-center font-bold text-xl shadow-sm">
				당신과 가장 잘 어울리는 사람들 💖
			</div>
			
			<div class="relative group">
				<button onclick="slideLeft(this)" class="absolute left-0 top-[125px] -translate-y-1/2 -ml-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-slate-50 hover:text-blue-600 hover:scale-110">
					<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg>
				</button>

				<div class="scroll-container flex overflow-x-auto gap-6 pb-6 snap-x hide-scrollbar scroll-smooth">
					<c:forEach var="match" items="${matchList}">
						<c:if test="${match.matchCount >= 4}">
							<div class="match-card min-w-[250px] w-[250px] bg-white rounded-[2.5rem] border-2 border-slate-200 overflow-hidden flex-shrink-0 snap-center cursor-pointer group/card"
								 onclick="location.href='${pageContext.request.contextPath}/matching/detail?seqMember=${match.seqMember}';">
								<div class="h-[250px] bg-slate-100 overflow-hidden relative">
									<img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" 
										 class="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110">
									<div class="absolute top-4 right-4 z-10 bg-blue-600 text-white text-[10px] px-2.5 py-1 rounded-full font-bold shadow-sm">
										${match.matchCount}개 일치
									</div>
								</div>
								<div class="p-6 text-center">
									<h3 class="font-bold text-xl text-slate-800 mb-3">${match.nickname}</h3>
									<div class="bg-slate-100 rounded-full py-2 px-4 inline-block w-full">
										<p class="text-sm font-bold text-slate-600"><span class="text-blue-600">${match.mbti}</span> · ${match.age}세 · ${match.gender == 0 ? '남' : '여'}</p>
									</div>
								</div>
							</div>
						</c:if>
					</c:forEach>
				</div>

				<button onclick="slideRight(this)" class="absolute right-0 top-[125px] -translate-y-1/2 -mr-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-slate-50 hover:text-blue-600 hover:scale-110">
					<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" /></svg>
				</button>
			</div>
		</div>

		<div class="mb-16">
			<c:choose>
				<c:when test="${fn:contains(fn:toUpperCase(myProfile.mbti), 'J')}">
					<div class="bg-white border border-slate-200 rounded-full px-8 py-4 mb-8 text-center font-bold text-xl shadow-sm">계획부터 척척! 함께 일정을 짤 J형 동행 📝</div>
					
					<div class="relative group">
						<button onclick="slideLeft(this)" class="absolute left-0 top-[125px] -translate-y-1/2 -ml-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg></button>
						<div class="scroll-container flex overflow-x-auto gap-6 pb-6 snap-x hide-scrollbar scroll-smooth">
							<c:forEach var="match" items="${matchList}">
								<c:if test="${fn:contains(fn:toUpperCase(match.mbti), 'J')}">
									<div class="match-card min-w-[250px] w-[250px] bg-white rounded-[2.5rem] border-2 border-indigo-400 overflow-hidden flex-shrink-0 snap-center cursor-pointer group/card"
										 onclick="location.href='${pageContext.request.contextPath}/matching/detail?seqMember=${match.seqMember}';">
										<div class="h-[250px] bg-slate-100 overflow-hidden relative">
											<img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" class="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110">
										</div>
										<div class="p-6 text-center">
											<h3 class="font-bold text-xl text-slate-800 mb-3">${match.nickname}</h3>
											<div class="bg-indigo-50 rounded-full py-2 px-4 inline-block w-full"><p class="text-sm font-bold text-indigo-600">${match.mbti} · ${match.age}세 · ${match.gender == 0 ? '남' : '여'}</p></div>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
						<button onclick="slideRight(this)" class="absolute right-0 top-[125px] -translate-y-1/2 -mr-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" /></svg></button>
					</div>
				</c:when>
				<c:otherwise>
					<div class="bg-white border border-slate-200 rounded-full px-8 py-4 mb-8 text-center font-bold text-xl shadow-sm">발길 닿는 대로! 자유로운 여행을 즐길 P형 동행 🎒</div>
					
					<div class="relative group">
						<button onclick="slideLeft(this)" class="absolute left-0 top-[125px] -translate-y-1/2 -ml-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg></button>
						<div class="scroll-container flex overflow-x-auto gap-6 pb-6 snap-x hide-scrollbar scroll-smooth">
							<c:forEach var="match" items="${matchList}">
								<c:if test="${fn:contains(fn:toUpperCase(match.mbti), 'P')}">
									<div class="match-card min-w-[250px] w-[250px] bg-white rounded-[2.5rem] border-2 border-orange-400 overflow-hidden flex-shrink-0 snap-center cursor-pointer group/card"
										 onclick="location.href='${pageContext.request.contextPath}/matching/detail?seqMember=${match.seqMember}';">
										<div class="h-[250px] bg-slate-100 overflow-hidden relative">
											<img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" class="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110">
										</div>
										<div class="p-6 text-center">
											<h3 class="font-bold text-xl text-slate-800 mb-3">${match.nickname}</h3>
											<div class="bg-indigo-50 rounded-full py-2 px-4 inline-block w-full"><p class="text-sm font-bold text-indigo-600">${match.mbti} · ${match.age}세 · ${match.gender == 0 ? '남' : '여'}</p></div>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
						<button onclick="slideRight(this)" class="absolute right-0 top-[125px] -translate-y-1/2 -mr-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" /></svg></button>
					</div>
				</c:otherwise>
			</c:choose>
		</div>

		<div class="mb-16">
			<c:choose>
				<c:when test="${myProfile.smoking == 1}">
					<div class="bg-white border border-slate-200 rounded-full px-8 py-4 mb-8 text-center font-bold text-xl shadow-sm">쾌적한 여행을 위해! 담배 냄새 걱정 없는 비흡연 동행 🚭</div>
					
					<div class="relative group">
						<button onclick="slideLeft(this)" class="absolute left-0 top-[125px] -translate-y-1/2 -ml-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg></button>
						<div class="scroll-container flex overflow-x-auto gap-6 pb-6 snap-x hide-scrollbar scroll-smooth">
							<c:forEach var="match" items="${matchList}">
								<c:if test="${match.smoking == 1}">
									<div class="match-card min-w-[250px] w-[250px] bg-white rounded-[2.5rem] border-2 border-emerald-400 overflow-hidden flex-shrink-0 snap-center cursor-pointer group/card"
										 onclick="location.href='${pageContext.request.contextPath}/matching/detail?seqMember=${match.seqMember}';">
										<div class="h-[250px] bg-slate-100 overflow-hidden relative">
											<img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" class="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110">
										</div>
										<div class="p-6 text-center">
											<h3 class="font-bold text-xl text-slate-800 mb-3">${match.nickname}</h3>
											<div class="bg-indigo-50 rounded-full py-2 px-4 inline-block w-full"><p class="text-sm font-bold text-indigo-600">${match.mbti} · ${match.age}세 · ${match.gender == 0 ? '남' : '여'}</p></div>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
						<button onclick="slideRight(this)" class="absolute right-0 top-[125px] -translate-y-1/2 -mr-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" /></svg></button>
					</div>
				</c:when>
				<c:otherwise>
					<div class="bg-white border border-slate-200 rounded-full px-8 py-4 mb-8 text-center font-bold text-xl shadow-sm">눈치 볼 필요 없어요! 흡연 매너를 아는 동행 🚬</div>
					
					<div class="relative group">
						<button onclick="slideLeft(this)" class="absolute left-0 top-[125px] -translate-y-1/2 -ml-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg></button>
						<div class="scroll-container flex overflow-x-auto gap-6 pb-6 snap-x hide-scrollbar scroll-smooth">
							<c:forEach var="match" items="${matchList}">
								<c:if test="${match.smoking == 0}">
									<div class="match-card min-w-[250px] w-[250px] bg-white rounded-[2.5rem] border-2 border-slate-400 overflow-hidden flex-shrink-0 snap-center cursor-pointer group/card"
										 onclick="location.href='${pageContext.request.contextPath}/matching/detail?seqMember=${match.seqMember}';">
										<div class="h-[250px] bg-slate-100 overflow-hidden relative">
											<img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" class="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110">
										</div>
										<div class="p-6 text-center">
											<h3 class="font-bold text-xl text-slate-800 mb-3">${match.nickname}</h3>
											<div class="bg-indigo-50 rounded-full py-2 px-4 inline-block w-full"><p class="text-sm font-bold text-indigo-600">${match.mbti} · ${match.age}세 · ${match.gender == 0 ? '남' : '여'}</p></div>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
						<button onclick="slideRight(this)" class="absolute right-0 top-[125px] -translate-y-1/2 -mr-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" /></svg></button>
					</div>
				</c:otherwise>
			</c:choose>
		</div>

		<div class="mb-16">
			<c:choose>
				<c:when test="${myProfile.drinking == 2}">
					<div class="bg-white border border-slate-200 rounded-full px-8 py-4 mb-8 text-center font-bold text-xl shadow-sm">술 없이도 즐거워요! 건전하고 건강한 여행 동행 🥤</div>
					
					<div class="relative group">
						<button onclick="slideLeft(this)" class="absolute left-0 top-[125px] -translate-y-1/2 -ml-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg></button>
						<div class="scroll-container flex overflow-x-auto gap-6 pb-6 snap-x hide-scrollbar scroll-smooth">
							<c:forEach var="match" items="${matchList}">
								<c:if test="${match.drinking == 2}">
									<div class="match-card min-w-[250px] w-[250px] bg-white rounded-[2.5rem] border-2 border-sky-400 overflow-hidden flex-shrink-0 snap-center cursor-pointer group/card"
										 onclick="location.href='${pageContext.request.contextPath}/matching/detail?seqMember=${match.seqMember}';">
										<div class="h-[250px] bg-slate-100 overflow-hidden relative">
											<img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" class="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110">
										</div>
										<div class="p-6 text-center">
											<h3 class="font-bold text-xl text-slate-800 mb-3">${match.nickname}</h3>
											<div class="bg-indigo-50 rounded-full py-2 px-4 inline-block w-full"><p class="text-sm font-bold text-indigo-600">${match.mbti} · ${match.age}세 · ${match.gender == 0 ? '남' : '여'}</p></div>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
						<button onclick="slideRight(this)" class="absolute right-0 top-[125px] -translate-y-1/2 -mr-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" /></svg></button>
					</div>
				</c:when>
				<c:otherwise>
					<div class="bg-white border border-slate-200 rounded-full px-8 py-4 mb-8 text-center font-bold text-xl shadow-sm">여행의 밤은 시원하게! 술 한잔 곁들일 술친구 🍻</div>
					
					<div class="relative group">
						<button onclick="slideLeft(this)" class="absolute left-0 top-[125px] -translate-y-1/2 -ml-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" /></svg></button>
						<div class="scroll-container flex overflow-x-auto gap-6 pb-6 snap-x hide-scrollbar scroll-smooth">
							<c:forEach var="match" items="${matchList}">
								<c:if test="${match.drinking == 0 || match.drinking == 1}">
									<div class="match-card min-w-[250px] w-[250px] bg-white rounded-[2.5rem] border-2 border-amber-400 overflow-hidden flex-shrink-0 snap-center cursor-pointer group/card"
										 onclick="location.href='${pageContext.request.contextPath}/matching/detail?seqMember=${match.seqMember}';">
										<div class="h-[250px] bg-slate-100 overflow-hidden relative">
											<img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" class="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110">
										</div>
										<div class="p-6 text-center">
											<h3 class="font-bold text-xl text-slate-800 mb-3">${match.nickname}</h3>
											<div class="bg-indigo-50 rounded-full py-2 px-4 inline-block w-full"><p class="text-sm font-bold text-indigo-600">${match.mbti} · ${match.age}세 · ${match.gender == 0 ? '남' : '여'}</p></div>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
						<button onclick="slideRight(this)" class="absolute right-0 top-[125px] -translate-y-1/2 -mr-5 z-20 bg-white border border-slate-200 text-slate-600 w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110"><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" /></svg></button>
					</div>
				</c:otherwise>
			</c:choose>
		</div>

	</main>

	<script>
		function slideLeft(btn) {
			// 클릭한 버튼의 부모(relative) 안에서 scroll-container를 찾음
			const container = btn.parentElement.querySelector('.scroll-container');
			// 왼쪽으로 300px(카드 1개 반 너비) 이동
			container.scrollBy({ left: -300, behavior: 'smooth' });
		}

		function slideRight(btn) {
			// 클릭한 버튼의 부모(relative) 안에서 scroll-container를 찾음
			const container = btn.parentElement.querySelector('.scroll-container');
			// 오른쪽으로 300px 이동
			container.scrollBy({ left: 300, behavior: 'smooth' });
		}
	</script>
</body>
</html>