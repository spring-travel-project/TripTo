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

            <div class="travel-detail-content">
                <c:out value="${dto.content}" escapeXml="false" />
            </div>

            <div id="detailMapBox" style="display:none; margin-top:24px; position:relative;">
                <div id="detailMap" style="width:100%; height:360px; border-radius:14px;"></div>

                <div id="emptyMapOverlay"
                     style="display:none; position:absolute; inset:0; z-index:10;
                            background:rgba(255,255,255,0.72);
                            border-radius:14px; align-items:center; justify-content:center;
                            font-weight:700; color:#475569; font-size:16px;
                            pointer-events:none;">
                    등록된 위치가 없습니다.
                </div>
            </div>

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

                                <div class="travel-comment-buttons">
                                    <c:if test="${comment.seqMember == currentSeqMember}">
                                        <button type="button"
                                                class="travel-comment-link"
                                                onclick="toggleCommentEdit(${comment.seqTravelComment});">
                                            수정
                                        </button>
                                    </c:if>

                                    <c:if test="${comment.seqMember == currentSeqMember || isAdmin}">
                                        <form method="post"
                                              action="${cp}/travel/comment/delete.do"
                                              class="travel-comment-delete-form"
                                              onsubmit="return confirm('댓글을 삭제하시겠습니까?');">

                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                            <input type="hidden" name="seqTravelComment" value="${comment.seqTravelComment}">
                                            <input type="hidden" name="seqTravelPost" value="${dto.seqTravelPost}">

                                            <button type="submit" class="travel-comment-link travel-comment-delete">
                                                삭제
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>

                            <div id="comment-content-${comment.seqTravelComment}"
                                 style="text-align:left; margin-top:10px; width:100%;">
                                ${comment.content}
                            </div>

                            <form method="post"
                                  action="${cp}/travel/comment/edit.do"
                                  class="travel-comment-edit-form"
                                  id="comment-edit-${comment.seqTravelComment}"
                                  style="display:none;">

                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                <input type="hidden" name="seqTravelComment" value="${comment.seqTravelComment}">
                                <input type="hidden" name="seqTravelPost" value="${dto.seqTravelPost}">

                                <textarea name="content" class="travel-comment-textarea" required>${comment.content}</textarea>

                                <div class="travel-comment-form-actions">
                                    <button type="button"
                                            class="btn-travel-outline btn-travel-sm btn-travel-outline-light"
                                            onclick="toggleCommentEdit(${comment.seqTravelComment});">
                                        취소
                                    </button>

                                    <button type="submit" class="btn-travel-outline btn-travel-sm">
                                        저장
                                    </button>
                                </div>
                            </form>
                        </div>
                    </c:forEach>
                </div>
                
                <!-- 🔥 채팅 버튼 추가 -->
	            <c:if test="${not isWriter}">
				    <div style="margin:20px 0;">
				        <button type="button"
						        class="btn-travel-outline btn-travel-sm btn-chat-hover"
						        style="width:100%; margin:20px 0;"
						        onclick="location.href='${cp}/chat/start?targetSeq=${dto.seqMember}'">
						    채팅 보내기
						</button>
				    </div>
				</c:if>

                <div class="travel-detail-actions">

                    <div class="travel-detail-action-left" style="display:flex; gap:10px; align-items:center;">
                        <a href="${cp}/travel/list.do" class="btn-travel-outline btn-travel-sm">
                            목록으로
                        </a>

                        <c:if test="${isAdmin}">
                            <c:choose>
                                <c:when test="${isRecommended eq true}">
                                    <form method="post" action="${cp}/main/recommend/remove.do" style="display:inline;">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                        <input type="hidden" name="targetType" value="TRAVEL">
                                        <input type="hidden" name="seqTarget" value="${dto.seqTravelPost}">
                                        <button type="submit" class="btn-travel-outline btn-travel-sm btn-travel-danger">
										    이 게시글 해제하기
										</button>
                                    </form>
                                </c:when>

                                <c:otherwise>
                                    <form method="post" action="${cp}/main/recommend/add.do" style="display:inline;">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                        <input type="hidden" name="targetType" value="TRAVEL">
                                        <input type="hidden" name="seqTarget" value="${dto.seqTravelPost}">
                                        <button type="submit" class="btn-travel-outline btn-travel-sm">
                                            이 게시글 등록하기
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </div>

                    <div class="travel-detail-action-right">
                        <c:if test="${!isWriter}">
                            <form method="post"
                                  action="${cp}/report/add.do"
                                  class="travel-report-form"
                                  onsubmit="return confirm('이 게시글을 신고하시겠습니까?');">

                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                <input type="hidden" name="targetType" value="TRAVEL">
                                <input type="hidden" name="seqTarget" value="${dto.seqTravelPost}">

                                <button type="submit" class="btn-travel-outline btn-travel-sm btn-travel-warning">
                                    신고
                                </button>
                            </form>
                        </c:if>

                        <c:if test="${isWriter}">
                            <a href="${cp}/travel/edit.do?seqTravelPost=${dto.seqTravelPost}"
                               class="btn-travel-outline btn-travel-sm">
                                수정
                            </a>
                        </c:if>

                        <c:if test="${isWriter or isAdmin}">
                            <form method="post"
                                  action="${cp}/travel/delete.do"
                                  class="travel-delete-form"
                                  onsubmit="return confirm('게시글을 삭제하시겠습니까?');">

                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                <input type="hidden" name="seqTravelPost" value="${dto.seqTravelPost}">

                                <button type="submit" class="btn-travel-outline btn-travel-sm btn-travel-danger">
                                    삭제
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </section>

        </article>
    </div>
</div>

<c:if test="${not empty message}">
    <script>alert('${message}');</script>
</c:if>

<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=cf168dc299fb311b33c67ac55e3af698&libraries=services"></script>

<script>
    const mapBox = document.getElementById('detailMapBox');
    const mapEl = document.getElementById('detailMap');
    const emptyMapOverlay = document.getElementById('emptyMapOverlay');

    const locations = [];

    const locationData = document.querySelector('.travel-location-data');

    if (locationData) {
        const lat = Number(locationData.dataset.latitude);
        const lng = Number(locationData.dataset.longitude);

        if (!isNaN(lat) && !isNaN(lng)) {
            locations.push({
                name: locationData.dataset.placeName || '대표 장소',
                lat: lat,
                lng: lng
            });
        }
    }

    <c:forEach items="${locationList}" var="loc">
        <c:if test="${not empty loc.latitude and not empty loc.longitude}">
            locations.push({
                name: "${loc.placeName}",
                lat: Number("${loc.latitude}"),
                lng: Number("${loc.longitude}")
            });
        </c:if>
    </c:forEach>

    mapBox.style.display = 'block';

    setTimeout(function () {
        let center;

        if (locations.length > 0) {
            center = new kakao.maps.LatLng(locations[0].lat, locations[0].lng);
            emptyMapOverlay.style.display = 'none';
        } else {
            center = new kakao.maps.LatLng(37.5665, 126.9780);
            emptyMapOverlay.style.display = 'flex';
        }

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

        map.relayout();

        if (locations.length > 1) {
            map.setBounds(bounds);
        } else {
            map.setCenter(center);
        }
    }, 100);

    function toggleCommentEdit(seq) {
        const content = document.getElementById('comment-content-' + seq);
        const form = document.getElementById('comment-edit-' + seq);

        if (form.style.display === 'none') {
            form.style.display = 'block';
            content.style.display = 'none';
        } else {
            form.style.display = 'none';
            content.style.display = 'block';
        }
    }
</script>

</body>
</html>