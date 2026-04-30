<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>동행</title>

    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">

    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>

    <link rel="stylesheet" href="${cp}/resources/css/travel.css">
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css">
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
</head>

<body>

<%@ include file="/WEB-INF/views/inc/header.jsp" %>

<div class="travel-write-page">
    <div class="max-w-3xl mx-auto mt-10">

        <h1 class="text-3xl font-bold mb-6">게시글 작성</h1>

        <form method="post"
		      action="${cp}/travel/write.do?${_csrf.parameterName}=${_csrf.token}"
		      class="travel-write-form"
		      enctype="multipart/form-data">

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="form-group">
                <label class="form-label">제목</label>
                <input type="text" name="title" class="input input-bordered w-full" required>
            </div>

            <div class="form-group">
                <label class="form-label">만남 장소</label>

                <div class="travel-map-search">
                    <input type="text"
                           id="placeKeyword"
                           class="input input-bordered w-full"
                           placeholder="장소명을 검색하세요. 예: 강남역">

                    <button type="button" id="btnSearchPlace" class="btn-travel-outline">
                        검색
                    </button>
                </div>

                <div id="map" style="width:100%; height:360px; margin-top:12px; border-radius:14px;"></div>

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

            <div class="form-group">
                <label class="form-label">내용</label>

                <div id="editor"></div>
                <input type="hidden" name="content" id="content">
            </div>

            <div class="flex gap-3">
                <button type="submit" class="btn-travel-outline">등록</button>
                <a href="${cp}/travel/list.do" class="btn-travel-outline btn-travel-outline-light">취소</a>
            </div>
        </form>

    </div>
</div>

<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=cf168dc299fb311b33c67ac55e3af698&libraries=services"></script>

<script>
    const mapContainer = document.getElementById('map');

    const map = new kakao.maps.Map(mapContainer, {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 5
    });

    const places = new kakao.maps.services.Places();
    let marker = null;
    
    document.getElementById('placeKeyword').addEventListener('keydown', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            document.getElementById('btnSearchPlace').click();
        }
    });

    document.getElementById('btnSearchPlace').addEventListener('click', function () {
    	
        const keyword = document.getElementById('placeKeyword').value.trim();

        if (keyword === '') {
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

            let labelOverlay = null;

            if (marker) {
                marker.setMap(null);
            }

            if (labelOverlay) {
                labelOverlay.setMap(null);
            }

            // 마커
            marker = new kakao.maps.Marker({
                map: map,
                position: position
            });

            // 라벨
            labelOverlay = new kakao.maps.CustomOverlay({
                map: map,
                position: position,
                content: '<div class="map-marker-label">' + place.place_name + '</div>',
                yAnchor: 2.3
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

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '700px',
        initialEditType: 'wysiwyg',
        previewStyle: 'vertical',
        initialValue: '',

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
        const html = editor.getHTML();

        const placeName = document.getElementById('placeName').value;
        const address = document.getElementById('address').value;
        const latitude = document.getElementById('latitude').value;
        const longitude = document.getElementById('longitude').value;
        const mapProviderId = document.getElementById('mapProviderId').value;

        let locationHtml = '';

        if (placeName && latitude && longitude) {
            locationHtml =
                '<div class="travel-location-data" ' +
                'data-place-name="' + placeName + '" ' +
                'data-address="' + address + '" ' +
                'data-latitude="' + latitude + '" ' +
                'data-longitude="' + longitude + '" ' +
                'data-map-provider-id="' + mapProviderId + '" ' +
                'style="display:none;"></div>';
        }

        document.getElementById('content').value = html + locationHtml;
    });
    
    function updateSelectedPlace(name, address) {
        const box = document.getElementById("selectedPlaceBox");

        box.classList.remove("empty");

        box.innerHTML = `
            <div class="place-icon">📍</div>
            <div class="place-info">
                <div class="place-title">선택된 장소</div>
                <div class="place-name">${name}</div>
                <div class="place-address">${address}</div>
            </div>
        `;
    }
</script>

</body>
</html>