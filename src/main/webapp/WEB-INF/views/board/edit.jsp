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

    <link rel="stylesheet" href="${cp}/resources/css/board.css">
</head>
<body>
	
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>
	
	<div class="board-page">
	    <div class="board-write-wrap">
	
	        <h1 class="board-write-title">게시글 수정</h1>
	
	        <form method="post" action="${cp}/board/edit.do" class="board-write-form">
	            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
	            <input type="hidden" name="seqBoardPost" value="${dto.seqBoardPost}">
	
	            <div class="form-group">
	                <label class="form-label">카테고리</label>
	
	                <select name="seqCategory" class="input input-bordered category-select">
	                    <c:forEach items="${categoryList}" var="c">
	                        <option value="${c.seqCategory}"
	                            ${dto.seqCategory == c.seqCategory ? 'selected' : ''}>
	                            ${c.categoryName}
	                        </option>
	                    </c:forEach>
	                </select>
	            </div>
	
	            <div class="form-group">
	                <label class="form-label">제목</label>
	                <input type="text"
	                       name="title"
	                       value="${dto.title}"
	                       class="input input-bordered w-full"
	                       required>
	            </div>
	
	            <div class="form-group">
			        <label class="form-label">글 내용</label>
			
			        <div id="editor"></div>
			        <input type="hidden" name="content" id="content">
			
			        <textarea id="originContent" style="display:none;"><c:out value="${dto.content}" /></textarea>
			    </div>
			
			    <div class="board-write-actions">
			        <button type="submit" class="btn-board-outline btn-board-sm">수정하기</button>
			        <a href="${cp}/board/detail.do?seqBoardPost=${dto.seqBoardPost}" class="btn-board-outline btn-board-sm">취소</a>
			    </div>
	        </form>
	
	    </div>
	</div>
	
	<c:if test="${not empty message}">
	    <script>alert('${message}');</script>
	</c:if>
	
	<script>
	    const csrfParameter = '${_csrf.parameterName}';
	    const csrfToken = '${_csrf.token}';
	
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

	                const response = await fetch('${cp}/board/imageUpload.do', {
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
	
	    document.querySelector('.board-write-form').addEventListener('submit', function () {
	        document.getElementById('content').value = editor.getHTML();
	    });
	</script>

</body>
</html>