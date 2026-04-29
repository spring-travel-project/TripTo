<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>내 정보 수정 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>
    
    <main class="max-w-2xl mx-auto py-10 px-4">
        
        <div class="mb-6">
            <a href="javascript:history.back()" class="inline-flex items-center text-slate-400 hover:text-blue-600 font-bold transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5 mr-2"><path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" /></svg>
                마이페이지로 돌아가기
            </a>
        </div>

        <div class="mb-8 text-center">
            <h1 class="text-3xl font-bold text-slate-900">내 정보 수정</h1>
            <p class="text-slate-500 mt-2">기본 프로필 정보를 수정할 수 있습니다.</p>
        </div>

        <div class="bg-white p-8 rounded-xl shadow-sm border border-slate-200">
            <form action="${pageContext.request.contextPath}/member/editInfo.do?${_csrf.parameterName}=${_csrf.token}" method="POST" id="editForm" enctype="multipart/form-data">
                
                <div class="flex flex-col items-center justify-center mb-8">
    <div class="w-28 h-28 rounded-full border-2 border-slate-200 overflow-hidden mb-3">
        <img id="profilePreview" src="${pageContext.request.contextPath}${empty member.pic or member.pic eq 'pic.png' ? '/resources/img/pic.png' : '/resources/upload/profile/' += member.pic}" class="w-full h-full object-cover">
    </div>
    <label class="block text-sm font-bold text-slate-500 mb-2">새 프로필 사진 업로드 (선택)</label>
    <input type="file" name="picFile" class="file-input file-input-bordered file-input-sm w-full max-w-xs" accept="image/*" />
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">닉네임</span></label>
                    <div class="flex gap-2">
                        <input type="text" name="nickname" id="userNickname" value="${member.nickname}" placeholder="새 닉네임을 입력하세요" class="input input-bordered w-full" required />
                        <button type="button" class="btn btn-neutral" onclick="checkNickname()">중복확인</button>
                    </div>
                    <label class="label"><span class="label-text-alt" id="nicknameCheckMsg">닉네임은 10자 이내로만 사용 가능합니다.</span></label>
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">자기소개</span></label>
                    <textarea name="intro" placeholder="간단한 자기소개를 작성해주세요 (150자 이내)" class="textarea textarea-bordered w-full h-24 resize-none leading-relaxed">${member.intro}</textarea>
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">생년월일</span></label>
                    <input type="date" name="birth" value="${member.birth}" class="input input-bordered w-full" required />
                </div>
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">성별</span></label>
                    <div class="flex gap-4 bg-slate-50 p-3 rounded-lg border border-slate-200">
                        <label class="cursor-pointer flex items-center gap-2">
                            <input type="radio" name="gender" value="0" class="radio radio-primary" ${member.gender == 0 ? 'checked' : ''} /> <span class="label-text font-medium">남성</span>
                        </label>
                        <label class="cursor-pointer flex items-center gap-2 ml-4">
                            <input type="radio" name="gender" value="1" class="radio radio-primary" ${member.gender == 1 ? 'checked' : ''} /> <span class="label-text font-medium">여성</span>
                        </label>
                    </div>
                </div>

                <div class="form-control mb-8">
                    <label class="label"><span class="label-text font-bold">거주 국가</span></label>
                    <div class="relative" id="regionWrapper">
                        <input type="text" id="regionSearch" value="${member.region}" placeholder="국가명을 입력하세요" class="input input-bordered w-full" autocomplete="off" />
                        <input type="hidden" name="region" id="regionValue" value="${member.region}" required />
                        <ul id="regionDropdown" class="absolute z-50 w-full bg-white border border-slate-200 rounded-lg shadow-lg mt-1 max-h-48 overflow-y-auto hidden"></ul>
                    </div>
                    <label class="label"><span class="label-text-alt text-error hidden" id="regionCheckMsg">국가를 목록에서 선택해주세요.</span></label>
                </div>
                
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                
                <button type="submit" id="submitBtn" class="btn btn-primary w-full text-white text-lg h-14 rounded-xl">저장하기</button>
            </form>
        </div>
    </main>
    
    <script>
    // 기존 닉네임 백업 (자신의 기존 닉네임은 중복 검사 패스하기 위함)
    const originalNickname = "${member.nickname}";
    let isNicknameChecked = true; // 초기 상태는 기존 닉네임이므로 true

    // 닉네임 변경 시 다시 중복확인 하도록 상태 변경
    $('#userNickname').on('input', function() {
        if($(this).val() !== originalNickname) {
            isNicknameChecked = false;
            $('#nicknameCheckMsg').text("닉네임 중복확인이 필요합니다.").removeClass("text-success").addClass("text-error");
        } else {
            isNicknameChecked = true;
            $('#nicknameCheckMsg').text("기존 닉네임입니다.").removeClass("text-error").addClass("text-success");
        }
    });

    // 닉네임 중복확인 AJAX
    function checkNickname() { 
        const nickname = $('#userNickname').val().trim();
        const $msg = $('#nicknameCheckMsg');
        const csrfToken = $("input[name='_csrf']").val();
        
        if(!nickname) {
            showAlert("닉네임을 입력해주세요.");
            $('#userNickname').focus();
            return;
        }

        if(nickname.length > 10) {
            $msg.text("닉네임은 10자 이내로만 사용 가능합니다.").removeClass("text-success").addClass("text-error");
            return;
        }

        if(nickname === originalNickname) {
            $msg.text("현재 사용 중인 닉네임입니다.").removeClass("text-error").addClass("text-success");
            isNicknameChecked = true;
            return;
        }

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/member/checkNickname.do",
            data: { nickname: nickname, _csrf: csrfToken },
            success: function(response) {
                if(response === "AVAILABLE") {
                    $msg.text("사용 가능한 닉네임입니다.").removeClass("text-error").addClass("text-success");
                    isNicknameChecked = true;
                } else if(response === "DUPLICATE") {
                    $msg.text("이미 사용 중인 닉네임입니다.").removeClass("text-success").addClass("text-error");
                    isNicknameChecked = false;
                }
            }
        });
    }
    
    // 내 정보 수정: 프로필 사진 미리보기 로직
    $('input[name="picFile"]').on('change', function(event) {
        const file = event.target.files[0]; // 유저가 선택한 파일 가져오기
        
        if (file) {
            const reader = new FileReader();
            
            // 파일을 다 읽으면 실행
            reader.onload = function(e) {
                // 방금 HTML에 이름표 달아준 id="profilePreview"의 사진을 싹 교체!
                $('#profilePreview').attr('src', e.target.result);
            }
            
            // 파일 읽기 시작
            reader.readAsDataURL(file);
        }
    });

    // 국가 자동완성 배열 (join.jsp 재활용)
    const countries = [
        { name: "대한민국", en: "South Korea" }, { name: "미국", en: "United States" }, { name: "일본", en: "Japan" },
        { name: "중국", en: "China" }, { name: "영국", en: "United Kingdom" }, { name: "프랑스", en: "France" },
        { name: "독일", en: "Germany" }, { name: "이탈리아", en: "Italy" }, { name: "스페인", en: "Spain" },
        { name: "호주", en: "Australia" }, { name: "태국", en: "Thailand" }, { name: "베트남", en: "Vietnam" },
        { name: "대만", en: "Taiwan" } // ... 필요시 추가
    ];

    const $regionSearch = $('#regionSearch');
    const $regionValue  = $('#regionValue');
    const $dropdown     = $('#regionDropdown');

    $regionSearch.on('input', function() {
        const query = $(this).val().trim().toLowerCase();
        $regionValue.val(''); 
        
        if (!query) { $dropdown.addClass('hidden').empty(); return; }

        const filtered = countries.filter(c => c.name.toLowerCase().includes(query) || c.en.toLowerCase().includes(query)).slice(0, 8);
        if (filtered.length === 0) { $dropdown.addClass('hidden').empty(); return; }
        
        const items = filtered.map(c => '<li class="px-4 py-2 cursor-pointer hover:bg-slate-100 text-sm" data-value="' + c.name + '" data-display="' + c.name + ' (' + c.en + ')">' + c.name + ' <span class="text-slate-400">' + c.en + '</span></li>').join('');
        $dropdown.html(items).removeClass('hidden');
    });

    $dropdown.on('click', 'li', function() {
        $regionSearch.val($(this).data('display'));
        $regionValue.val($(this).data('value'));
        $dropdown.addClass('hidden').empty();
        $('#regionCheckMsg').addClass('hidden');
    });

    $(document).on('click', function(e) {
        if (!$('#regionWrapper').is(e.target) && $('#regionWrapper').has(e.target).length === 0) {
            $dropdown.addClass('hidden');
        }
    });

    // 폼 제출 전 검증
    $('#editForm').on('submit', function(e) {
        if (!isNicknameChecked) {
            e.preventDefault();
            showAlert("닉네임 중복확인이 필요합니다.");
            $('#userNickname').focus();
            return false;
        }
        if (!$regionValue.val()) {
            e.preventDefault();
            $('#regionCheckMsg').removeClass('hidden');
            $regionSearch.focus();
            return false;
        }
    });
    </script>
<%@ include file="/WEB-INF/views/inc/modal.jsp" %>
</body>
</html>