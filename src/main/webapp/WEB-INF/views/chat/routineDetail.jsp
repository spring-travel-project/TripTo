<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 일정 상세</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
	<style>
		.file-container {
		    display: flex;
		    flex-wrap: wrap;
		    gap: 16px;
		}
		
		.file-image {
		    width: 450px;
		    height: auto;
		    object-fit: cover;
		    border-radius: 16px;
		}
	</style>
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
					<div class="text-right">
					    <p>
					        작성일 <c:out value="${routine.regdateText}" />
					    </p>
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
			</div>

			<!-- 일정 내용 -->
			<div class="rounded-2xl border border-slate-200 bg-slate-100 p-8 min-h-[360px]">
				<h3 class="text-xl font-bold text-slate-900 mb-5">일정 내용</h3>

				<p class="text-sm text-slate-700 leading-7 whitespace-pre-line">
					<c:out value="${routine.detail}" />
				</p>
				
				<div class="file-container mt-4">
				    <c:forEach items="${fileList}" var="file">
				        <c:if test="${not empty file.filePath}">
				            <img src="${pageContext.request.contextPath}${file.filePath}"
				                 class="file-image border border-slate-200 shadow-sm">
				        </c:if>
				    </c:forEach>
				</div>
				
				<c:if test="${not empty routine.placeName}">
					<div class="mt-8 rounded-2xl border border-slate-200 bg-white p-5">
						<p class="text-sm font-semibold text-slate-700 mb-1">목적지</p>
						<p class="text-base font-bold text-slate-900">
							<c:out value="${routine.placeName}" />
						</p>
						<p class="text-sm text-slate-500 mt-1 mb-4">
							<c:out value="${routine.address}" />
						</p>
				
						<div id="detailMap"
						     class="w-full h-[360px] rounded-2xl border border-slate-200 bg-slate-100">
						</div>
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
	
	<c:if test="${not empty routine.latitude and not empty routine.longitude}">
		<script type="text/javascript"
			src="//dapi.kakao.com/v2/maps/sdk.js?appkey=1cf6e908ad168b1d10c9952edc015a7d&libraries=services">
		</script>
	
		<script>
			window.addEventListener('load', function () {
				const lat = Number('${routine.latitude}');
				const lng = Number('${routine.longitude}');
	
				const position = new kakao.maps.LatLng(lat, lng);
	
				const map = new kakao.maps.Map(document.getElementById('detailMap'), {
					center: position,
					level: 4
				});
	
				const marker = new kakao.maps.Marker({
					position: position,
					map: map
				});
	
				const infoWindow = new kakao.maps.InfoWindow({
					content: '<div style="padding:8px 12px;font-size:13px;">'
						+ '<strong><c:out value="${routine.placeName}" /></strong><br>'
						+ '<span><c:out value="${routine.address}" /></span>'
						+ '</div>'
				});
	
				infoWindow.open(map, marker);
			});
		</script>
	</c:if>
</body>
</html>