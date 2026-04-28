<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 일정 상세</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>

	<main class="page-wrap">

		<div class="mb-8 flex items-end justify-between gap-4">
			<div>
				<h1 class="section-title">일정 상세</h1>
				<p class="section-desc">채팅방에 공유된 여행 일정을 확인하세요.</p>
			</div>

			<a href="${pageContext.request.contextPath}/chat/schedulePoll?roomId=${roomId}"
			   class="px-4 py-2 rounded-xl border border-slate-300 bg-white text-sm font-medium hover:bg-slate-100 transition">
				목록으로 돌아가기
			</a>
		</div>

		<div class="content-card card-pad space-y-6">

			<!-- 일정 제목 -->
			<div class="rounded-2xl border border-slate-200 bg-slate-100 px-5 py-4 text-center">
				<h2 class="text-2xl font-bold text-slate-900">
					<c:out value="${routine.title}" />
				</h2>
			</div>

			<!-- 작성 정보 -->
			<div class="flex flex-wrap items-center justify-between gap-3 text-sm text-slate-500">
				<div>
					작성자:
					<span class="font-semibold text-slate-700">
						<c:out value="${routine.writerNickname}" />
					</span>
				</div>

				<div class="flex items-center gap-2">
					<span class="inline-flex items-center rounded-full bg-sky-100 text-sky-700 text-xs font-medium px-2 py-0.5">
						<c:out value="${routine.dDayText}" />
					</span>
					<span>
						작성일 <c:out value="${routine.regdateText}" />
					</span>
				</div>
			</div>

			<!-- 일정 내용 -->
			<div class="rounded-2xl border border-slate-200 bg-slate-100 p-8 min-h-[360px]">
				<h3 class="text-xl font-bold text-slate-900 mb-5">일정 내용</h3>

				<p class="text-sm text-slate-700 leading-7 whitespace-pre-line">
					<c:out value="${routine.detail}" />
				</p>
				
				<c:forEach items="${fileList}" var="file">
				    <c:if test="${not empty file.filePath}">
				        <img src="${pageContext.request.contextPath}${file.filePath}"
				             class="w-40 rounded-2xl border border-slate-200 shadow-sm">
				    </c:if>
				</c:forEach>
				
				<c:if test="${not empty routine.placeName}">
					<div class="mt-8 rounded-2xl border border-slate-200 bg-white p-5">
						<p class="text-sm font-semibold text-slate-700 mb-1">목적지</p>
						<p class="text-base font-bold text-slate-900">
							<c:out value="${routine.placeName}" />
						</p>
						<p class="text-sm text-slate-500 mt-1">
							<c:out value="${routine.address}" />
						</p>
					</div>
				</c:if>
			</div>

			<!-- 하단 버튼 -->
			<c:if test="${routine.seqMember == loginUserId}">
				<div class="flex justify-end gap-3">
					<a href="${pageContext.request.contextPath}/chat/routine/edit?roomId=${roomId}&routineId=${routine.seq}"
					   class="px-5 py-2.5 rounded-xl border border-slate-300 bg-white text-slate-700 text-sm font-semibold hover:bg-slate-100 transition">
					    수정
					</a>

					<form method="post"
					      action="${pageContext.request.contextPath}/chat/routine/delete"
					      onsubmit="return confirm('정말 이 일정을 삭제하시겠습니까? 삭제 후 복구할 수 없습니다.');">
					
					    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
					
					    <input type="hidden" name="roomId" value="${roomId}">
					    <input type="hidden" name="routineId" value="${routine.seq}">
					
					    <button type="submit"
					            class="px-5 py-2.5 rounded-xl border border-rose-200 bg-rose-50 text-rose-600 text-sm font-semibold hover:bg-rose-100 transition">
					        일정 삭제
					    </button>
					</form>
				</div>
			</c:if>

		</div>

	</main>
</body>
</html>