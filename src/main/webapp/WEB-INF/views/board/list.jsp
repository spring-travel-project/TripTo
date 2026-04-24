<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>커뮤니티</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
    <link rel="stylesheet" href="${cp}/resources/css/board.css">
</head>
<body>

    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <div class="board-page">
        <div class="board-wrap">

            <div class="board-top">
                <h1 class="board-title">커뮤니티</h1>

                <a href="${cp}/board/write.do" class="btn-board-outline btn-board-sm">
                    글쓰기
                </a>
            </div>

            <!-- 카테고리 -->
            <div class="board-category-box">
                <a href="${cp}/board/list.do"
                   class="board-category ${empty category ? 'board-category-active' : ''}">
                    전체
                </a>

                <c:forEach items="${categoryList}" var="c">
                    <a href="${cp}/board/list.do?category=${c.categoryName}"
                       class="board-category ${category == c.categoryName ? 'board-category-active' : ''}">
                        ${c.categoryName}
                    </a>
                </c:forEach>
            </div>

            <!-- 검색 -->
            <form method="get" action="${cp}/board/list.do" class="board-search-form">
                <input type="hidden" name="category" value="${category}">

                <input type="text"
                       name="searchWord"
                       value="${searchWord}"
                       placeholder="제목 또는 내용으로 검색해보세요."
                       class="board-search-input">

                <button type="submit" class="btn-board-outline btn-board-sm">
				    검색
				</button>
            </form>

            <!-- 목록 -->
            <div class="board-list-card-wrap">

                <c:if test="${empty list}">
                    <div class="board-empty-box">
                        <div class="board-empty-icon">📝</div>
                        <div class="board-empty-title">게시글이 없습니다.</div>
                        <div class="board-empty-desc">
                            아직 등록된 게시글이 없거나 검색 조건에 맞는 결과가 없습니다.
                        </div>
                    </div>
                </c:if>

                <c:forEach items="${list}" var="dto">
                    <a href="${cp}/board/detail.do?seqBoardPost=${dto.seqBoardPost}" class="board-list-card">

                        <div class="board-list-thumb">
                            <c:choose>
                                <c:when test="${not empty dto.savedName}">
								    <img src="${cp}/resources/upload/board/${dto.savedName}" alt="대표 이미지">
								</c:when>
                                <c:otherwise>
                                    <div class="board-list-noimg">이미지 없음</div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="board-list-content">
                            <div class="board-list-category">${dto.categoryName}</div>
                            <div class="board-list-title">${dto.title}</div>

                            <div class="board-list-meta">
                                <span>${dto.writerName}</span>
                                <span>${dto.createDate}</span>
                                <span>조회 ${dto.viewCount}</span>
                            </div>
                        </div>
                    </a>
                </c:forEach>

            </div>

            <!-- 페이지네이션 -->
            <div class="board-pagination">
                <c:forEach begin="1" end="${totalPage}" var="p">
                    <c:choose>
                        <c:when test="${p == page}">
                            <span class="board-page-btn board-page-btn-active">${p}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${cp}/board/list.do?page=${p}&category=${category}&searchWord=${searchWord}"
                               class="board-page-btn">
                                ${p}
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>

        </div>
    </div>

    <c:if test="${not empty message}">
        <script>alert('${message}');</script>
    </c:if>
    
    <script>
	    window.addEventListener('DOMContentLoaded', function () {
	        const url = new URL(window.location.href);
	
	        if (url.searchParams.has('searchWord') || url.searchParams.has('category')) {
	            window.history.replaceState({}, '', url.pathname);
	        }
	    });
	</script>

</body>
</html>