<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>deverytime</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
	<style>
		/* 현재 페이지 전용 CSS가 필요하면 여기에 작성 */
	</style>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>
	
	<main class="page-wrap">
		
		<div class="mb-8">
			<h1 class="section-title">콘텐츠 제목</h1>
			<p class="section-desc">여기에 페이지 부제목이나 설명을 적습니다.</p>
		</div>

		<div class="content-card card-pad">
			여기에 폼(Form), 테이블(Table) 등 메인 내용을 작성하세요.
		</div>

	</main>
	
	<script>
		// 현재 페이지 전용 JavaScript가 필요하면 여기에 작성
	</script>
</body>
</html>