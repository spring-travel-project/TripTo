<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>deverytime</title>

	<meta name="_csrf" content="${_csrf.token}">
	<meta name="_csrf_header" content="${_csrf.headerName}">

	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
	<link rel="stylesheet" href="${cp}/resources/css/board.css">
	<link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css">
	<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>

	<style>
	    select[name="seqCategory"] {
	        width: 220px !important;
	        max-width: 100% !important;
	        height: 44px !important;
	        padding: 0 40px 0 12px !important;
	        border: 1px solid #cbd5e1 !important;
	        border-radius: 8px !important;
	        background-color: #fff !important;
	        color: #0f172a !important;
	        box-shadow: none !important;
	        outline: none !important;

	        appearance: none !important;
	        -webkit-appearance: none !important;
	        -moz-appearance: none !important;

	        background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='none' stroke='%2364758b' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><polyline points='4,6 8,10 12,6'/></svg>") !important;
	        background-repeat: no-repeat !important;
	        background-position: right 12px center !important;
	        background-size: 14px !important;
	    }

	    select[name="seqCategory"]:focus {
	        border-color: #94a3b8 !important;
	        box-shadow: 0 0 0 2px rgba(148, 163, 184, 0.12) !important;
	        outline: none !important;
	    }
	</style>
</head>

<body>

<%@ include file="/WEB-INF/views/inc/header.jsp" %>

<div class="board-write-page">

	<div class="max-w-3xl mx-auto mt-10">

	    <h1 class="text-3xl font-bold mb-6">게시글 작성</h1>

	    <form method="post"
	          action="${cp}/board/write.do?${_csrf.parameterName}=${_csrf.token}"
	          class="board-write-form">

			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

		    <div class="form-group">
			    <label class="form-label">카테고리</label>
			    <select name="seqCategory" class="input input-bordered category-select">
			        <c:forEach items="${categoryList}" var="c">
			            <option value="${c.seqCategory}">
			                ${c.categoryName}
			            </option>
			        </c:forEach>
			    </select>
			</div>

			<div class="form-group">
			    <label class="form-label">제목</label>
			    <input type="text" name="title" class="input input-bordered w-full" required>
			</div>

			<div class="form-group">
			    <label class="form-label">내용</label>

			    <div id="editor"></div>
				<input type="hidden" name="content" id="content">

			    <div id="imagePreviewArea" class="image-preview-area"></div>
			</div>

		    <div class="flex gap-3">
		        <button type="submit" class="btn-board-outline">등록</button>
		        <a href="${cp}/board/list.do" class="btn-board-outline btn-board-outline-light">취소</a>
		    </div>

		</form>

	</div>
</div>

<script>
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '700px',
        initialEditType: 'wysiwyg',
        previewStyle: 'vertical',
        initialValue: '',
        hooks: {
            addImageBlobHook: async (blob, callback) => {
                const formData = new FormData();
                formData.append('file', blob);

                const response = await fetch('${cp}/board/imageUpload.do', {
                    method: 'POST',
                    headers: {
                        [csrfHeader]: csrfToken
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

    document.querySelector('.board-write-form').addEventListener('submit', function () {
        document.getElementById('content').value = editor.getHTML();
    });
</script>

</body>
</html>