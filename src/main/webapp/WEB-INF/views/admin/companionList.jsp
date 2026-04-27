<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>동행 관리 - 관리자</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-[#F8FAFC] text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow max-w-7xl mx-auto w-full px-6 py-10 flex gap-8">
        
        <aside class="w-64 flex-shrink-0">
            <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-4">
                <ul class="menu w-full gap-2 text-base font-bold text-slate-600">
                    <li><a href="${pageContext.request.contextPath}/admin/main">📊 대시보드</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/memberList">👤 회원 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/companionList" class="active bg-blue-50 text-blue-600">🤝 동행 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/boardList">📝 일반 게시판 관리</a></li>
                </ul>
            </div>
        </aside>

        <section class="flex-1 bg-white rounded-3xl shadow-sm border border-slate-200 p-8">
            <h2 class="text-3xl font-black mb-8 text-slate-900">🤝 동행 관리</h2>

            <form action="${pageContext.request.contextPath}/admin/companionList" method="GET" class="flex items-center gap-3 mb-8 bg-slate-50 p-4 rounded-2xl border border-slate-100">
                <select name="searchType" class="select select-bordered w-32 font-bold bg-white">
                    <option value="title" ${searchType == 'title' ? 'selected' : ''}>제목</option>
                    <option value="writer" ${searchType == 'writer' ? 'selected' : ''}>작성자</option> </select>
                
                <div class="relative flex-grow">
                    <input type="text" name="searchKeyword" value="${searchKeyword}" placeholder="검색어를 입력하세요" class="input input-bordered w-full font-medium bg-white" />
                </div>

                <div class="form-control bg-white px-4 py-2 rounded-xl border border-slate-200 whitespace-nowrap">
                    <label class="label cursor-pointer gap-3 p-0">
                        <span class="label-text font-bold text-slate-600">삭제글 포함</span> 
                        <input type="checkbox" name="showDeleted" value="Y" ${showDeleted == 'Y' ? 'checked' : ''} 
                               class="checkbox checkbox-primary checkbox-sm" onchange="this.form.submit()" />
                    </label>
                </div>

                <button type="submit" class="btn btn-primary px-8 font-bold">검색</button>
            </form>

            <div class="overflow-x-auto rounded-xl border border-slate-100">
                <table class="table table-zebra w-full text-center" style="table-layout: fixed;">
                    <thead class="bg-slate-50 text-slate-500 font-bold">
                        <tr>
                            <th style="width: 70px;">번호</th>
                            <th class="text-left" style="width: auto;">제목</th>
                            <th style="width: 100px;">작성자</th>
                            <th style="width: 120px;">작성일</th>
                            <th style="width: 80px;">조회수</th>
                            <th style="width: 80px;" class="text-red-500">신고수</th>
                            <th style="width: 90px;">상태</th>
                            <th style="width: 80px;">관리</th>
                        </tr>
                    </thead>
                    <tbody class="font-medium text-slate-600">
                        <c:forEach var="item" items="${list}">
                            <tr class="hover:bg-slate-50 transition-colors">
                                <td>${item.SEQTRAVELPOST}</td>
                                
                                <td class="text-left font-bold text-slate-800 truncate">
                                    <a href="${pageContext.request.contextPath}/travel/detail?seqTravelPost=${item.SEQTRAVELPOST}" 
                                       target="_blank" class="hover:text-blue-600 hover:underline">
                                       ${item.TITLE}
                                    </a>
                                </td>
                                
                                <td class="truncate">${item.WRITERNAME}</td>
                                <td class="text-xs">${item.CREATEDATE}</td>
                                <td>${item.VIEWCOUNT}</td>
                                
                                <td>
                                    <span class="badge ${item.REPORTCOUNT > 0 ? 'badge-error text-white' : 'badge-ghost'} font-bold">
                                        ${item.REPORTCOUNT}
                                    </span>
                                </td>
                                
                                <td class="whitespace-nowrap">
                                    <c:choose>
                                        <c:when test="${item.STATUS == 'DELETED'}">
                                            <span class="badge badge-error badge-sm text-[10px] text-white font-bold">삭제됨</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-ghost badge-sm text-[10px] font-bold text-slate-400">정상</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                
                                <td>
                                    <c:if test="${item.STATUS == 'NORMAL'}">
                                        <button type="button" onclick="deleteCompanion('${item.SEQTRAVELPOST}', '${item.TITLE}')" 
                                                class="btn btn-error btn-xs text-white font-bold">삭제</button>
                                    </c:if>
                                    <c:if test="${item.STATUS == 'DELETED'}">-</c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty list}">
                            <tr><td colspan="8" class="py-24 text-slate-400 font-bold text-center">동행 게시글이 없습니다.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="flex justify-center mt-10">
                <div class="join border border-slate-200">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/admin/companionList?page=${currentPage - 1}&searchType=${searchType}&searchKeyword=${searchKeyword}&showDeleted=${showDeleted}" class="join-item btn btn-sm bg-white border-none">«</a>
                    </c:if>
                    <c:forEach var="i" begin="1" end="${totalPage}">
                        <a href="${pageContext.request.contextPath}/admin/companionList?page=${i}&searchType=${searchType}&searchKeyword=${searchKeyword}&showDeleted=${showDeleted}" 
                           class="join-item btn btn-sm ${currentPage == i ? 'btn-primary text-white' : 'bg-white text-slate-600'} border-none">${i}</a>
                    </c:forEach>
                    <c:if test="${currentPage < totalPage}">
                        <a href="${pageContext.request.contextPath}/admin/companionList?page=${currentPage + 1}&searchType=${searchType}&searchKeyword=${searchKeyword}&showDeleted=${showDeleted}" class="join-item btn btn-sm bg-white border-none">»</a>
                    </c:if>
                </div>
            </div>
        </section>
    </main>

    <script>
        function deleteCompanion(seq, title) {
            // 방어 코드: 번호가 제대로 넘어왔는지 확인
            if (!seq || seq === 'undefined' || seq === '') {
                alert("게시글 번호를 불러오지 못했습니다.");
                return;
            }
            
            if (confirm("[" + title + "]\n동행 게시글을 삭제 처리하시겠습니까?")) {
                location.href = "${pageContext.request.contextPath}/admin/companionDelete?seqTravelPost=" + seq; // 🌟 파라미터 이름 수정
            }
        }
    </script>
</body>
</html>