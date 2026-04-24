<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 수정</title>
    <link rel="stylesheet" href="${cp}/resources/css/board.css">
</head>
<body>

<h1>게시글 수정</h1>

<form method="post" action="${cp}/board/edit.do" enctype="multipart/form-data">
    <input type="hidden" name="seqBoardPost" value="${dto.seqBoardPost}">

    <div>
        <label>카테고리</label>
        <select name="seqCategory" required>
            <c:forEach items="${categoryList}" var="c">
                <option value="${c.seqCategory}" ${dto.seqCategory == c.seqCategory ? 'selected' : ''}>
                    ${c.categoryName}
                </option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label>제목</label>
        <input type="text" name="title" value="${dto.title}" required>
    </div>

    <div>
        <label>글 내용</label>
        <textarea name="content" rows="15" cols="80" required>${dto.content}</textarea>
    </div>

    <div>
        <label>파일 첨부</label>
        <input type="file" name="attach">
    </div>

    <div>
        <a href="${cp}/board/detail.do?seqBoardPost=${dto.seqBoardPost}"
           onclick="return confirm('변경된 내용이 삭제됩니다. 계속 하시겠습니까?');">취소</a>
        <button type="submit">수정하기</button>
    </div>
</form>

</body>
</html>