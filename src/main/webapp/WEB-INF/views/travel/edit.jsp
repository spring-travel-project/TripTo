<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 수정</title>

    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>

    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css">
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>

    <link rel="stylesheet" href="${cp}/resources/css/travel.css">
</head>
<body>
	
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>
	
	<div class="travel-page">
	    <div class="travel-write-wrap">
	
	        <h1 class="travel-write-title">게시글 수정</h1>
	
	        <form method="post" action="${cp}/travel/edit.do" class="travel-write-form">
	            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
	            <input type="hidden" name="seqTravelPost" value="${dto.seqTravelPost}">
	
	            <!-- 제목 -->
	            <div class="form-group">
	                <label class="form-label">제목</label>
	                <input type="text"
	                       name="title"
	                       value="${dto.title}"
	                       class="input input-bordered w-full"
	                       required>
	            </div>

	            <!-- 🔥 지도 (대표 장소만 수정 가능) -->
	            <div class="form-group">
	                <label class="form-label">만남 장소</label>

	                <div class="travel-map-search">
	                    <input type="text"
	                           id="placeKeyword"
	                           class="input input-bordered w-full"
	                           placeholder="장소명을 검색하세요">

	                    <button type="button" id="btnSearchPlace" class="btn-travel-outline">
	                        검색
	                    </button>
	                </div>

					<div id="map"
					     style="width:100%; height:360px; margin-top:12px; border-radius:14px;"></div>
					
	                <div id="selectedPlaceBox" class="selected-place empty">
				    <div class="place-info">
				        <div class="place-name">장소를 선택해주세요</div>
				    </div>
				</div>

	                <input type="hidden" id="placeName" name="placeName">
	                <input type="hidden" id="address" name="address">
	                <input type="hidden" id="latitude" name="latitude">
	                <input type="hidden" id="longitude" name="longitude">
	                <input type="hidden" id="mapProviderId" name="mapProviderId">
	            </div>
	
	            <!-- 내용 -->
	            <div class="form-group">
				    <label class="form-label">글 내용</label>
				
				    <div id="editor"></div>
				    <input type="hidden" name="content" id="content">
				
				    <textarea id="originContent" style="display:none;"><c:out value="${dto.content}" /></textarea>
				</div>
			
			    <div class="travel-write-actions">
			        <button type="submit" class="btn-travel-outline btn-travel-sm">수정하기</button>
			        <a href="${cp}/travel/detail.do?seqTravelPost=${dto.seqTravelPost}" class="btn-travel-outline btn-travel-sm">취소</a>
			    </div>
	        </form>
	
	    </div>
	</div>
	
	<c:if test="${not empty message}">
	    <script>alert('${message}');</script>
	</c:if>

	<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=cf168dc299fb311b33c67ac55e3af698&libraries=services"></script>
	
	<script>
	    const originContent = document.getElementById('originContent').value;
	
	    const mapContainer = document.getElementById('map');
	
	    const map = new kakao.maps.Map(mapContainer, {
	        center: new kakao.maps.LatLng(37.5665, 126.9780),
	        level: 5
	    });
	
	    const places = new kakao.maps.services.Places();
	    let marker = null;
	
	    setTimeout(function () {
	        map.relayout();

	        const latitude = document.getElementById('latitude').value;
	        const longitude = document.getElementById('longitude').value;

	        if (latitude && longitude) {
	            map.setCenter(new kakao.maps.LatLng(Number(latitude), Number(longitude)));
	        } else {
	            map.setCenter(new kakao.maps.LatLng(37.5665, 126.9780));
	        }
	    }, 100);
	
	    const temp = document.createElement('div');
	    temp.innerHTML = originContent;
	
	    const originLocation = temp.querySelector('.travel-location-data');
	
	    if (originLocation) {
	        const placeName = originLocation.getAttribute('data-place-name');
	        const address = originLocation.getAttribute('data-address');
	        const latitude = originLocation.getAttribute('data-latitude');
	        const longitude = originLocation.getAttribute('data-longitude');
	
	        const position = new kakao.maps.LatLng(Number(latitude), Number(longitude));
	
	        map.setCenter(position);
	        map.setLevel(3);
	
	        marker = new kakao.maps.Marker({
	            map: map,
	            position: position
	        });

	        labelOverlay = new kakao.maps.CustomOverlay({
	            map: map,
	            position: position,
	            content: '<div class="map-marker-label">' + placeName + '</div>',
	            yAnchor: 2.3
	        });
	
	        document.getElementById('placeName').value = placeName;
	        document.getElementById('address').value = address;
	        document.getElementById('latitude').value = latitude;
	        document.getElementById('longitude').value = longitude;
	
	        document.getElementById('selectedPlaceBox').innerText =
	            '선택된 장소: ' + placeName + ' / ' + address;
	    }
	
	    document.getElementById('btnSearchPlace').addEventListener('click', function () {
	        const keyword = document.getElementById('placeKeyword').value.trim();
	
	        if (!keyword) {
	            alert('장소명을 입력하세요.');
	            return;
	        }
	
	        places.keywordSearch(keyword, function (data, status) {
	            if (status !== kakao.maps.services.Status.OK || data.length === 0) {
	                alert('검색 결과가 없습니다.');
	                return;
	            }
	
	            const place = data[0];
	            const lat = place.y;
	            const lng = place.x;
	            const position = new kakao.maps.LatLng(lat, lng);
	
	            map.setCenter(position);
	            map.setLevel(3);
	
	            if (marker) {
	                marker.setMap(null);
	            }
	
	            marker = new kakao.maps.Marker({
	                map: map,
	                position: position
	            });
	
	            document.getElementById('placeName').value = place.place_name;
	            document.getElementById('address').value = place.road_address_name || place.address_name;
	            document.getElementById('latitude').value = lat;
	            document.getElementById('longitude').value = lng;
	            document.getElementById('mapProviderId').value = place.id;
	
	            document.getElementById('selectedPlaceBox').innerText =
	                '선택된 장소: ' + place.place_name + ' / ' + (place.road_address_name || place.address_name);
	        });
	    });
	
	    const editor = new toastui.Editor({
	        el: document.querySelector('#editor'),
	        height: '700px',
	        initialEditType: 'wysiwyg',
	        previewStyle: 'vertical',
	        initialValue: originContent || '',

	        // 🔥 여기 추가
	        hooks: {
	            addImageBlobHook: async (blob, callback) => {

	                const formData = new FormData();
	                formData.append('attach', blob);

	                const response = await fetch('${cp}/travel/imageUpload.do', {
	                    method: 'POST',
	                    headers: {
	                        'X-CSRF-TOKEN': '${_csrf.token}'
	                    },
	                    body: formData
	                });

	                const result = await response.json();

	                callback(result.url, '이미지');
	            }
	        }
	    });
	
	    document.querySelector('.travel-write-form').addEventListener('submit', function () {
	        const contentBox = document.createElement('div');
	        contentBox.innerHTML = editor.getHTML();
	
	        const oldLocationData = contentBox.querySelector('.travel-location-data');
	        if (oldLocationData) {
	            oldLocationData.remove();
	        }
	
	        const placeName = document.getElementById('placeName').value;
	        const address = document.getElementById('address').value;
	        const latitude = document.getElementById('latitude').value;
	        const longitude = document.getElementById('longitude').value;
	        const mapProviderId = document.getElementById('mapProviderId').value;
	
	        if (placeName && latitude && longitude) {
	            const locationDiv = document.createElement('div');
	            locationDiv.className = 'travel-location-data';
	            locationDiv.setAttribute('data-place-name', placeName);
	            locationDiv.setAttribute('data-address', address);
	            locationDiv.setAttribute('data-latitude', latitude);
	            locationDiv.setAttribute('data-longitude', longitude);
	            locationDiv.setAttribute('data-map-provider-id', mapProviderId);
	            locationDiv.style.display = 'none';
	
	            contentBox.appendChild(locationDiv);
	        }
	
	        document.getElementById('content').value = contentBox.innerHTML;
	    });
	</script>

</body>
</html>