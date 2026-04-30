<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 일정 등록</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
	
	<style>
		.file-item {
		    display: inline-flex;
		    align-items: center;
		    gap: 8px;
		    margin-left: 10px;
		    padding: 0;
		    background: transparent;
		    border-radius: 0;
		    font-size: 14px;
		    color: #334155;
		    font-weight: 500;
		}
		
		.file-remove {
		    cursor: pointer;
		    color: #ef4444;
		    font-weight: bold;
		    margin-left: 4px;
		    border: 0;
		    background: transparent;
		}
	</style>

</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>

	<main class="page-wrap">

		<div class="mb-8">
			<h1 class="section-title">일정 등록</h1>
			<p class="section-desc">채팅방에서 공유할 여행 일정을 작성하세요.</p>
		</div>

		<div class="content-card card-pad">
			<form method="post"
			      action="${pageContext.request.contextPath}/chat/routine/write?${_csrf.parameterName}=${_csrf.token}"
			      enctype="multipart/form-data"
			      class="space-y-6">
			
			    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			    <input type="hidden" name="seqChattingroom" value="${roomId}">

				<div>
					<label for="title" class="block text-sm font-semibold text-slate-700 mb-2">
						일정 제목
					</label>
					<input
						type="text"
						id="title"
						name="title"
						placeholder="일정 제목을 입력하세요."
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
						placeholder="일정 내용을 입력하세요."
						class="w-full resize-none rounded-2xl border border-slate-300 bg-slate-50 px-5 py-4 text-sm leading-6 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
						required></textarea>
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
							<div class="flex items-center gap-3 mt-2">
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
						<p id="selectedPlaceText" class="text-sm text-slate-500">선택된 장소가 없습니다.</p>
					</div>
				
					<input type="hidden" id="placeName" name="placeName">
					<input type="hidden" id="address" name="address">
					<input type="hidden" id="latitude" name="latitude">
					<input type="hidden" id="longitude" name="longitude">
					<input type="hidden" id="mapProviderId" name="mapProviderId">
				</div>

				<div>
					<label for="dDay" class="block text-sm font-semibold text-slate-700 mb-2">
						일정 날짜
					</label>
					<input
						type="date"
						id="dDay"
						name="dDayInput"
						class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400">
					<p class="mt-2 text-xs text-slate-400">
						선택하지 않으면 일정 상태가 ‘예정’으로 표시됩니다.
					</p>
				</div>

				<div class="rounded-2xl border border-slate-200 bg-slate-50 px-5 py-6 shadow-sm">
				    <p class="text-sm font-semibold text-slate-700 mb-3">첨부파일</p>
				
				    <input type="file"
				           id="fileInput"
				           name="files"
				           multiple
				           style="display:none;"
				           onchange="showSelectedFiles(this)">
				
				    <label for="fileInput"
				           class="inline-flex cursor-pointer px-4 py-2 rounded-lg bg-sky-100 text-sky-700 text-sm font-semibold hover:bg-sky-200 transition">
				        파일 선택
				    </label>
				
				    <div id="fileList" class="mt-3 flex flex-col items-start gap-2 text-sm text-slate-700"></div>
				
				    <p class="mt-3 text-xs text-slate-400">
				        여러 개 파일 업로드 가능합니다.
				    </p>
				</div>

				<div class="flex justify-end gap-3">
					<button
						type="submit"
						class="px-5 py-2.5 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition">
						등록
					</button>

					<a href="${pageContext.request.contextPath}/chat/schedulePoll?roomId=${roomId}"
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
			const defaultPosition = new kakao.maps.LatLng(37.566826, 126.9786567);
	
			map = new kakao.maps.Map(document.getElementById('map'), {
				center: defaultPosition,
				level: 4
			});
	
			marker = new kakao.maps.Marker({
				position: defaultPosition
			});
	
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
		
		let filesArray = [];

		function showSelectedFiles(input) {
		    const newFiles = Array.from(input.files);

		    newFiles.forEach(function(file) {
		        filesArray.push(file);
		    });

		    syncFileInput(input);
		    renderFiles();

		}

		function renderFiles() {
		    const fileListDiv = document.getElementById('fileList');
		    fileListDiv.innerHTML = '';

		    filesArray.forEach(function(file, index) {
		        const item = document.createElement('span');
		        item.className = 'file-item';

		        const name = document.createElement('span');
		        name.textContent = file.name;

		        const remove = document.createElement('button');
		        remove.type = 'button';
		        remove.className = 'file-remove';
		        remove.textContent = '×';
		        remove.onclick = function() {
		            removeFile(index);
		        };

		        item.appendChild(name);
		        item.appendChild(remove);
		        fileListDiv.appendChild(item);
		    });
		}

		function removeFile(index) {
		    const fileInput = document.getElementById('fileInput');

		    filesArray.splice(index, 1);

		    syncFileInput(fileInput);
		    renderFiles();
		}

		function syncFileInput(input) {
		    const dt = new DataTransfer();

		    filesArray.forEach(function(file) {
		        dt.items.add(file);
		    });

		    input.files = dt.files;
		}
	</script>
</body>
</html>