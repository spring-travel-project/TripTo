<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>프로필 작성 안내 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow flex items-center justify-center p-4">
        <div class="bg-white p-10 rounded-[2.5rem] shadow-lg border border-slate-200 w-full max-w-lg text-center transform hover:-translate-y-1 transition-transform duration-300">
            
            <div class="text-7xl mb-6">📝</div>
            
            <h1 class="text-3xl font-black text-slate-900 mb-4 tracking-tight">프로필 작성이 필요합니다!</h1>
            
            <p class="text-slate-500 font-medium text-[15px] leading-relaxed mb-10">
                TripTo를 이용하시려면<br>
                나만의 여행 성향이 담긴 프로필 작성이 권장됩니다.<br>
                <span class="text-blue-500 font-bold mt-2 inline-block">지금 프로필을 작성하러 가시겠습니까?</span>
            </p>

            <div class="flex flex-col gap-3">
                <button onclick="location.href='${pageContext.request.contextPath}/member/profileEdit.do'" class="btn btn-primary text-white w-full h-14 text-lg font-bold rounded-2xl shadow-md">
                    예
                </button>
                <button onclick="location.href='${pageContext.request.contextPath}/index.do'" class="btn bg-slate-100 text-slate-500 border-none hover:bg-slate-200 w-full h-14 text-lg font-bold rounded-2xl">
                    아니오 (메인으로 이동합니다)
                </button>
            </div>
            
        </div>
    </main>
</body>
</html>