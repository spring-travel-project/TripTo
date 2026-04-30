<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 일정/투표</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>

	<main class="page-wrap">

		<div class="mb-8 flex items-end justify-between gap-4">
			
			<c:choose>
			    <c:when test="${not empty selectedRoom and not empty selectedRoom.roomName}">
			        <h1 class="section-title">${selectedRoom.roomName}</h1>
			    </c:when>
			
			    <c:otherwise>
			        <h1 class="section-title">이름없는 채팅방</h1>
			    </c:otherwise>
			</c:choose>
			

			<a href="${pageContext.request.contextPath}/chat/list?roomId=${roomId}"
			   class="px-4 py-2 rounded-xl border border-slate-300 bg-white text-sm font-medium hover:bg-slate-100 transition">
				채팅방으로 돌아가기
			</a>
		</div>

		<div class="content-card p-0 overflow-hidden">
			<div class="grid grid-cols-1 xl:grid-cols-2 min-h-[720px]">

				<!-- 왼쪽: 일정 -->
				<section class="border-b xl:border-b-0 xl:border-r border-slate-200 bg-slate-50">
					<div class="p-6 border-b border-slate-200 bg-white flex items-center justify-between">
						<div>
							<h2 class="text-2xl font-bold tracking-tight">일정</h2>
							<p class="text-sm text-slate-500 mt-1">채팅방에 등록된 여행 일정을 확인할 수 있습니다.</p>
						</div>

						<a href="${pageContext.request.contextPath}/chat/routine/write?roomId=${roomId}"
						   class="px-4 py-2 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition">
							일정 등록
						</a>
					</div>

					<div class="p-5 space-y-4 h-[640px] overflow-y-auto">
						<c:choose>
							<c:when test="${not empty routineList}">
								<c:forEach items="${routineList}" var="routine">
									<a href="${pageContext.request.contextPath}/chat/routine/detail?roomId=${roomId}&routineId=${routine.seq}"
   									class="block rounded-2xl border border-slate-200 bg-white p-5 shadow-sm hover:shadow-md transition">
										<div class="flex items-start justify-between gap-3 mb-4">
											<div class="min-w-0">
												<div class="flex items-center gap-2 mb-2 flex-wrap">
													<h3 class="font-bold text-lg text-slate-900 truncate">
														<c:out value="${routine.title}" />
													</h3>
													<span class="inline-flex items-center rounded-full bg-sky-100 text-sky-700 text-xs font-medium px-2 py-0.5">
														<c:out value="${routine.dDayText}" />
													</span>
												</div>
												<p class="text-sm text-slate-500">
													작성자:
													<c:out value="${routine.writerNickname}" />
												</p>
											</div>

											<div class="text-xs text-slate-400 whitespace-nowrap text-right">
											    <p>작성일 <c:out value="${routine.regdateText}" /></p>
											    <p>
											        종료일
											        <c:choose>
											            <c:when test="${not empty routine.dDayDateText}">
											                <c:out value="${routine.dDayDateText}" />
											            </c:when>
											            <c:otherwise>
											                미정
											            </c:otherwise>
											        </c:choose>
											    </p>
											</div>
										</div>

										<div class="w-20 h-20 rounded-xl bg-slate-200 shrink-0 overflow-hidden flex items-center justify-center text-xs text-slate-500">
										    <c:choose>
										        <c:when test="${not empty routine.filePath}">
										            <img src="${fn:startsWith(routine.filePath, 'http') 
													    ? routine.filePath 
													    : pageContext.request.contextPath.concat(routine.filePath)}"
										                 class="w-full h-full object-cover">
										        </c:when>
										        <c:otherwise>
										            <div class="text-center">
										                첨부<br>사진<br>
										            </div>
										        </c:otherwise>
										    </c:choose>
										</div>
									</a>
								</c:forEach>
							</c:when>

							<c:otherwise>
								<div class="rounded-2xl border border-slate-200 bg-white p-8 text-center text-slate-500">
									등록된 일정이 없습니다.
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</section>

				<!-- 오른쪽: 투표 -->
				<section class="bg-slate-50">
					<div class="p-6 border-b border-slate-200 bg-white flex items-center justify-between">
						<div>
							<h2 class="text-2xl font-bold tracking-tight">투표</h2>
							<p class="text-sm text-slate-500 mt-1">일정, 장소, 시간 등을 투표로 결정하세요.</p>
						</div>

						<a href="${pageContext.request.contextPath}/chat/poll/write?roomId=${roomId}"
						   class="px-4 py-2 rounded-xl bg-violet-500 text-white text-sm font-semibold hover:bg-violet-600 transition">
							투표 등록
						</a>
					</div>

					<div class="p-5 space-y-4 h-[640px] overflow-y-auto">
						<c:choose>
							<c:when test="${not empty pollList}">
								<c:forEach items="${pollList}" var="poll">
									<a href="${pageContext.request.contextPath}/chat/poll/detail?roomId=${roomId}&pollId=${poll.seq}"
									   class="block rounded-2xl border border-slate-200 bg-white p-5 shadow-sm hover:shadow-md transition">
										<div class="flex items-start justify-between gap-3 mb-4">
											<div class="min-w-0">
												<div class="flex items-center gap-2 mb-2 flex-wrap">
													<h3 class="font-bold text-lg text-slate-900 truncate">
														<c:out value="${poll.pollTitle}" />
													</h3>

													<c:choose>
													    <c:when test="${poll.pollClosed == 1}">
													        <span class="inline-flex items-center rounded-full bg-slate-100 text-slate-600 text-xs font-medium px-2 py-0.5">
													            종료
													        </span>
													    </c:when>
													    <c:otherwise>
													        <span class="inline-flex items-center rounded-full bg-violet-100 text-violet-700 text-xs font-medium px-2 py-0.5">
													            진행중
													        </span>
													    </c:otherwise>
													</c:choose>
												</div>

												<p class="text-sm text-slate-500">
													작성자:
													<c:out value="${poll.writerNickname}" />
												</p>
											</div>

											<span class="text-xs text-slate-400 whitespace-nowrap">
												마감 <c:out value="${poll.enddateText}" />
											</span>
										</div>

										<div class="flex gap-4">
											<div class="flex-1 rounded-2xl bg-slate-100 border border-slate-200 px-5 py-4">
												<p class="text-base font-semibold text-slate-800 mb-2">투표 내용</p>
												<p class="text-sm text-slate-600 leading-6 whitespace-pre-line">
													<c:out value="${poll.polldetail}" />
												</p>
											</div>

											<button type="button"
												class="w-16 rounded-xl border border-violet-200 bg-violet-50 text-violet-600 text-sm font-semibold hover:bg-violet-100 transition shrink-0">
												참여<br>인원<br>
												<c:out value="${poll.participantCount}" />명
											</button>
										</div>
									</a>
								</c:forEach>
							</c:when>

							<c:otherwise>
								<div class="rounded-2xl border border-slate-200 bg-white p-8 text-center text-slate-500">
									등록된 투표가 없습니다.
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</section>

			</div>
		</div>

	</main>
</body>
</html>