<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 상세</title>

    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
    <link rel="stylesheet" href="${cp}/resources/css/travel.css">
</head>
<body>

<%@ include file="/WEB-INF/views/inc/header.jsp" %>

<div class="travel-page">
    <div class="travel-detail-wrap">

        <article class="travel-detail-card">

            <!-- 제목 -->
            <div class="travel-detail-header">
                <div>
                    <h1 class="travel-detail-title">${dto.title}</h1>

                    <div class="travel-detail-meta">
                        <span>${dto.writerName}</span>
                        <span>${dto.createDate}</span>
                        <span>조회 ${dto.viewCount}</span>
                    </div>
                </div>
            </div>

            <!-- 내용 -->
            <div class="travel-detail-content">
                <c:out value="${dto.content}" escapeXml="false" />
            </div>

            <!-- 🔥 지도 (텍스트 제거, 지도만) -->
            <div id="detailMapBox" style="display:none; margin-top:24px;">
                <div id="detailMap" style="width:100%; height:360px; border-radius:14px;"></div>
            </div>

            <!-- 댓글 -->
            <section class="travel-comment-box">
                <div class="travel-comment-title">댓글</div>

                <form method="post" action="${cp}/travel/comment/add.do" class="travel-comment-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <input type="hidden" name="seqTravelPost" value="${dto.seqTravelPost}">

                    <textarea name="content" class="travel-comment-textarea"
                              placeholder="댓글을 입력하세요." required></textarea>

                    <div class="travel-comment-form-actions">
                        <button type="submit" class="btn-travel-outline btn-travel-sm">
                            댓글 등록
                        </button>
                    </div>
                </form>

                <div class="travel-comment-list">
                    <c:if test="${empty commentList}">
                        <div class="travel-comment-empty">
                            아직 등록된 댓글이 없습니다.
                        </div>
                    </c:if>

                    <c:forEach items="${commentList}" var="comment">
                        <div class="travel-comment-item">
                            <div class="travel-comment-head">
                                <div class="travel-comment-info">
                                    <span class="travel-comment-writer">${comment.writerName}</span>
                                    <span class="travel-comment-date">${comment.createDate}</span>
                                </div>
                            </div>
                            <div>${comment.content}</div>
                        </div>
                    </c:forEach>
                </div>
            </section>

        </article>
    </div>
</div>

<c:if test="${not empty message}">
    <script>alert('${message}');</script>
</c:if>

<!-- 🔥 지도 스크립트 -->
<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=cf168dc299fb311b33c67ac55e3af698&libraries=services"></script>

<script>
    const mapBox = document.getElementById('detailMapBox');
    const mapEl = document.getElementById('detailMap');

    const locations = [];

    // 글 작성 시 content 안에 저장한 대표 장소
    const locationData = document.querySelector('.travel-location-data');

    if (locationData) {
        locations.push({
            name: locationData.dataset.placeName,
            lat: Number(locationData.dataset.latitude),
            lng: Number(locationData.dataset.longitude)
        });
    }

    // 일정에 연결된 장소들
    <c:forEach items="${locationList}" var="loc">
        locations.push({
            name: "${loc.placeName}",
            lat: ${loc.latitude},
            lng: ${loc.longitude}
        });
    </c:forEach>

    if (locations.length > 0) {
        mapBox.style.display = 'block';

        setTimeout(function () {
            const first = locations[0];
            const center = new kakao.maps.LatLng(first.lat, first.lng);

            const map = new kakao.maps.Map(mapEl, {
                center: center,
                level: 5
            });

            const bounds = new kakao.maps.LatLngBounds();

            locations.forEach(function (loc) {
                const position = new kakao.maps.LatLng(loc.lat, loc.lng);

                new kakao.maps.Marker({
                    map: map,
                    position: position,
                    title: loc.name
                });

                bounds.extend(position);
            });

            map.setBounds(bounds);
            map.relayout();
        }, 100);
    }
</script>

</body>
</html>