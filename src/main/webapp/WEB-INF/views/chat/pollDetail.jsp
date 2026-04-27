<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 투표 조회</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>

	<main class="page-wrap">

		<div class="mb-8 flex items-end justify-between gap-4">
			<div>
				<h1 class="section-title">투표 조회</h1>
				<p class="section-desc">투표 내용과 참여 현황을 확인하세요.</p>
			</div>

			<a href="${pageContext.request.contextPath}/chat/schedulePoll?roomId=${roomId}"
			   class="px-4 py-2 rounded-xl border border-slate-300 bg-white text-sm font-medium hover:bg-slate-100 transition">
				목록으로 돌아가기
			</a>
		</div>

		<div class="content-card card-pad space-y-6">

			<!-- 투표 제목 / 종료일 -->
			<div class="grid grid-cols-1 md:grid-cols-[1fr_260px] gap-6">
				<div class="rounded-2xl border border-slate-200 bg-slate-100 px-5 py-4 text-center">
					<h2 class="text-2xl font-bold text-slate-900">
						<c:out value="${poll.pollTitle}" />
					</h2>
				</div>

				<div class="rounded-2xl border border-slate-200 bg-white px-5 py-4 text-center">
					<p class="text-xs text-slate-500 mb-1">투표 종료 시간/날짜</p>
					<p class="text-sm font-semibold text-slate-700">
						<c:out value="${poll.enddateText}" />
					</p>
				</div>
			</div>

			<!-- 투표 설명 + 항목 -->
			<div class="rounded-2xl border border-slate-200 bg-slate-100 p-6">
				<div class="mb-8">
					<h3 class="text-xl font-bold text-slate-900 mb-3">투표 내용</h3>
					<p class="text-sm text-slate-700 leading-7 whitespace-pre-line">
						<c:out value="${poll.polldetail}" />
					</p>
				</div>

				<div>
					<h3 class="text-xl font-bold text-slate-900 mb-4">투표 항목</h3>

					<div class="space-y-3">
						<c:forEach items="${pollContentList}" var="item">
							<form method="post" action="${pageContext.request.contextPath}/chat/poll/vote">
								<input type="hidden" name="roomId" value="${roomId}">
								<input type="hidden" name="pollId" value="${poll.seq}">
								<input type="hidden" name="pollContentId" value="${item.seq}">
					
								<button type="submit"
									class="w-full flex items-center justify-between gap-4 rounded-xl border border-slate-200 bg-white px-5 py-4 text-left hover:border-violet-300 hover:bg-violet-50 transition">
									<span class="font-semibold text-slate-800 truncate">
										<c:out value="${item.pollContent}" />
									</span>
					
									<span class="shrink-0 rounded-full bg-violet-100 text-violet-700 text-xs font-semibold px-3 py-1">
										투표하기
									</span>
								</button>
							</form>
						</c:forEach>
					</div>
				</div>
			</div>

			<!-- 참여 내역 -->
			<div class="rounded-2xl border border-slate-200 bg-white p-6">
				<div class="flex items-center justify-between mb-5">
					<h3 class="text-xl font-bold text-slate-900">참여 내역</h3>
					<span class="rounded-full bg-violet-100 text-violet-700 text-sm font-semibold px-3 py-1">
						총 <c:out value="${poll.totalParticipantCount}" />명 참여
					</span>
				</div>

				<div class="space-y-3">
					<c:forEach items="${pollContentList}" var="item">
						<div>
							<div class="flex items-center justify-between text-sm mb-1">
								<span class="font-medium text-slate-700">
									<c:out value="${item.pollContent}" />
								</span>
								<span class="text-slate-500">
									<c:out value="${item.voteCount}" />명
								</span>
							</div>

							<div class="w-full h-3 rounded-full bg-slate-100 overflow-hidden">
								<c:choose>
									<c:when test="${poll.totalParticipantCount > 0}">
										<div class="h-full bg-violet-400"
										     style="width: ${item.voteCount * 100 / poll.totalParticipantCount}%;">
										</div>
									</c:when>
									<c:otherwise>
										<div class="h-full bg-violet-400" style="width: 0%;"></div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>

			<!-- 하단 버튼 -->
			<c:if test="${poll.seqMember == 1}">
				<div class="flex justify-end">
					<form method="post"
						  action="${pageContext.request.contextPath}/chat/poll/delete"
						  onsubmit="return confirm('정말 이 투표를 삭제하시겠습니까? 삭제 후 복구할 수 없습니다.');">
			
						<input type="hidden" name="roomId" value="${roomId}">
						<input type="hidden" name="pollId" value="${poll.seq}">
			
						<button type="submit"
							class="px-5 py-2.5 rounded-xl border border-rose-200 bg-rose-50 text-rose-600 text-sm font-semibold hover:bg-rose-100 transition">
							투표 삭제
						</button>
					</form>
				</div>
			</c:if>

		</div>

	</main>
</body>
</html>