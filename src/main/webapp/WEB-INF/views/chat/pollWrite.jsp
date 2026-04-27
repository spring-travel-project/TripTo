<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>TripTo | 투표 등록</title>
	<%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
	<%@ include file="/WEB-INF/views/inc/header.jsp" %>

	<main class="page-wrap">

		<div class="mb-8">
			<h1 class="section-title">투표 등록</h1>
			<p class="section-desc">채팅방에서 함께 결정할 투표를 작성하세요.</p>
		</div>

		<div class="content-card card-pad">
			<form method="post" action="${pageContext.request.contextPath}/chat/poll/write" class="space-y-6">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
				<input type="hidden" name="seqChattingroom" value="${roomId}">

				<div class="grid grid-cols-1 md:grid-cols-[1fr_260px] gap-6">
					<div>
						<label for="pollTitle" class="block text-sm font-semibold text-slate-700 mb-2">
							투표 제목
						</label>
						<input
							type="text"
							id="pollTitle"
							name="pollTitle"
							placeholder="투표 제목을 입력하세요."
							class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-violet-400"
							required>
					</div>

					<div>
						<label for="pollEnddateInput" class="block text-sm font-semibold text-slate-700 mb-2">
							투표 종료 시간/날짜
						</label>
						<input
							type="datetime-local"
							id="pollEnddateInput"
							name="pollEnddateInput"
							class="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-violet-400"
							required>
					</div>
				</div>

				<div>
					<label for="polldetail" class="block text-sm font-semibold text-slate-700 mb-2">
						투표 설명
					</label>
					<textarea
						id="polldetail"
						name="polldetail"
						rows="8"
						placeholder="투표에 대한 설명을 입력하세요."
						class="w-full resize-none rounded-2xl border border-slate-300 bg-slate-50 px-5 py-4 text-sm leading-6 focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-violet-400"
						required></textarea>
				</div>
				
				<div>
					<div class="flex items-center justify-between mb-3">
						<label class="block text-sm font-semibold text-slate-700">
							투표 항목
						</label>
				
						<button
							type="button"
							id="addOptionBtn"
							class="px-3 py-2 rounded-xl bg-slate-900 text-white text-sm font-semibold hover:bg-slate-700 transition">
							+ 항목 추가
						</button>
					</div>
				
					<div id="pollOptionArea" class="space-y-3">
						<div class="flex gap-2 poll-option-row">
							<input
								type="text"
								name="pollContents"
								placeholder="투표 항목을 입력하세요."
								class="flex-1 rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-violet-400"
								required>
				
							<button
								type="button"
								class="removeOptionBtn px-3 rounded-xl border border-slate-300 bg-white text-slate-500 hover:bg-slate-100 transition"
								disabled>
								삭제
							</button>
						</div>
				
						<div class="flex gap-2 poll-option-row">
							<input
								type="text"
								name="pollContents"
								placeholder="투표 항목을 입력하세요."
								class="flex-1 rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-violet-400"
								required>
				
							<button
								type="button"
								class="removeOptionBtn px-3 rounded-xl border border-slate-300 bg-white text-slate-500 hover:bg-slate-100 transition"
								disabled>
								삭제
							</button>
						</div>
					</div>
				</div>

				<div class="flex justify-end gap-3">
					<button
						type="submit"
						class="px-5 py-2.5 rounded-xl bg-violet-500 text-white text-sm font-semibold hover:bg-violet-600 transition">
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
	<script>
		const addOptionBtn = document.getElementById('addOptionBtn');
		const pollOptionArea = document.getElementById('pollOptionArea');
	
		function updateRemoveButtons() {
			const rows = pollOptionArea.querySelectorAll('.poll-option-row');
			rows.forEach(function(row) {
				const btn = row.querySelector('.removeOptionBtn');
				btn.disabled = rows.length <= 2;
				btn.classList.toggle('opacity-40', rows.length <= 2);
				btn.classList.toggle('cursor-not-allowed', rows.length <= 2);
			});
		}
	
		addOptionBtn.addEventListener('click', function () {
			const row = document.createElement('div');
			row.className = 'flex gap-2 poll-option-row';
	
			row.innerHTML = `
				<input
					type="text"
					name="pollContents"
					placeholder="투표 항목을 입력하세요."
					class="flex-1 rounded-xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-violet-400"
					required>
	
				<button
					type="button"
					class="removeOptionBtn px-3 rounded-xl border border-slate-300 bg-white text-slate-500 hover:bg-slate-100 transition">
					삭제
				</button>
			`;
	
			pollOptionArea.appendChild(row);
			updateRemoveButtons();
		});
	
		pollOptionArea.addEventListener('click', function (e) {
			if (e.target.classList.contains('removeOptionBtn')) {
				const rows = pollOptionArea.querySelectorAll('.poll-option-row');
	
				if (rows.length <= 2) {
					return;
				}
	
				e.target.closest('.poll-option-row').remove();
				updateRemoveButtons();
			}
		});
	
		updateRemoveButtons();
	</script>
	
</body>
</html>