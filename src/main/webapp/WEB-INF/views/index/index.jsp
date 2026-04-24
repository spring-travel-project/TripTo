<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>TripTo - 완벽한 여행 동행 찾기</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-[#F8FAFC] text-slate-800">
    
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="max-w-7xl mx-auto px-8 py-20">
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-12 items-stretch h-[650px]">
            
            <div class="lg:col-span-4 flex flex-col justify-between">
                
                <div class="space-y-8">
                    <h1 class="text-6xl font-black text-slate-900 leading-[1.1] tracking-tighter">
                        나와 딱 맞는<br>
                        <span class="text-[#1D63FF]">여행 동행</span><br>
                        TripTo
                    </h1>
                    <p class="text-xl text-slate-400 font-medium leading-relaxed">
                        MBTI부터 걸음수까지,<br>
                        취향을 넘어 데이터로 검증된<br>
                        완벽한 메이트를 만나보세요.
                    </p>
                </div>

                <div class="bg-white rounded-[3rem] border border-slate-100 flex items-center justify-center p-12 shadow-[0_10px_40px_-15px_rgba(0,0,0,0.05)] overflow-hidden relative group">
                    <img src="${pageContext.request.contextPath}/resources/img/main_logo.png" 
                         onerror="this.src='https://cdn-icons-png.flaticon.com/512/2060/2060284.png'"
                         class="w-40 h-40 object-contain group-hover:scale-110 transition-transform duration-500">
                </div>
            </div>

            <div class="lg:col-span-8">
                <c:choose>
                    <c:when test="${not empty featuredPost}">
                        <div class="w-full h-full rounded-[3.5rem] overflow-hidden relative shadow-2xl group cursor-pointer"
                             onclick="location.href='${pageContext.request.contextPath}/community/detail?seq=${featuredPost.seqBoardPost}';">
                            
                            <img src="${pageContext.request.contextPath}/resources/upload/board/${mainImage}" 
     onerror="this.src='https://images.unsplash.com/photo-1499856871958-5b9627545d1a?q=80&w=2020&auto=format&fit=crop'" 
                                 onerror="this.src='https://images.unsplash.com/photo-1499856871958-5b9627545d1a?q=80&w=2020&auto=format&fit=crop'"
                                 class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105">
                            
                            <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-black/20 to-transparent"></div>

                            <div class="absolute bottom-0 left-0 p-16 w-full">
                                <div class="inline-flex items-center gap-2 px-5 py-2 rounded-full bg-[#1D63FF] text-white font-bold text-sm mb-6 shadow-lg">
                                    ⭐ EDITOR'S PICK
                                </div>
                                <h2 class="text-5xl font-black text-white mb-6 drop-shadow-md leading-tight">
                                    ${featuredPost.title}
                                </h2>
                                <div class="flex items-center text-slate-300 gap-6 text-lg font-bold">
                                    <span class="flex items-center gap-2">
                                        <div class="w-8 h-8 rounded-full bg-slate-500 overflow-hidden border border-white/20">
                                            <img src="${pageContext.request.contextPath}/resources/upload/profile/${featuredPost.userPic}" class="w-full h-full object-cover">
                                        </div>
                                        ${featuredPost.nickname}
                                    </span>
                                    <span class="w-1.5 h-1.5 bg-white/30 rounded-full"></span>
                                    <span>조회수 ${featuredPost.viewCount}</span>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="w-full h-full rounded-[3.5rem] bg-slate-200 flex items-center justify-center text-slate-400 font-bold">
                            추천 게시물을 등록해주세요.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </main>

    
</body>
</html>





