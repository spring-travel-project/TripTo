<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>
    
    <main class="max-w-2xl mx-auto py-10 px-4">
        <div class="mb-8 text-center">
            <h1 class="text-3xl font-bold">회원가입</h1>
        </div>

        <div class="bg-white p-8 rounded-xl shadow-sm border border-slate-200">
            <form action="/member/join.do" method="POST" id="joinForm">
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">이름</span></label>
                    <input type="text" name="name" placeholder="이름을 입력하세요" class="input input-bordered w-full" required />
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">닉네임</span></label>
                    <input type="text" name="nickname" placeholder="닉네임을 입력하세요" class="input input-bordered w-full" required />
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">아이디</span></label>
                    <div class="flex gap-2">
                        <input type="text" name="id" id="userId" placeholder="아이디를 입력하세요" class="input input-bordered w-full" required />
                        <button type="button" class="btn btn-neutral" onclick="checkId()">중복확인</button>
                    </div>
                    <label class="label"><span class="label-text-alt text-error" id="idCheckMsg"></span></label>
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">비밀번호</span></label>
                    <input type="password" name="pw" placeholder="비밀번호를 입력하세요" class="input input-bordered w-full mb-2" required />
                    <input type="password" name="pwConfirm" placeholder="비밀번호를 다시 입력하세요" class="input input-bordered w-full" required />
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">이메일</span></label>
                    
                    <div class="flex gap-2 mb-2">
                        <input type="email" name="email" id="emailInput" placeholder="이메일을 입력하세요" class="input input-bordered flex-1" required />
                        <button type="button" id="sendBtn" class="btn btn-info text-white w-32 shrink-0" onclick="sendAuthCode()">인증번호 전송</button>
                    </div>

                    <div class="flex gap-2 relative">
                        <div class="relative flex-1">
                            <input type="text" id="authCodeInput" placeholder="인증번호를 입력하세요" class="input input-bordered w-full pr-16" disabled />
                            
                            <span id="timer" class="absolute right-3 top-1/2 -translate-y-1/2 text-error font-bold text-sm"></span>
                        </div>
                        
                        <button type="button" id="verifyBtn" class="btn btn-success text-white w-32 shrink-0" onclick="verifyAuthCode()" disabled>인증 확인</button>
                    </div>
                </div>

                <div class="form-control mb-6">
                    <label class="label"><span class="label-text font-bold">성별</span></label>
                    <div class="flex gap-4">
                        <label class="cursor-pointer flex items-center gap-2">
                            <input type="radio" name="gender" value="0" class="radio radio-primary" checked /> <span class="label-text">남</span>
                        </label>
                        <label class="cursor-pointer flex items-center gap-2">
                            <input type="radio" name="gender" value="1" class="radio radio-primary" /> <span class="label-text">여</span>
                        </label>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-full text-white text-lg">회원가입</button>
            </form>
        </div>
    </main>
    
    <script>
    let timerInterval; // 타이머를 담을 변수

    // 1. 이메일 인증번호 전송 (또는 재발송)
    function sendAuthCode() {
        const email = $('#emailInput').val();
        
        if(!email) {
            alert("이메일을 입력해주세요!");
            $('#emailInput').focus();
            return;
        }

        const $btn = $('#sendBtn');
        $btn.prop('disabled', true).text('전송 중...');

        $.ajax({
            type: "POST",
            url: "/member/sendAuthEmail.do",
            data: { email: email },
            success: function(response) {
                if(response === "SUCCESS") {
                    alert("인증번호가 발송되었습니다. 10분 안에 입력해주세요.");
                    $btn.text('인증번호 재발송'); // 버튼 텍스트 변경
                    $btn.prop('disabled', false);
                    
                    // 입력창 & 확인 버튼 활성화
                    $('#authCodeInput').prop('disabled', false).focus();
                    $('#verifyBtn').prop('disabled', false);
                    
                    startTimer(10 * 60); // 10분 타이머 시작
                } else {
                    alert("인증번호 발송에 실패했습니다.");
                    $btn.prop('disabled', false).text('인증번호 전송');
                }
            },
            error: function() {
                alert("서버 통신 오류가 발생했습니다.");
                $btn.prop('disabled', false).text('인증번호 전송');
            }
        });
    }

    // 1-1. 인증번호 유효 시간 10분 타이머 함수
    function startTimer(duration) {
        let timer = duration;
        const $timerDisplay = $('#timer');
        
        // 기존에 돌고 있는 타이머가 있다면 초기화 (재발송을 누를 경우 대비)
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
                $('#authCodeInput').prop('disabled', true);
                $('#verifyBtn').prop('disabled', true);
                alert("인증 시간이 만료되었습니다. 인증번호 재발송을 눌러주세요.");
            }
        }, 1000);
    }

    // 2. 인증번호 확인 기능 (AJAX)
    function verifyAuthCode() {
        const inputCode = $('#authCodeInput').val();
        
        if(!inputCode) {
            alert("인증번호를 입력해주세요!");
            return;
        }

        $.ajax({
            type: "POST",
            url: "/member/verifyAuthCode.do",
            data: { inputCode: inputCode },
            success: function(response) {
                if(response === "MATCH") {
                    alert("이메일 인증이 완료되었습니다!");
                    clearInterval(timerInterval); // 타이머 멈춤
                    $('#timer').text("인증완료").removeClass('text-error').addClass('text-success');
                    $('#emailInput, #authCodeInput, #sendBtn, #verifyBtn').prop('disabled', true); // 인증이 완료되었으니 더 이상 수정 못하게 막음
                } else if(response === "EXPIRED") {
                    alert("인증 시간이 만료되었습니다. 재발송을 눌러주세요.");
                } else if(response === "MISMATCH") {
                    alert("인증번호가 일치하지 않습니다.");
                } else {
                    alert("인증 요청을 먼저 진행해주세요.");
                }
            },
            error: function() {
                alert("서버 통신 오류가 발생했습니다.");
            }
        });
    }

    // 3. 아이디 중복확인 기능
    function checkId() { 
        alert("아이디 중복확인 기능 구현 필요"); 
    }
    </script>
</body>
</html>