<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원 관리 - 관리자</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-[#F8FAFC] text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow max-w-7xl mx-auto w-full px-6 py-10 flex gap-8">
        
        <aside class="w-64 flex-shrink-0">
            <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-4">
                <ul class="menu w-full gap-2 text-base font-bold text-slate-600">
                    <li><a href="${pageContext.request.contextPath}/admin/main" class="hover:bg-slate-50 transition-colors">📊 대시보드</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/memberList" class="active bg-blue-50 text-blue-600" >👤 회원 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/companionList" class="hover:bg-slate-50 transition-colors">🤝 동행 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/boardList" class="hover:bg-slate-50 transition-colors">📝 일반 게시판 관리</a></li>
                </ul>
            </div>
        </aside>

        <section class="flex-1 bg-white rounded-3xl shadow-sm border border-slate-200 p-8">
            <h2 class="text-3xl font-black mb-8 text-slate-900">👤 회원 관리</h2>

            <form action="${pageContext.request.contextPath}/admin/memberList" method="GET" class="flex gap-2 mb-8">
                <select name="searchType" class="select select-bordered w-32 font-bold">
                    <option value="name" ${searchType == 'name' ? 'selected' : ''}>이름</option>
                    <option value="id" ${searchType == 'id' ? 'selected' : ''}>아이디</option>
                    <option value="nickname" ${searchType == 'nickname' ? 'selected' : ''}>닉네임</option>
                </select>
                <div class="relative flex-grow">
                    <input type="text" name="searchKeyword" value="${searchKeyword}" placeholder="검색어를 입력하세요" class="input input-bordered w-full pl-4 font-medium" />
                </div>
                <button type="submit" class="btn btn-primary px-8 font-bold">검색</button>
            </form>

            <div class="overflow-x-auto rounded-xl border border-slate-100">
                <table class="table table-zebra w-full text-center">
                    <thead class="bg-slate-50 text-slate-500 font-bold">
                        <tr>
                            <th>이름</th>
                            <th>아이디</th>
                            <th>닉네임</th>
                            <th>가입일</th>
                            <th>신고수</th>
                        </tr>
                    </thead>
                    <tbody class="font-medium text-slate-600">
                        <c:forEach var="dto" items="${list}">
                            <tr class="hover:bg-slate-50 transition-colors">
                                <td class="font-bold text-slate-800">${dto.name}</td>
                                <td>${dto.id}</td>
                                <td>${dto.nickname}</td>
                                <td>${dto.regDate}</td>
                                <td>
                                    <span class="badge ${dto.reportCount > 0 ? 'badge-error' : 'badge-ghost'} font-bold">
                                        ${dto.reportCount}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty list}">
                            <tr><td colspan="5" class="py-20 text-slate-400">검색 결과가 없습니다.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="flex justify-center mt-10">
                <div class="join shadow-sm border border-slate-200">
                    
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/admin/memberList?page=${currentPage - 1}&searchType=${searchType}&searchKeyword=${searchKeyword}" 
                           class="join-item btn btn-sm bg-white hover:bg-slate-50 text-slate-600 border-none">
                           «
                        </a>
                    </c:if>
            
                    <c:if test="${totalPage > 0}">
                        <c:forEach var="i" begin="1" end="${totalPage}">
                            <a href="${pageContext.request.contextPath}/admin/memberList?page=${i}&searchType=${searchType}&searchKeyword=${searchKeyword}" 
                               class="join-item btn btn-sm ${currentPage == i ? 'btn-primary text-white' : 'bg-white hover:bg-slate-50 text-slate-600'} border-none">
                               ${i}
                            </a>
                        </c:forEach>
                    </c:if>
            
                    <c:if test="${currentPage < totalPage}">
                        <a href="${pageContext.request.contextPath}/admin/memberList?page=${currentPage + 1}&searchType=${searchType}&searchKeyword=${searchKeyword}" 
                           class="join-item btn btn-sm bg-white hover:bg-slate-50 text-slate-600 border-none">
                           »
                        </a>
                    </c:if>
                    
                </div>
            </div>
        </section>
    </main>
</body>
</html>