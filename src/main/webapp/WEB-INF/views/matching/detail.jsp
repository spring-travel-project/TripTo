<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${target.nickname}님의 프로필 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow max-w-7xl mx-auto w-full px-4 sm:px-6 py-8 lg:py-12">
        
        <div class="mb-6 lg:mb-10">
            <a href="javascript:history.back()" class="inline-flex items-center text-slate-400 hover:text-blue-600 font-bold transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5 mr-2"><path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" /></svg>
                목록으로 돌아가기
            </a>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-start">
            
            <div class="lg:col-span-5 lg:sticky lg:top-12">
                <div class="bg-white rounded-[2rem] border border-slate-200 overflow-hidden shadow-sm">
                    
                    <div class="w-full aspect-square max-h-[400px] lg:max-h-[500px] bg-slate-50 flex items-center justify-center relative border-b border-slate-100">
                        <img src="${pageContext.request.contextPath}/resources/upload/profile/${empty target.pic ? 'pic.png' : target.pic}" 
                             class="w-3/4 h-3/4 object-contain rounded-2xl drop-shadow-sm">
                    </div>
                    
                    <div class="p-6 lg:p-10 text-center">
                        <div class="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-slate-100 text-slate-600 font-bold text-xs lg:text-sm mb-3 lg:mb-4">
                            <span>${target.gender == 0 ? 'MALE' : 'FEMALE'}</span>
                            <span class="w-1 h-1 bg-slate-300 rounded-full"></span>
                            <span>${target.age} AGE</span>
                        </div>
                        
                        <h1 class="text-3xl lg:text-5xl font-black text-slate-900 mb-6 lg:mb-8 tracking-tight">${target.nickname}</h1>
                        
                        <button class="w-full bg-blue-600 text-white py-4 lg:py-5 rounded-2xl font-bold text-lg lg:text-xl hover:bg-blue-700 transition-all shadow-lg flex items-center justify-center gap-3"
                                onclick="location.href='${pageContext.request.contextPath}/chat/start?targetSeq=${target.seqMember}'">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6 lg:w-7 lg:h-7">
                                <path d="M4.913 2.658c2.075-.27 4.19-.408 6.337-.408 2.147 0 4.262.14 6.337.408 1.92.25 3.413 1.874 3.413 3.826v6.016c0 1.952-1.493 3.576-3.413 3.826-1.077.14-2.162.24-3.26.298-.318.016-.612.183-.796.45L10.5 20.59V18.11c0-.402-.34-.727-.75-.75a48.384 48.384 0 01-4.837-.527c-1.92-.25-3.413-1.874-3.413-3.826V6.484c0-1.952 1.493-3.576 3.413-3.826z" />
                            </svg>
                            채팅 신청하기
                        </button>
            
                        <button class="mt-4 lg:mt-6 w-full flex items-center justify-center gap-2 text-slate-400 hover:text-red-500 font-bold transition-all group"
                                onclick="reportUser('${target.seqMember}', '${target.nickname}')">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5 group-hover:animate-pulse">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M3 3v1.5M3 21v-6m0 0l2.77-.693a9 9 0 016.208.682l.108.054a9 9 0 006.086.71l3.114-.732a48.524 48.524 0 010-5.715l-3.114.732a9 9 0 01-6.086-.71l-.108-.054a9 9 0 00-6.208-.682L3 10.5V15z" />
                            </svg>
                            <span class="text-sm lg:text-base">이 사용자 신고하기</span>
                        </button>
                    </div>
                </div>
            </div>

            <div class="lg:col-span-7">
                <div class="bg-white rounded-[2rem] lg:rounded-[2.5rem] border border-slate-200 p-6 md:p-8 lg:p-12 shadow-sm">
                    
                    <div class="flex flex-col sm:flex-row sm:items-end justify-between mb-8 lg:mb-12 pb-6 lg:pb-8 border-b-2 border-slate-50 gap-4">
                        <div>
                            <h2 class="text-2xl lg:text-4xl font-black text-slate-900 mb-2">여행 성향 분석</h2>
                            <p class="text-slate-400 font-medium text-sm lg:text-lg">나와의 성향이 얼마나 일치하는지 확인해보세요.</p>
                        </div>
                        <div class="sm:text-right">
                            <span class="text-5xl lg:text-7xl font-black text-blue-600">${target.matchCount}</span>
                            <span class="text-xl lg:text-2xl font-bold text-slate-300"> / 8</span>
                        </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-6">
                        
                        <div class="p-5 lg:p-8 rounded-2xl lg:rounded-3xl border-2 ${target.mbti == me.mbti ? 'border-blue-500 bg-blue-50/30' : 'border-slate-100 bg-white'} flex items-center justify-between transition-all">
                            <div class="space-y-1">
                                <span class="text-[10px] lg:text-xs font-black text-slate-300 uppercase tracking-widest">MBTI</span>
                                <c:choose>
                                    <c:when test="${empty me.mbti}"><p class="text-lg lg:text-xl font-bold text-slate-300">🔒 비공개</p></c:when>
                                    <c:otherwise>
                                        <p class="text-2xl lg:text-3xl font-black text-slate-800">${target.mbti}</p>
                                        <p class="text-xs lg:text-sm ${target.mbti == me.mbti ? 'text-blue-600' : 'text-slate-400'} font-bold">${target.mbti == me.mbti ? '나와 일치함' : '다름'}</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="text-4xl lg:text-5xl opacity-80">${not empty me.mbti and target.mbti == me.mbti ? '💖' : '🧩'}</div>
                        </div>

                        <div class="p-5 lg:p-8 rounded-2xl lg:rounded-3xl border-2 ${target.smoking == me.smoking ? 'border-blue-500 bg-blue-50/30' : 'border-slate-100 bg-white'} flex items-center justify-between transition-all">
                            <div class="space-y-1">
                                <span class="text-[10px] lg:text-xs font-black text-slate-300 uppercase tracking-widest">SMOKING</span>
                                <c:choose>
                                    <c:when test="${empty me.smoking}"><p class="text-lg lg:text-xl font-bold text-slate-300">🔒 비공개</p></c:when>
                                    <c:otherwise>
                                        <p class="text-2xl lg:text-3xl font-black text-slate-800">${target.smoking == 1 ? '비흡연' : '흡연자'}</p>
                                        <p class="text-xs lg:text-sm ${target.smoking == me.smoking ? 'text-blue-600' : 'text-slate-400'} font-bold">${target.smoking == me.smoking ? '나와 일치함' : '다름'}</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="text-4xl lg:text-5xl opacity-80">${target.smoking == 1 ? '🚭' : '🚬'}</div>
                        </div>

                        <div class="p-5 lg:p-8 rounded-2xl lg:rounded-3xl border-2 ${target.drinking == me.drinking ? 'border-blue-500 bg-blue-50/30' : 'border-slate-100 bg-white'} flex items-center justify-between transition-all">
                            <div class="space-y-1">
                                <span class="text-[10px] lg:text-xs font-black text-slate-300 uppercase tracking-widest">DRINKING</span>
                                <c:choose>
                                    <c:when test="${empty me.drinking}"><p class="text-lg lg:text-xl font-bold text-slate-300">🔒 비공개</p></c:when>
                                    <c:otherwise>
                                        <p class="text-2xl lg:text-3xl font-black text-slate-800">${target.drinking == 2 ? '안함' : (target.drinking == 1 ? '가끔' : '자주')}</p>
                                        <p class="text-xs lg:text-sm ${target.drinking == me.drinking ? 'text-blue-600' : 'text-slate-400'} font-bold">${target.drinking == me.drinking ? '나와 일치함' : '다름'}</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="text-4xl lg:text-5xl opacity-80">${target.drinking == 2 ? '🥤' : '🍻'}</div>
                        </div>

                        <div class="p-5 lg:p-8 rounded-2xl lg:rounded-3xl border-2 ${target.travelType == me.travelType ? 'border-blue-500 bg-blue-50/30' : 'border-slate-100 bg-white'} flex items-center justify-between transition-all">
                            <div class="space-y-1">
                                <span class="text-[10px] lg:text-xs font-black text-slate-300 uppercase tracking-widest">STYLE</span>
                                <c:choose>
                                    <c:when test="${empty me.travelType}"><p class="text-lg lg:text-xl font-bold text-slate-300">🔒 비공개</p></c:when>
                                    <c:otherwise>
                                        <p class="text-2xl lg:text-3xl font-black text-slate-800">${target.travelType == 0 ? '휴양' : '관광'}</p>
                                        <p class="text-xs lg:text-sm ${target.travelType == me.travelType ? 'text-blue-600' : 'text-slate-400'} font-bold">${target.travelType == me.travelType ? '나와 일치함' : '다름'}</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="text-4xl lg:text-5xl opacity-80">${target.travelType == 0 ? '🏖️' : '🏙️'}</div>
                        </div>

                        <div class="md:col-span-2 p-6 lg:p-10 rounded-2xl lg:rounded-3xl border-2 ${target.stepCount == me.stepCount ? 'border-blue-500 bg-blue-50/30' : 'border-slate-100 bg-white'} flex items-center justify-between transition-all">
                            <div class="space-y-2">
                                <span class="text-[10px] lg:text-xs font-black text-slate-300 uppercase tracking-widest">WALKING</span>
                                <c:choose>
                                    <c:when test="${empty me.stepCount}"><p class="text-lg lg:text-xl font-bold text-slate-300">🔒 비공개</p></c:when>
                                    <c:otherwise>
                                        <p class="text-2xl lg:text-4xl font-black text-slate-800">${target.stepCount == 0 ? '5천보 이하' : (target.stepCount == 1 ? '5천 ~ 1만보' : '1만보 이상')}</p>
                                        <p class="text-sm lg:text-lg ${target.stepCount == me.stepCount ? 'text-blue-600' : 'text-slate-400'} font-bold">나와 선호도가 ${target.stepCount == me.stepCount ? '일치합니다' : '다릅니다'}</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="text-5xl lg:text-7xl opacity-20">👟</div>
                        </div>

                        <div class="p-5 lg:p-8 rounded-2xl lg:rounded-3xl border border-slate-100 bg-slate-50/50 space-y-2 lg:space-y-3">
                            <span class="text-[10px] lg:text-xs font-black text-slate-300 uppercase tracking-widest">선호 숙소</span>
                            <p class="text-lg lg:text-xl font-bold text-slate-700">${empty me.stayNames ? '🔒' : target.stayNames}</p>
                        </div>
                        <div class="p-5 lg:p-8 rounded-2xl lg:rounded-3xl border border-slate-100 bg-slate-50/50 space-y-2 lg:space-y-3">
                            <span class="text-[10px] lg:text-xs font-black text-slate-300 uppercase tracking-widest">사용 언어</span>
                            <p class="text-lg lg:text-xl font-bold text-slate-700">${empty me.languageNames ? '🔒' : target.languageNames}</p>
                        </div>

                    </div>                    
                </div>
            </div>
        </div>
    </main>

    <script>
        // 서버에서 넘어온 메시지 알림
        const alertMessage = '${message}';
        if (alertMessage) {
            alert(alertMessage);
        }

        // 신고하기 함수
        function reportUser(seq, nickname) {
            // 본인 신고 방지용 (시큐리티 로그인 연동 후)
            /*
            const mySeq = '${sessionScope.seqMember}'; 
            if(seq == mySeq) { 
                alert('본인을 신고할 수 없습니다. 😅'); 
                return; 
            }
            */

            if (confirm("[" + nickname + "] 사용자를 부적절한 활동으로 신고하시겠습니까?")) {
                let form = document.createElement('form');
                form.action = '${pageContext.request.contextPath}/report/add.do';
                form.method = 'POST';
                
                // 스프링 시큐리티 CSRF 토큰
                let csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = '${_csrf.parameterName}';
                csrfInput.value = '${_csrf.token}';
                form.appendChild(csrfInput);

                let inputTargetType = document.createElement('input');
                inputTargetType.type = 'hidden';
                inputTargetType.name = 'targetType';
                inputTargetType.value = 'USER';
                form.appendChild(inputTargetType);

                let inputSeqTarget = document.createElement('input');
                inputSeqTarget.type = 'hidden';
                inputSeqTarget.name = 'seqTarget';
                inputSeqTarget.value = seq;
                form.appendChild(inputSeqTarget);

                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</body>
</html>