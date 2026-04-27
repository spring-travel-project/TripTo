<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 대시보드 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-[#F8FAFC] text-slate-800 flex flex-col min-h-screen">
    
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow max-w-7xl mx-auto w-full px-6 py-10 flex gap-8">
        
       	<aside class="w-64 flex-shrink-0">
            <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-4">
                <ul class="menu w-full gap-2 text-base font-bold text-slate-600">
                    <li><a href="${pageContext.request.contextPath}/admin/main" class="active bg-blue-50 text-blue-600">📊 대시보드</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/memberList" class="hover:bg-slate-50 transition-colors" >👤 회원 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/companionList" class="hover:bg-slate-50 transition-colors">🤝 동행 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/boardList" class="hover:bg-slate-50 transition-colors">📝 일반 게시판 관리</a></li>
                </ul>
            </div>
		</aside>

        <section class="flex-1 flex flex-col gap-8">
            
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
			    <div class="bg-white p-8 rounded-[2rem] shadow-sm border border-slate-100 flex flex-col justify-center h-44 transition-all hover:shadow-md">
			        <div class="text-slate-400 font-bold text-sm mb-2">현재 가입 회원</div>
			        <div class="text-5xl font-black text-slate-900 leading-none">
			            <%-- TOTALMEMBER -> totalMember 로 수정 --%>
			            <c:out value="${not empty stats.totalMember ? stats.totalMember : 0}" />
			            <span class="text-lg font-bold text-slate-300 ml-1">명</span>
			        </div>
			    </div>
			
			    <div class="bg-white p-8 rounded-[2rem] shadow-sm border border-slate-100 flex flex-col justify-center h-44 transition-all hover:shadow-md">
			        <div class="text-slate-400 font-bold text-sm mb-2">동행 게시글 수</div>
			        <div class="text-5xl font-black text-slate-900 leading-none">
			            <%-- TOTALTRAVEL -> totalTravel 로 수정 --%>
			            <c:out value="${not empty stats.totalTravel ? stats.totalTravel : 0}" />
			            <span class="text-lg font-bold text-slate-300 ml-1">개</span>
			        </div>
			    </div>
			
			    <div class="bg-white p-8 rounded-[2rem] shadow-sm border border-slate-100 flex flex-col justify-center h-44 transition-all hover:shadow-md">
			        <div class="text-slate-400 font-bold text-sm mb-2">일반 게시글 수</div>
			        <div class="text-5xl font-black text-slate-900 leading-none">
			            <%-- TOTALBOARD -> totalBoard 로 수정 --%>
			            <c:out value="${not empty stats.totalBoard ? stats.totalBoard : 0}" />
			            <span class="text-lg font-bold text-slate-300 ml-1">개</span>
			        </div>
			    </div>
			</div>

            <div class="flex flex-col gap-6">
			    <h3 class="text-xl font-black text-slate-800 px-2 italic">⚠️ 실시간 신고 현황</h3>
			    
			    <div class="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100 flex items-center justify-between transition-all hover:border-red-200 group">
			        <div class="flex items-center gap-6">
			            <div class="w-16 h-16 bg-red-50 text-red-500 rounded-full flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">🚨</div>
			            <div>
			                <h4 class="text-2xl font-black text-slate-800">신고 된 회원 수</h4>
			                <p class="text-slate-500 mt-1 font-medium text-sm">부적절한 활동으로 신고 접수된 누적 유저 수입니다.</p>
			            </div>
			        </div>
			        <div class="text-right">
			            <div class="text-sm font-bold text-slate-400 mb-1 tracking-tighter uppercase">Total User Reports</div>
			            <div class="text-5xl font-black text-red-500">
			                <%-- reportMemberTotal 로 수정 --%>
			                <c:out value="${not empty stats.reportMemberTotal ? stats.reportMemberTotal : 0}" />
			                <span class="text-xl ml-1 font-bold">건</span>
			            </div>
			        </div>
			    </div>
			
			    <div class="bg-white p-8 rounded-[2.5rem] shadow-sm border border-slate-100 flex items-center justify-between transition-all hover:border-orange-200 group">
			        <div class="flex items-center gap-6">
			            <div class="w-16 h-16 bg-orange-50 text-orange-500 rounded-full flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">🗑️</div>
			            <div>
			                <h4 class="text-2xl font-black text-slate-800">신고 된 게시글 수</h4>
			                <p class="text-slate-500 mt-1 font-medium text-sm">운영 정책 위반으로 신고된 게시물 누적 건수입니다.</p>
			            </div>
			        </div>
			        <div class="text-right">
			            <div class="text-sm font-bold text-slate-400 mb-1 tracking-tighter uppercase">Total Post Reports</div>
			            <div class="text-5xl font-black text-orange-500">
			                <%-- reportBoardTotal 로 수정 --%>
			                <c:out value="${not empty stats.reportBoardTotal ? stats.reportBoardTotal : 0}" />
			                <span class="text-xl ml-1 font-bold">건</span>
			            </div>
			        </div>
			    </div>
			</div>
        </section>
        
    </main>

</body>
</html>