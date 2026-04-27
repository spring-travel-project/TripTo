<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 일정 등록</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>

	<main class="page-wrap">

		<div class="mb-8">
			<h1 class="section-title">일정 등록</h1>
			<p class="section-desc">채팅방에서 공유할 여행 일정을 작성하세요.</p>
		</div>

		<div class="content-card card-pad">
			<form method="post"
			      action="${pageContext.request.contextPath}/chat/routine/write"
			      class="space-y-6">

				<input type="hidden" name="seqChattingroom" value="${roomId}">

				<div>
					<label for="title" class="block text-sm font-semibold text-slate-700 mb-2">
						일정 제목
					</label>
					<input
						type="text"
						id="title"
						name="title"
						placeholder="일정 제목을 입력하세요."
						class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
						required>
				</div>

				<div>
					<label for="detail" class="block text-sm font-semibold text-slate-700 mb-2">
						일정 내용
					</label>
					<textarea
						id="detail"
						name="detail"
						rows="14"
						placeholder="일정 내용을 입력하세요."
						class="w-full resize-none rounded-2xl border border-slate-300 bg-slate-50 px-5 py-4 text-sm leading-6 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
						required></textarea>
				</div>

				<div>
					<label for="dDay" class="block text-sm font-semibold text-slate-700 mb-2">
						일정 날짜
					</label>
					<input
						type="date"
						id="dDay"
						name="dDayInput"
						class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400">
					<p class="mt-2 text-xs text-slate-400">
						선택하지 않으면 일정 상태가 ‘예정’으로 표시됩니다.
					</p>
				</div>

				<div class="rounded-2xl border border-dashed border-slate-300 bg-slate-50 px-5 py-6">
					<p class="text-sm font-semibold text-slate-700 mb-2">첨부파일</p>
					<p class="text-xs text-slate-400">
						현재 첨부파일 저장 기능은 연결하지 않은 상태입니다.
					</p>
				</div>

				<div class="flex justify-end gap-3">
					<button
						type="submit"
						class="px-5 py-2.5 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition">
						등록
					</button>

					<a href="${pageContext.request.contextPath}/chat/schedulePoll?roomId=${roomId}"
					   class="px-5 py-2.5 rounded-xl border border-slate-300 bg-white text-slate-700 text-sm font-semibold hover:bg-slate-100 transition">
						취소
					</a>
				</div>
			</form>
		</div>

	</main>
</body>
</html>