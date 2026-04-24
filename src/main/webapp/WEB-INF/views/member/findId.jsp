<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>아이디 찾기 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>
    
    <main class="flex-grow flex items-start justify-center pt-24 pb-10 px-4">
        <div class="bg-white p-8 rounded-xl shadow-sm border border-slate-200 w-full max-w-md">
            <div class="mb-8 text-center">
                <h1 class="text-3xl font-bold text-primary mb-2">아이디 찾기</h1>
                <p class="text-slate-500">가입 시 등록한 이름과 이메일을 입력해 주세요.</p>
            </div>

            <form action="/member/findIdResult.do" method="POST" id="findIdForm">
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">이름</span></label>
                    <input type="text" name="name" id="nameInput" placeholder="이름을 입력하세요" class="input input-bordered w-full" required autofocus />
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">이메일</span></label>
                    <div class="flex gap-2 mb-2">
                        <input type="email" name="email" id="emailInput" placeholder="이메일을 입력하세요" class="input input-bordered flex-1" required />
                        <button type="button" id="sendBtn" class="btn btn-info text-white w-32 shrink-0" onclick="sendAuthCode()">인증번호 전송</button>
                    </div>

                    <div class="flex gap-2 relative">
                        <div class="relative flex-1">
                            <input type="text" id="authCodeInput" placeholder="인증번호를 적으시오" class="input input-bordered w-full pr-16" disabled />
                            <span id="timer" class="absolute right-3 top-1/2 -translate-y-1/2 text-error font-bold text-sm"></span>
                        </div>
                        <button type="button" id="verifyBtn" class="btn btn-success text-white w-32 shrink-0" onclick="verifyAuthCode()" disabled>인증 확인</button>
                    </div>
                </div>

                <button type="button" id="findIdBtn" class="btn btn-primary w-full text-white text-lg mt-6 mb-4" disabled onclick="submitFindId()">아이디 찾기</button>
                
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </form>

            <div class="flex justify-between text-sm text-slate-500 mt-6 px-2">
                <a href="/member/findPw.do" class="hover:text-primary font-bold hover:underline">비밀번호 찾기</a>
                <a href="/member/join.do" class="hover:text-primary font-bold hover:underline">회원가입</a>
            </div>
        </div>
    </main>

    <dialog id="customModal" class="modal">
        <div class="modal-box text-center">
            <h3 class="font-bold text-lg mb-2">알림</h3>
            <p class="py-4 text-slate-600" id="modalMessage"></p>
            <div class="modal-action justify-center mt-2">
                <form method="dialog">
                    <button class="btn btn-neutral w-24">확인</button>
                </form>
            </div>
        </div>
    </dialog>

    <script>
    let timerInterval;
    let isEmailVerified = false; // 인증 완료 여부 체크 변수

    // 모달 띄우기 함수
    function showAlert(msg) {
        $('#modalMessage').text(msg);
        document.getElementById('customModal').showModal();
    }

    // 1. 인증번호 전송
    function sendAuthCode() {
        const name = $('#nameInput').val();
        const email = $('#emailInput').val();
        // 스프링 시큐리티용 CSRF 토큰
        const csrfToken = $("input[name='_csrf']").val(); 
        
        // 이름을 작성하지 않았으면 진행할 수 없음
        if(!name) {
            showAlert("이름을 먼저 입력해주세요!");
            $('#nameInput').focus();
            return;
        }

        if(!email) {
            showAlert("이메일을 입력해주세요!");
            $('#emailInput').focus();
            return;
        }

        const $btn = $('#sendBtn');
        $btn.prop('disabled', true).text('전송 중...');

        $.ajax({
            type: "POST",
            url: "/member/sendAuthEmailForFindId.do",
            data: { 
                name: name,   // 이름도 같이 보냄
                email: email, 
                _csrf: csrfToken // CSRF 토큰 
            },
            success: function(response) {
                if(response === "SUCCESS") { 
                    showAlert("인증번호가 발송되었습니다. 10분 안에 입력해주세요.");
                    $btn.text('재발송'); 
                    $btn.prop('disabled', false);
                    
                    $('#authCodeInput').prop('disabled', false).focus();
                    $('#verifyBtn').prop('disabled', false);
                    startTimer(10 * 60); 
                } else if (response === "NOT_FOUND") {
                    // DB에 회원이 없을 때의 에러 처리
                    showAlert("입력하신 정보와 일치하는 가입 내역이 없습니다.");
                    $btn.prop('disabled', false).text('인증번호 전송');
                } else {
                    showAlert("인증번호 발송에 실패했습니다.");
                    $btn.prop('disabled', false).text('인증번호 전송');
                }
            },
            error: function() {
                showAlert("서버 통신 오류가 발생했습니다.");
                $btn.prop('disabled', false).text('인증번호 전송');
            }
        });
    }

    // 타이머 함수 (회원가입과 동일)
    function startTimer(duration) {
        let timer = duration;
        const $timerDisplay = $('#timer');
        clearInterval(timerInterval); 
        timerInterval = setInterval(function () {
            let minutes = parseInt(timer / 60, 10);
            let seconds = parseInt(timer % 60, 10);
            minutes = minutes < 10 ? "0" + minutes : minutes;
            seconds = seconds < 10 ? "0" + seconds : seconds;
            $timerDisplay.text(minutes + ":" + seconds);

            if (--timer < 0) {
                clearInterval(timerInterval);
                $timerDisplay.text("만료됨");
                $('#authCodeInput, #verifyBtn').prop('disabled', true);
                showAlert("인증 시간이 만료되었습니다.");
            }
        }, 1000);
    }

    // 2. 인증번호 확인 (회원가입과 동일)
    function verifyAuthCode() {
        const inputCode = $('#authCodeInput').val();
        // AJAX에 CSRF 토큰 챙겨주기
        const csrfToken = $("input[name='_csrf']").val();
        if(!inputCode) {
            showAlert("인증번호를 입력해주세요!");
            return;
        }

        $.ajax({
            type: "POST",
            url: "/member/verifyAuthCode.do",
            // AJAX가 서버로 갈 때 CSRF 토큰도 같이 챙겨 보냄
            data: { inputCode: inputCode, _csrf: csrfToken },
            success: function(response) {
                if(response === "MATCH") {
                    showAlert("인증번호가 일치합니다."); // 기획서 요구사항 텍스트
                    clearInterval(timerInterval);
                    $('#timer').text("인증완료").removeClass('text-error').addClass('text-success');
                    $('#authCodeInput, #sendBtn, #verifyBtn').prop('disabled', true); 
                    $('#emailInput').prop('readonly', true).addClass('bg-slate-200');
                    
                    // 인증 성공 시 아이디 찾기 버튼 활성화
                    isEmailVerified = true;
                    $('#findIdBtn').prop('disabled', false);
                } else {
                    showAlert("인증번호가 일치하지 않습니다."); 
                }
            }
        });
    }

    // 3. 폼 제출 (아이디 찾기 버튼) - AJAX
    function submitFindId() {
        const name = $('#nameInput').val();
        const email = $('#emailInput').val();
        const csrfToken = $("input[name='_csrf']").val();

        if(!name) {
            showAlert("이름을 입력해주세요.");
            $('#nameInput').focus();
            return;
        }
        if(!isEmailVerified) {
            showAlert("이메일 인증을 먼저 완료해주세요.");
            return;
        }

        const $btn = $('#findIdBtn');
        $btn.prop('disabled', true).text('처리 중...');

        // 페이지 이동(submit) 대신 서버에 요청(AJAX)을 보냄
        $.ajax({
            type: "POST",
            url: "/member/findIdResult.do",
            data: { 
                name: name,
                email: email,
                _csrf: csrfToken
            },
            success: function(response) {
                if(response === "SUCCESS") {
                    // 성공 모달 띄우기
                    $('#modalMessage').text("가입하신 이메일로 아이디가 발송되었습니다.");
                    
                    // 모달의 '확인' 버튼을 누르면 로그인 페이지로 이동
                    $('#customModal form button').attr('onclick', "location.href='/member/login.do'");
                    
                    document.getElementById('customModal').showModal();
                } else {
                    showAlert("아이디 찾기 처리 중 문제가 발생했습니다.");
                    $btn.prop('disabled', false).text('아이디 찾기');
                }
            },
            error: function() {
                showAlert("서버 통신 오류가 발생했습니다.");
                $btn.prop('disabled', false).text('아이디 찾기');
            }
        });
    }
    </script>
</body>
</html>