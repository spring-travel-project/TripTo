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
	
	<script>
	    const csrfParameter = '${_csrf.parameterName}';
	    const csrfToken = '${_csrf.token}';
	
	    const editor = new toastui.Editor({
	        el: document.querySelector('#editor'),
	        height: '700px',
	        initialEditType: 'wysiwyg',
	        previewStyle: 'vertical',
	        initialValue: document.getElementById('originContent').value,
	        hooks: {
	        	addImageBlobHook: async (blob, callback) => {
	        	    const formData = new FormData();
	        	    formData.append('file', blob);

	        	    const response = await fetch('${cp}/travel/imageUpload.do', {
	        	        method: 'POST',
	        	        headers: {
	        	            'X-CSRF-TOKEN': csrfToken
	        	        },
	        	        body: formData
	        	    });

	        	    if (!response.ok) {
	        	        alert('이미지 업로드 실패');
	        	        return;
	        	    }

	        	    const result = await response.json();
	        	    callback(result.url, '이미지');
	        	}
	        }
	    });
	
	    document.querySelector('.travel-write-form').addEventListener('submit', function () {
	        document.getElementById('content').value = editor.getHTML();
	    });
	</script>

</body>
</html>