<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>동행</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
    <link rel="stylesheet" href="${cp}/resources/css/travel.css">
</head>
<body>

    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <div class="travel-page">
        <div class="travel-wrap">

            <div class="travel-top">
                <h1 class="travel-title">동행</h1>

                <a href="${cp}/travel/write.do" class="btn-travel-outline btn-travel-sm">
                    글쓰기
                </a>
            </div>

            <!-- 검색 -->
            <form method="get" action="${cp}/travel/list.do" class="travel-search-form">

                <input type="text"
                       name="searchWord"
                       value="${searchWord}"
                       placeholder="제목 또는 내용으로 검색해보세요."
                       class="travel-search-input">

                <button type="submit" class="btn-travel-outline btn-travel-sm">
				    검색
				</button>
            </form>

            <!-- 목록 -->
            <div class="travel-list-card-wrap">

                <c:if test="${empty list}">
                    <div class="travel-empty-box">
                        <div class="travel-empty-icon">📝</div>
                        <div class="travel-empty-title">게시글이 없습니다.</div>
                        <div class="travel-empty-desc">
                            아직 등록된 게시글이 없거나 검색 조건에 맞는 결과가 없습니다.
                        </div>
                    </div>
                </c:if>

                <c:forEach items="${list}" var="dto">
                    <a href="${cp}/travel/detail.do?seqTravelPost=${dto.seqTravelPost}" class="travel-list-card">

                        <div class="travel-list-thumb">
                            <c:choose>
                                <c:when test="${not empty dto.thumbnailUrl}">
								    <img src="${dto.thumbnailUrl}" alt="대표 이미지">
								</c:when>
                                <c:otherwise>
                                    <div class="travel-list-noimg">이미지 없음</div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="travel-list-content">
                            <div class="travel-list-title">${dto.title}</div>

                            <div class="travel-list-meta">
                                <span>${dto.writerName}</span>
                                <span>${dto.createDate}</span>
                                <span>조회 ${dto.viewCount}</span>
                            </div>
                        </div>
                    </a>
                </c:forEach>

            </div>

            <!-- 페이지네이션 -->
            <div class="travel-pagination">
                <c:forEach begin="1" end="${totalPage}" var="p">
                    <c:choose>
                        <c:when test="${p == page}">
                            <span class="travel-page-btn travel-page-btn-active">${p}</span>
                        </c:when>
                    </c:choose>
                </c:forEach>
            </div>

        </div>
    </div>

    <c:if test="${not empty message}">
        <script>alert('${message}');</script>
    </c:if>
    
</body>
</html>