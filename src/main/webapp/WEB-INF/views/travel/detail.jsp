<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 상세</title>
    <link rel="stylesheet" href="${cp}/resources/css/board.css">
</head>
<body>

<h1>${dto.title}</h1>

<div>카테고리: ${dto.categoryName}</div>
<div>작성자: ${dto.writerNickname}</div>
<div>작성일: ${dto.createDate}</div>
<div>조회수: ${dto.viewCount}</div>

<hr>

<div style="min-height:300px; white-space:pre-wrap;">${dto.content}</div>

<hr>

<c:if test="${not empty dto.fileList}">
    <h3>첨부파일</h3>
    <c:forEach items="${dto.fileList}" var="file">
        <div>${file.originalName}</div>
    </c:forEach>
</c:if>

<hr>

<div>
    <a href="${cp}/board/list.do">목록으로</a>

    <button type="button" onclick="alert('신고 기능은 다음 단계에서 연결');">신고</button>

    <c:if test="${isWriter}">
        <a href="${cp}/board/edit.do?seqBoardPost=${dto.seqBoardPost}">수정</a>
    </c:if>

    <c:if test="${isWriter or isAdmin}">
        <form method="post" action="${cp}/board/delete.do" style="display:inline;" onsubmit="return confirm('해당 게시글을 삭제하시겠습니까?');">
            <input type="hidden" name="seqBoardPost" value="${dto.seqBoardPost}">
            <button type="submit">삭제</button>
        </form>
    </c:if>
</div>

<c:if test="${not empty message}">
    <script>alert('${message}');</script>
</c:if>

</body>
</html>