<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${match.nickname}님의 프로필 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="max-w-4xl mx-auto pb-20">
        <div class="relative h-[400px] w-full overflow-hidden sm:rounded-b-[3rem] shadow-lg">
            <img src="${pageContext.request.contextPath}/resources/upload/profile/${empty match.pic ? 'pic.png' : match.pic}" 
                 class="w-full h-full object-cover">
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
            
            <div class="absolute bottom-10 left-8 text-white">
                <div class="flex items-center gap-3 mb-2">
                    <span class="bg-blue-600 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">${match.mbti}</span>
                    <span class="text-sm font-medium opacity-90">${match.gender == 0 ? '남성' : '여성'} · ${match.age}세</span>
                </div>
                <h1 class="text-4xl font-extrabold">${match.nickname}</h1>
            </div>
        </div>

        <div class="px-6 -mt-8 relative z-10">
            <div class="bg-white rounded-[2rem] shadow-xl p-8 mb-8 border border-slate-100">
                <div class="flex items-center justify-between mb-8">
                    <h2 class="text-xl font-bold text-slate-800">나와의 여행 궁합 🧬</h2>
                    <div class="text-blue-600 font-extrabold text-2xl">${match.matchCount} / 8</div>
                </div>

                <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div class="p-4 rounded-2xl ${match.mbtiMatch ? 'bg-blue-50 border-blue-100' : 'bg-slate-50 border-slate-100'} border text-center">
                        <span class="block text-xl mb-1">${match.mbtiMatch ? '✅' : '❓'}</span>
                        <span class="text-xs font-bold text-slate-500 uppercase">MBTI</span>
                        <p class="text-sm font-bold mt-1">${match.mbti}</p>
                    </div>
                    <div class="p-4 rounded-2xl ${match.smokingMatch ? 'bg-emerald-50 border-emerald-100' : 'bg-slate-50 border-slate-100'} border text-center">
                        <span class="block text-xl mb-1">${match.smokingMatch ? '🚭' : '🚬'}</span>
                        <span class="text-xs font-bold text-slate-500 uppercase">흡연</span>
                        <p class="text-sm font-bold mt-1">${match.smoking == 1 ? '비흡연' : '흡연 가능'}</p>
                    </div>
                    <div class="p-4 rounded-2xl ${match.drinkingMatch ? 'bg-amber-50 border-amber-100' : 'bg-slate-50 border-slate-100'} border text-center">
                        <span class="block text-xl mb-1">${match.drinkingMatch ? '🍻' : '🥤'}</span>
                        <span class="text-xs font-bold text-slate-500 uppercase">음주</span>
                        <p class="text-sm font-bold mt-1">${match.drinking == 2 ? '안함' : '선호'}</p>
                    </div>
                    <div class="p-4 rounded-2xl ${match.travelTypeMatch ? 'bg-indigo-50 border-indigo-100' : 'bg-slate-50 border-slate-100'} border text-center">
                        <span class="block text-xl mb-1">🏨</span>
                        <span class="text-xs font-bold text-slate-500 uppercase">숙소타입</span>
                        <p class="text-sm font-bold mt-1">호텔/펜션</p>
                    </div>
                </div>
            </div>

            <div class="bg-white rounded-[2rem] shadow-sm p-8 mb-8 border border-slate-100">
                <h2 class="text-xl font-bold text-slate-800 mb-4">함께하고 싶은 여행 ✍️</h2>
                <p class="text-slate-600 leading-relaxed whitespace-pre-wrap">${match.intro}</p>
            </div>
        </div>

        <div class="fixed bottom-6 left-1/2 -translate-x-1/2 w-[calc(100%-3rem)] max-w-lg z-50">
            <div class="bg-white/80 backdrop-blur-xl border border-white/20 shadow-2xl rounded-full p-2 flex gap-2">
                <button class="flex-1 bg-slate-800 text-white py-4 rounded-full font-bold hover:bg-slate-900 transition-all">
                    동행 신청하기
                </button>
                <button class="w-14 h-14 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center hover:bg-blue-200 transition-all">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.129.166 2.27.293 3.423.379.35.026.67.21.865.501L12 21l2.755-4.133a1.14 1.14 0 01.865-.501 48.172 48.172 0 003.423-.379c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0012 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018z" />
                    </svg>
                </button>
            </div>
        </div>
    </main>
</body>
</html>