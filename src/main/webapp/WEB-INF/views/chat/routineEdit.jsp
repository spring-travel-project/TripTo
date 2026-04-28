<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 일정 수정</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>

	<main class="page-wrap">

		<div class="mb-8">
			<h1 class="section-title">일정 수정</h1>
			<p class="section-desc">채팅방에 공유한 여행 일정을 수정하세요.</p>
		</div>

		<div class="content-card card-pad">
			<form method="post"
			      action="${pageContext.request.contextPath}/chat/routine/edit"
			      class="space-y-6">

				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
				<input type="hidden" name="seq" value="${routine.seq}">
				<input type="hidden" name="seqChattingroom" value="${roomId}">

				<div>
					<label for="title" class="block text-sm font-semibold text-slate-700 mb-2">
						일정 제목
					</label>
					<input
						type="text"
						id="title"
						name="title"
						value="${routine.title}"
						class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
						required>
				</div>

				<div>
					<label for="detail" class="block text-sm font-semibold text-slate-700 mb-2">
						일정 내용
					</label>
					<textarea
						id="detail"
						name="detail"
						rows="14"
						class="w-full resize-none rounded-2xl border border-slate-300 bg-slate-50 px-5 py-4 text-sm leading-6 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
						required>${routine.detail}</textarea>
					
					<div class="mt-6">
					    <label for="dDay" class="block text-sm font-semibold text-slate-700 mb-2">
					        종료 날짜
					    </label>
					
					    <input
					        type="date"
					        id="dDay"
					        name="dDayInput"
					        value="${routine.dDayDateText}"
					        class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400">
					
					    <p class="mt-2 text-xs text-slate-400">
					        날짜를 선택하지 않으면 종료일은 설정되지 않습니다.
					    </p>
					</div>
					
					<div>
						<label class="block text-sm font-semibold text-slate-700 mb-2">
							목적지 선택
						</label>
					
						<div class="grid grid-cols-1 lg:grid-cols-[1fr_320px] gap-4">
							<div>
								<div id="map" class="w-full h-[360px] rounded-2xl border border-slate-200 bg-slate-100"></div>
							</div>
					
							<div class="space-y-3">
								<div class="flex gap-2">
									<input
										type="text"
										id="keyword"
										placeholder="장소를 검색하세요."
										class="flex-1 rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400">
					
									<button
										type="button"
										id="searchBtn"
										class="px-4 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition">
										검색
									</button>
								</div>
					
								<div id="placeList" class="h-[300px] overflow-y-auto space-y-2"></div>
							</div>
						</div>
					
						<div class="mt-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
							<p class="text-sm font-semibold text-slate-700 mb-1">선택한 목적지</p>
							<p id="selectedPlaceText" class="text-sm text-slate-500">
								<c:choose>
									<c:when test="${not empty routine.placeName}">
										<c:out value="${routine.placeName}" /> / <c:out value="${routine.address}" />
									</c:when>
									<c:otherwise>
										선택된 장소가 없습니다.
									</c:otherwise>
								</c:choose>
							</p>
						</div>
					
						<input type="hidden" id="placeName" name="placeName" value="${routine.placeName}">
						<input type="hidden" id="address" name="address" value="${routine.address}">
						<input type="hidden" id="latitude" name="latitude" value="${routine.latitude}">
						<input type="hidden" id="longitude" name="longitude" value="${routine.longitude}">
						<input type="hidden" id="mapProviderId" name="mapProviderId" value="${routine.mapProviderId}">
					</div>
				</div>

				<div class="flex justify-end gap-3">
					<button
						type="submit"
						class="px-5 py-2.5 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition">
						수정 완료
					</button>

					<a href="${pageContext.request.contextPath}/chat/routine/detail?roomId=${roomId}&routineId=${routine.seq}"
					   class="px-5 py-2.5 rounded-xl border border-slate-300 bg-white text-slate-700 text-sm font-semibold hover:bg-slate-100 transition">
						취소
					</a>
				</div>
			</form>
		</div>

	</main>
	
	<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=1cf6e908ad168b1d10c9952edc015a7d&libraries=services"></script>

	<script>
		let map;
		let marker;
		let places;
	
		window.addEventListener('load', function () {
			const savedLat = '${routine.latitude}';
			const savedLng = '${routine.longitude}';
	
			const defaultPosition = savedLat && savedLng
				? new kakao.maps.LatLng(Number(savedLat), Number(savedLng))
				: new kakao.maps.LatLng(37.566826, 126.9786567);
	
			map = new kakao.maps.Map(document.getElementById('map'), {
				center: defaultPosition,
				level: 4
			});
	
			marker = new kakao.maps.Marker({
				position: defaultPosition
			});
	
			if (savedLat && savedLng) {
				marker.setMap(map);
			}
	
			places = new kakao.maps.services.Places();
	
			document.getElementById('searchBtn').addEventListener('click', searchPlaces);
	
			document.getElementById('keyword').addEventListener('keydown', function (e) {
				if (e.key === 'Enter') {
					e.preventDefault();
					searchPlaces();
				}
			});
		});
	
		function searchPlaces() {
			const keyword = document.getElementById('keyword').value.trim();
	
			if (!keyword) {
				alert('검색어를 입력하세요.');
				return;
			}
	
			places.keywordSearch(keyword, function (data, status) {
				if (status === kakao.maps.services.Status.OK) {
					displayPlaceList(data);
				} else {
					alert('검색 결과가 없습니다.');
				}
			});
		}
	
		function displayPlaceList(placesData) {
			const placeList = document.getElementById('placeList');
			placeList.innerHTML = '';
	
			placesData.forEach(function (place) {
				const item = document.createElement('button');
				item.type = 'button';
				item.className = 'w-full text-left rounded-xl border border-slate-200 bg-white px-4 py-3 hover:bg-sky-50 hover:border-sky-300 transition';
	
				item.innerHTML =
					'<p class="font-semibold text-sm text-slate-800">' + escapeHtml(place.place_name) + '</p>' +
					'<p class="text-xs text-slate-500 mt-1">' + escapeHtml(place.road_address_name || place.address_name || '') + '</p>';
	
				item.addEventListener('click', function () {
					selectPlace(place);
				});
	
				placeList.appendChild(item);
			});
		}
	
		function selectPlace(place) {
			const lat = Number(place.y);
			const lng = Number(place.x);
			const position = new kakao.maps.LatLng(lat, lng);
	
			map.setCenter(position);
			marker.setPosition(position);
			marker.setMap(map);
	
			document.getElementById('placeName').value = place.place_name;
			document.getElementById('address').value = place.road_address_name || place.address_name || '';
			document.getElementById('latitude').value = lat;
			document.getElementById('longitude').value = lng;
			document.getElementById('mapProviderId').value = place.id;
	
			document.getElementById('selectedPlaceText').innerText =
				place.place_name + ' / ' + (place.road_address_name || place.address_name || '');
		}
	
		function escapeHtml(str) {
			if (!str) return '';
			return str
				.replace(/&/g, '&amp;')
				.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;')
				.replace(/"/g, '&quot;')
				.replace(/'/g, '&#39;');
		}
	</script>
	
</body>
</html>