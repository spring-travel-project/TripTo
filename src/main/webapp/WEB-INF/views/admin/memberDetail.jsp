<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원 상세 정보 - 관리자</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-[#F8FAFC] text-slate-800 flex flex-col min-h-screen">
    
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow max-w-7xl mx-auto w-full px-6 py-10 flex gap-8">
        
        <aside class="w-64 flex-shrink-0">
            <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-4">
                <ul class="menu w-full gap-2 text-base font-bold text-slate-600">
                    <li><a href="${pageContext.request.contextPath}/admin/main" class="hover:bg-slate-50 transition-colors">📊 대시보드</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/memberList" class="active bg-blue-50 text-blue-600">👤 회원 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/companionList" class="hover:bg-slate-50 transition-colors">🤝 동행 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/boardList" class="hover:bg-slate-50 transition-colors">📝 일반 게시판 관리</a></li>
                </ul>
            </div>
        </aside>

        <section class="flex-1 bg-white rounded-3xl shadow-sm border border-slate-200 p-10">
            
            <div class="flex justify-between items-center mb-10 pb-6 border-b border-slate-100">
                <h2 class="text-3xl font-black text-slate-900">👤 회원 상세 정보</h2>
                <button onclick="history.back();" class="btn btn-outline btn-sm text-slate-500 font-bold hover:bg-slate-100">
                    목록으로 돌아가기
                </button>
            </div>

            <%-- 🌟 데이터가 정상적으로 넘어왔을 때만 프로필 영역 출력 --%>
            <c:if test="${not empty member}">
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-10">
                    <div class="col-span-1 flex flex-col items-center bg-slate-50 rounded-3xl p-8 border border-slate-100">
                        <div class="avatar placeholder mb-6">
                            <div class="bg-slate-200 text-slate-500 rounded-full w-32 shadow-inner flex items-center justify-center">
                                <span class="text-5xl">👤</span>
                            </div>
                        </div>
                        <h3 class="text-2xl font-black text-slate-800 mb-1">${member.name}</h3>
                        <p class="text-slate-500 font-medium mb-6">@${member.id}</p>
                        
                        <div class="w-full flex justify-between bg-white px-6 py-4 rounded-2xl shadow-sm border border-slate-100">
                            <span class="font-bold text-slate-500">누적 신고 수</span>
                            <span class="font-black ${member.reportCount > 0 ? 'text-red-500' : 'text-slate-800'} text-xl">
                                <c:out value="${empty member.reportCount ? 0 : member.reportCount}" />건
                            </span>
                        </div>
                    </div>

                    <div class="col-span-2 flex flex-col gap-6 justify-center">
                        <div class="grid grid-cols-3 items-center border-b border-slate-100 pb-4">
                            <div class="col-span-1 text-slate-400 font-bold">닉네임</div>
                            <div class="col-span-2 font-bold text-slate-800 text-lg">${member.nickname}</div>
                        </div>
                        <div class="grid grid-cols-3 items-center border-b border-slate-100 pb-4">
                            <div class="col-span-1 text-slate-400 font-bold">이메일</div>
                            <div class="col-span-2 font-bold text-slate-800 text-lg">
                                ${not empty member.email ? member.email : '<span class="text-slate-400 italic">미등록</span>'}
                            </div>
                        </div>
                        <div class="grid grid-cols-3 items-center border-b border-slate-100 pb-4">
                            <div class="col-span-1 text-slate-400 font-bold">가입일</div>
                            <div class="col-span-2 font-bold text-slate-800 text-lg">${member.regDate}</div>
                        </div>
                        <div class="grid grid-cols-3 items-center border-b border-slate-100 pb-4">
						    <div class="col-span-1 text-slate-400 font-bold">계정 권한</div>
						    <div class="col-span-2">
						        <c:choose>
						            <%-- 🌟 member.auth 를 member.type 으로 수정 --%>
						            <c:when test="${member.type == '1'}">
						                <span class="badge badge-warning font-bold p-3">관리자 (ADMIN)</span>
						            </c:when>
						            <c:otherwise>
						                <span class="badge badge-info font-bold p-3 text-white">일반 회원 (USER)</span>
						            </c:otherwise>
						        </c:choose>
						    </div>
						</div>
                        
                        <div class="mt-8 flex gap-4">
                            <button type="button" onclick="forceDelete('${member.seqMember}', '${member.name}')" 
                                    class="btn btn-error flex-1 font-bold text-white hover:bg-red-600 border-none shadow-sm text-lg">
                                강제 탈퇴 처리
                            </button>
                        </div>
                    </div>
                </div>
            </c:if>

            <%-- 🌟 데이터가 비어있을 경우 에러 메시지 출력 --%>
            <c:if test="${empty member}">
                <div class="py-20 text-center flex flex-col items-center">
                    <span class="text-6xl mb-4">😢</span>
                    <h3 class="text-2xl font-bold text-slate-700 mb-2">회원 정보를 불러올 수 없습니다.</h3>
                    <p class="text-slate-500">잘못된 접근이거나, 이미 탈퇴한 회원일 수 있습니다.<br>주소창에 회원 번호(seqMember)가 정상적으로 전달되었는지 확인해 주세요.</p>
                </div>
            </c:if>

        </section>
    </main>

    <script>
        function forceDelete(seq, name) {
            if (!seq || seq.trim() === '') {
                alert('회원 번호 오류!'); return;
            }
            if (confirm("[" + name + "] 회원을 정말 강제 탈퇴 처리하시겠습니까?")) {
                location.href = "${pageContext.request.contextPath}/admin/memberDelete?seqMember=" + seq;
            }
        }
    </script>
</body>
</html>