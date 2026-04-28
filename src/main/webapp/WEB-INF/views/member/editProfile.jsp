<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>프로필 편집 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800 pb-20">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>
    
    <main class="max-w-3xl mx-auto py-10 px-4">
        
        <div class="mb-6">
            <a href="javascript:history.back()" class="inline-flex items-center text-slate-400 hover:text-blue-600 font-bold transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5 mr-2"><path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" /></svg>
                마이페이지로 돌아가기
            </a>
        </div>

        <div class="mb-10">
            <h1 class="text-3xl font-black text-slate-900 mb-2">나만의 여행 프로필</h1>
            <p class="text-slate-500 font-medium">입력하신 성향 정보는 더 잘 맞는 동행을 추천하기 위해 사용됩니다.</p>
        </div>

        <div class="bg-white p-8 md:p-12 rounded-[2.5rem] shadow-sm border border-slate-200">
            
            <form action="${pageContext.request.contextPath}/member/editProfile.do?${_csrf.parameterName}=${_csrf.token}" method="POST" id="profileForm" enctype="multipart/form-data">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">📸 마이페이지 배경 사진 (선택)</label>
                    <div class="h-48 rounded-2xl bg-slate-100 border-2 border-dashed border-slate-300 flex flex-col items-center justify-center relative overflow-hidden group">
                        <img id="coverPreview" src="${pageContext.request.contextPath}/resources/img/${empty profile.coverPic ? 'default_cover.png' : profile.coverPic}" class="absolute inset-0 w-full h-full object-cover z-0 transition-transform group-hover:scale-105">
                        <div class="absolute inset-0 bg-black/40 z-10 flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                            <span class="text-white font-bold mb-2">사진 변경하기</span>
                            <input type="file" name="coverFile" class="file-input file-input-bordered file-input-sm w-full max-w-xs" accept="image/*" />
                        </div>
                    </div>
                </div>

                <div class="divider mb-12"></div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">✨ 당신의 MBTI는 무엇인가요? <span class="text-red-500">*</span></label>
                    <input type="text" name="mbti" value="${profile.mbti}" placeholder="예: ENFP" maxlength="4" class="input input-bordered w-full max-w-xs font-black text-xl uppercase tracking-widest text-blue-600" required />
                </div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">🚬 흡연 유무를 알려주세요. <span class="text-red-500">*</span></label>
                    <div class="flex flex-wrap gap-3">
                        <label class="cursor-pointer">
                            <input type="radio" name="smoking" value="1" class="peer sr-only" ${profile.smoking == 1 ? 'checked' : ''} required>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">비흡연</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="smoking" value="0" class="peer sr-only" ${profile.smoking == 0 ? 'checked' : ''}>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">흡연자</div>
                        </label>
                    </div>
                </div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">🍻 음주 여부를 알려주세요. <span class="text-red-500">*</span></label>
                    <div class="flex flex-wrap gap-3">
                        <label class="cursor-pointer">
                            <input type="radio" name="drinking" value="2" class="peer sr-only" ${profile.drinking == 2 ? 'checked' : ''} required>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">마시지 않음</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="drinking" value="1" class="peer sr-only" ${profile.drinking == 1 ? 'checked' : ''}>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">가끔 마심</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="drinking" value="0" class="peer sr-only" ${profile.drinking == 0 ? 'checked' : ''}>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">즐겨 마심</div>
                        </label>
                    </div>
                </div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">👣 여행 중 하루 최대 걸음수는? <span class="text-red-500">*</span></label>
                    <div class="flex flex-wrap gap-3">
                        <label class="cursor-pointer">
                            <input type="radio" name="stepCount" value="0" class="peer sr-only" ${profile.stepCount == 0 ? 'checked' : ''} required>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">5천보 이하</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="stepCount" value="1" class="peer sr-only" ${profile.stepCount == 1 ? 'checked' : ''}>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">5천 ~ 1만보</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="stepCount" value="2" class="peer sr-only" ${profile.stepCount == 2 ? 'checked' : ''}>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">1만보 이상</div>
                        </label>
                    </div>
                </div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">🏖️ 선호하는 여행 스타일은? <span class="text-red-500">*</span></label>
                    <div class="flex flex-wrap gap-3">
                        <label class="cursor-pointer">
                            <input type="radio" name="travelType" value="0" class="peer sr-only" ${profile.travelType == 0 ? 'checked' : ''} required>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">정적인 휴양</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="travelType" value="1" class="peer sr-only" ${profile.travelType == 1 ? 'checked' : ''}>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">상관 없음</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="radio" name="travelType" value="2" class="peer sr-only" ${profile.travelType == 2 ? 'checked' : ''}>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:bg-blue-50 peer-checked:text-blue-600 hover:bg-slate-50">활동적인 관광</div>
                        </label>
                    </div>
                </div>

                <div class="divider mb-10"></div>
                <div class="mb-8 bg-blue-50/50 border border-blue-100 p-5 rounded-2xl">
                    <p class="text-[15px] text-blue-600 font-bold">💡 아래 항목들은 취향에 맞게 여러 개 선택 가능합니다!</p>
                </div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">🏠 선호하는 숙소 유형</label>
                    <div class="flex flex-wrap gap-3">
                        <label class="cursor-pointer">
                            <input type="checkbox" name="staySeqs" value="1" class="peer sr-only" <c:if test="${fn:contains(profile.stayNames, '호텔')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">호텔</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="checkbox" name="staySeqs" value="2" class="peer sr-only" <c:if test="${fn:contains(profile.stayNames, '에어비앤비')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">에어비앤비</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="checkbox" name="staySeqs" value="3" class="peer sr-only" <c:if test="${fn:contains(profile.stayNames, '게스트하우스')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">게스트하우스</div>
                        </label>
                    </div>
                </div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">🗣️ 사용 가능한 언어</label>
                    <div class="flex flex-wrap gap-3">
                        <label class="cursor-pointer">
                            <input type="checkbox" name="languageSeqs" value="1" class="peer sr-only" <c:if test="${fn:contains(profile.languageNames, '한국어')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">한국어</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="checkbox" name="languageSeqs" value="2" class="peer sr-only" <c:if test="${fn:contains(profile.languageNames, '영어')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">영어</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="checkbox" name="languageSeqs" value="3" class="peer sr-only" <c:if test="${fn:contains(profile.languageNames, '일본어')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">일본어</div>
                        </label>
                    </div>
                </div>

                <div class="mb-12">
                    <label class="block text-lg font-bold text-slate-800 mb-4">👫 원하는 동행의 연령대</label>
                    <div class="flex flex-wrap gap-3">
                        <label class="cursor-pointer">
                            <input type="checkbox" name="ageGroupSeqs" value="1" class="peer sr-only" <c:if test="${fn:contains(profile.ageGroupNames, '20대 초반')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">20대 초반</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="checkbox" name="ageGroupSeqs" value="2" class="peer sr-only" <c:if test="${fn:contains(profile.ageGroupNames, '20대 중후반')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">20대 중후반</div>
                        </label>
                        <label class="cursor-pointer">
                            <input type="checkbox" name="ageGroupSeqs" value="3" class="peer sr-only" <c:if test="${fn:contains(profile.ageGroupNames, '30대 초반')}">checked</c:if>>
                            <div class="px-6 py-3 rounded-full border border-slate-200 text-slate-600 font-bold text-[15px] transition-all peer-checked:border-blue-500 peer-checked:text-blue-600 hover:bg-slate-50">30대 초반</div>
                        </label>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-full text-white text-xl h-16 rounded-2xl shadow-lg hover:shadow-xl mt-4">
                    나의 여행 프로필 저장하기
                </button>
            </form>

        </div>
    </main>
</body>
</html>