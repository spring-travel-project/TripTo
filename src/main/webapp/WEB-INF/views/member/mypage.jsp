<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>마이페이지 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-[#F8FAFC] text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow max-w-6xl w-full mx-auto px-4 py-10 flex gap-8 items-start">
        
        <aside class="w-64 shrink-0 sticky top-24">
            <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-4">
                <ul class="menu w-full gap-2 font-bold text-slate-600 text-base">
                    <li><a href="${pageContext.request.contextPath}/member/mypage.do" class="active bg-blue-50 text-blue-600">내 정보 보기</a></li>
                    
                    <li><a href="${pageContext.request.contextPath}/member/myactivity.do?tab=TRAVEL" class="hover:bg-slate-50 transition-colors">내가 작성한 동행 게시글</a></li>
                    <li><a href="${pageContext.request.contextPath}/member/myactivity.do?tab=BOARD" class="hover:bg-slate-50 transition-colors">내가 작성한 일반 게시글</a></li>
                    <li><a href="${pageContext.request.contextPath}/member/myactivity.do?tab=COMMENT" class="hover:bg-slate-50 transition-colors">내가 작성한 댓글</a></li>
                </ul>
            </div>
        </aside>

        <section class="flex-1 bg-white rounded-[2.5rem] shadow-sm border border-slate-200 overflow-hidden relative">
            
            <div class="h-56 bg-slate-200 relative group">
                <c:choose>
                    <%-- 사진이 없거나 기본 이미지인 경우 --%>
                    <c:when test="${empty profile.coverPic or profile.coverPic eq 'default_cover.png'}">
                        <img src="${pageContext.request.contextPath}/resources/img/default_cover.png" class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105">
                    </c:when>
                    <%-- 클라우드 주소(http)인 경우 --%>
                    <c:when test="${profile.coverPic.startsWith('http')}">
                        <img src="${profile.coverPic}" class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105">
                    </c:when>
                    <%-- 로컬 파일명인 경우 --%>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/resources/upload/cover/${profile.coverPic}" class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105">
                    </c:otherwise>
                </c:choose>
                <div class="absolute inset-0 bg-black/10"></div>
                
                <div class="absolute top-8 right-12 z-10">
                    <button onclick="location.href='${pageContext.request.contextPath}/member/editProfile.do'" class="px-5 py-2.5 rounded-xl bg-white/90 hover:bg-white border border-slate-200 shadow-sm font-bold text-slate-700 text-sm transition-all hover:scale-105">
                        <c:choose>
                            <c:when test="${empty profile}">프로필 작성</c:when>
                            <c:otherwise>프로필 수정</c:otherwise>
                        </c:choose>
                    </button>
                </div>
            </div>

            <div class="px-12 pb-12 relative">
                
                <div class="flex items-end justify-between mb-10 -mt-16 relative z-10">
                    <div class="flex items-end gap-6">
                        <div class="w-36 h-36 rounded-full border-4 border-white bg-slate-100 shadow-lg overflow-hidden">
                            <c:choose>
                                <%-- 사진이 없거나 기본 이미지인 경우 --%>
                                <c:when test="${empty member.pic or member.pic eq 'pic.png'}">
                                    <img src="${pageContext.request.contextPath}/resources/img/pic.png" class="w-full h-full object-cover">
                                </c:when>
                                <%-- 클라우드 주소(http)인 경우 --%>
                                <c:when test="${member.pic.startsWith('http')}">
                                    <img src="${member.pic}" class="w-full h-full object-cover">
                                </c:when>
                                <%-- 로컬 파일명인 경우 --%>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/resources/upload/profile/${member.pic}" class="w-full h-full object-cover">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="pb-3">
                            <h2 class="text-3xl font-black text-slate-900 flex items-center gap-2">
                                ${member.nickname} 
                                <span class="text-lg font-bold text-slate-400">(${member.name})</span>
                            </h2>
                            <p class="text-sm font-medium text-slate-500 mt-1.5">
                                ${member.email} <span class="mx-2 text-slate-300">|</span> 가입일: ${member.regDate}
                            </p>
                        </div>
                    </div>
                    
                    <div class="pb-3">
                        <button onclick="document.getElementById('accountSettingsModal').showModal()" class="px-5 py-2.5 rounded-xl bg-white border-2 border-slate-200 text-slate-600 font-bold text-sm hover:border-slate-300 hover:bg-slate-50 transition-colors shadow-sm self-start mt-1">계정 설정</button>
                    </div>
                </div>

                <div class="rounded-3xl p-10 relative bg-white border border-slate-100">
                    <div class="grid grid-cols-2 gap-y-8 gap-x-12">
                        
                        <div>
                            <label class="block text-sm font-bold text-slate-400 mb-2">아이디</label>
                            <div class="bg-white border border-slate-200 rounded-xl px-5 py-3.5 text-slate-700 font-bold text-lg">${member.id}</div>
                        </div>
                        
                        <div>
                            <label class="block text-sm font-bold text-slate-400 mb-2">개요</label>
                            <div class="bg-white border border-slate-200 rounded-xl px-5 py-3.5 text-slate-700 font-bold text-lg">
                                ${ageGroup}, ${member.gender == 0 ? '남자' : '여자'}, ${member.region}
                            </div>
                        </div>
                        
                        <div>
                            <label class="block text-sm font-bold text-slate-400 mb-2">비밀번호</label>
                            <div class="bg-white border border-slate-200 rounded-xl px-5 py-3.5 text-slate-400 font-black tracking-[0.2em] text-lg">
                                *************
                            </div>
                        </div>
                        
                        <div>
                            <label class="block text-sm font-bold text-slate-400 mb-2">MBTI</label>
                            <div class="bg-white border border-slate-200 rounded-xl px-5 py-3.5 text-blue-600 font-black tracking-wider text-lg">
                                ${empty profile.mbti ? '미입력' : profile.mbti}
                            </div>
                        </div>
                        
                        <div class="col-span-2">
                            <label class="block text-sm font-bold text-slate-400 mb-2">자기소개</label>
                            <div class="bg-white border border-slate-200 rounded-xl px-5 py-4 text-slate-600 font-medium leading-relaxed min-h-[100px]">
                                ${empty member.intro ? '아직 자기소개를 작성하지 않았습니다.' : member.intro}
                            </div>
                        </div>
                        
                        <div class="col-span-2">
                            <label class="block text-sm font-bold text-slate-400 mb-2">본인의 여행 선호도</label>
                            <div class="bg-slate-50 border border-slate-200 rounded-xl px-6 py-8 min-h-[160px] mt-4">
                                <c:choose>
                                    <c:when test="${empty profile}">
                                        <div class="h-full flex flex-col items-center justify-center text-center text-slate-400 font-bold leading-relaxed">
                                            아직 상세 프로필을 등록하지 않으셨습니다.<br>우측 상단의 버튼을 눌러 나만의 여행 취향을 작성해 보세요!
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="grid grid-cols-2 gap-x-8 gap-y-5 text-[15px] font-bold text-slate-700">
                                            <p><span class="text-blue-500 mr-2 text-base">📌 선호 숙소:</span> ${empty profile.stayNames ? '미선택' : profile.stayNames}</p>
                                            <p><span class="text-blue-500 mr-2 text-base">🗣️ 사용 언어:</span> ${empty profile.languageNames ? '미선택' : profile.languageNames}</p>
                                            <p><span class="text-blue-500 mr-2 text-base">👣 활동량:</span> ${profile.stepCount == 0 ? '5천보 이하' : (profile.stepCount == 1 ? '5천 ~ 1만보' : '1만보 이상')}</p>
                                            <p><span class="text-blue-500 mr-2 text-base">🏖️ 여행 스타일:</span> ${profile.travelType == 0 ? '휴양' : '관광'}</p>
                                            <p><span class="text-blue-500 mr-2 text-base">🚬 흡연 여부:</span> ${profile.smoking == 1 ? '비흡연' : '흡연'}</p>
                                            <p><span class="text-blue-500 mr-2 text-base">🍻 음주 여부:</span> ${profile.drinking == 2 ? '안함' : (profile.drinking == 1 ? '가끔' : '자주')}</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </section>

    </main>

    <dialog id="accountSettingsModal" class="modal">
        <div class="modal-box">
            <form method="dialog">
                <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
            </form>
            <h3 class="font-bold text-2xl mb-6 text-slate-800">계정 설정</h3>
            
            <div class="flex flex-col gap-3">
                <button type="button" onclick="location.href='${pageContext.request.contextPath}/member/editInfo.do'" class="btn border-slate-200 bg-white text-slate-700 hover:border-blue-500 hover:bg-blue-50 w-full justify-start text-base h-14">👤 내 정보 수정 (기본 정보)</button>
                <button type="button" onclick="openChangePwModal()" class="btn border-slate-200 bg-white text-slate-700 hover:border-blue-500 hover:bg-blue-50 w-full justify-start text-base h-14">🔒 비밀번호 변경</button>
                <button type="button" onclick="openDeactivateModal()" class="btn border-slate-200 bg-white text-rose-500 hover:border-rose-500 hover:bg-rose-50 w-full justify-start text-base h-14">🚨 계정 탈퇴</button>
            </div>
        </div>
    </dialog>

    <dialog id="changePwModal" class="modal">
        <div class="modal-box">
            <form method="dialog"><button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button></form>
            <h3 class="font-bold text-2xl mb-6 text-slate-800">비밀번호 변경</h3>
            
            <form id="changePwForm" action="${pageContext.request.contextPath}/member/changePw.do" method="POST">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">현재 비밀번호</span></label>
                    <div class="flex gap-2">
                        <input type="password" id="currentPwInput" placeholder="현재 비밀번호를 입력하세요." class="input input-bordered w-full" required />
                        <button type="button" id="checkPwBtn" class="btn btn-neutral shrink-0" onclick="verifyCurrentPw()">확인</button>
                    </div>
                    <label class="label"><span class="label-text-alt" id="pwCheckAlert"></span></label>
                </div>

                <div id="newPwSection" class="hidden border-t border-slate-100 pt-4 mt-2">
                    <div class="form-control mb-4">
                        <label class="label"><span class="label-text font-bold">새 비밀번호</span></label>
                        <input type="password" name="newPw" id="newPwInput" placeholder="새 비밀번호를 입력하세요." class="input input-bordered w-full" required />
                    </div>
                    <div class="form-control mb-6">
                        <label class="label"><span class="label-text font-bold">새 비밀번호 재확인</span></label>
                        <input type="password" id="newPwConfirmInput" placeholder="다시 한 번 입력하세요." class="input input-bordered w-full" required />
                        <label class="label"><span class="label-text-alt text-error" id="newPwAlert"></span></label>
                    </div>
                    <button type="button" id="submitNewPwBtn" class="btn btn-primary w-full text-white text-lg" disabled onclick="submitChangePw()">비밀번호 재설정</button>
                </div>
            </form>
        </div>
    </dialog>

    <dialog id="deactivateModal" class="modal">
        <div class="modal-box">
            <h3 class="font-black text-2xl mb-2 text-rose-600">정말 탈퇴하시겠습니까?</h3>
            <p class="text-slate-500 mb-6">탈퇴 시 모든 정보가 삭제되며 복구할 수 없습니다.</p>
            <div class="modal-action flex gap-2">
                <form method="dialog" class="flex-1"><button class="btn btn-outline w-full">취소</button></form>
                <form action="${pageContext.request.contextPath}/member/deactivate.do" method="POST" class="flex-1">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <button type="submit" class="btn btn-error text-white w-full">탈퇴하기</button>
                </form>
            </div>
        </div>
    </dialog>

    <script>
        const csrfToken = $("input[name='_csrf']").val();
        let isNewPwValid = false;

        function openChangePwModal() {
            document.getElementById('accountSettingsModal').close();
            document.getElementById('changePwModal').showModal();
        }

        function openDeactivateModal() {
            document.getElementById('accountSettingsModal').close();
            document.getElementById('deactivateModal').showModal();
        }

        function verifyCurrentPw() {
            const currentPw = $('#currentPwInput').val();
            if(!currentPw) {
                $('#pwCheckAlert').text('비밀번호를 입력해주세요.').removeClass('text-success').addClass('text-error');
                return;
            }

            $.ajax({
                type: "POST",
                url: "${pageContext.request.contextPath}/member/checkCurrentPw.do",
                data: { currentPw: currentPw, _csrf: csrfToken },
                success: function(response) {
                    if(response === "MATCH") {
                        $('#pwCheckAlert').text('비밀번호가 일치합니다.').removeClass('text-error').addClass('text-success');
                        $('#currentPwInput, #checkPwBtn').prop('disabled', true);
                        $('#newPwSection').removeClass('hidden');
                    } else {
                        $('#pwCheckAlert').text('비밀번호가 일치하지 않습니다.').removeClass('text-success').addClass('text-error');
                    }
                }
            });
        }

        $('#newPwInput, #newPwConfirmInput').on('keyup', function() {
            const pw = $('#newPwInput').val();
            const pwConfirm = $('#newPwConfirmInput').val();
            const $msg = $('#newPwAlert');
            const pwRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_~]).{8,}$/;

            if (!pwRegex.test(pw)) {
                $msg.text("영문, 숫자, 특수문자를 포함해 8자리 이상이어야 합니다.").removeClass("text-success").addClass("text-error");
                isNewPwValid = false;
            } else if (pw !== pwConfirm) {
                $msg.text("비밀번호가 일치하지 않습니다.").removeClass("text-success").addClass("text-error");
                isNewPwValid = false;
            } else {
                $msg.text("사용 가능한 비밀번호입니다.").removeClass("text-error").addClass("text-success");
                isNewPwValid = true;
            }
            $('#submitNewPwBtn').prop('disabled', !isNewPwValid);
        });

        function submitChangePw() {
            if(isNewPwValid) {
                alert("비밀번호가 성공적으로 변경되었습니다. 다시 로그인 해주세요.");
                $('#changePwForm').submit();
            }
        }
    </script>
</body>
</html>