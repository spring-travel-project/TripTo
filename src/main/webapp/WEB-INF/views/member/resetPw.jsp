<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 재설정 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>
    
    <main class="flex-grow flex items-start justify-center pt-24 pb-10 px-4">
        <div class="bg-white p-8 rounded-xl shadow-sm border border-slate-200 w-full max-w-md">
            <div class="mb-8 text-center">
                <h1 class="text-3xl font-bold text-primary mb-2">비밀번호 재설정</h1>
                <p class="text-slate-500">새롭게 사용할 비밀번호를 입력해 주세요.</p>
            </div>

            <form action="${pageContext.request.contextPath}/member/updatePw.do" method="POST" id="resetPwForm">
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">새 비밀번호</span></label>
                    <input type="password" name="pw" id="userPw" placeholder="비밀번호를 입력하세요." class="input input-bordered w-full" required autofocus />
                    <label class="label"><span class="label-text-alt text-error" id="pwMsg1"></span></label>
                </div>

                <div class="form-control mb-6">
                    <label class="label"><span class="label-text font-bold">비밀번호 재확인</span></label>
                    <input type="password" id="userPwConfirm" placeholder="비밀번호 재확인(다시한번 입력하세요)" class="input input-bordered w-full" required disabled />
                    <label class="label"><span class="label-text-alt text-error" id="pwMsg2"></span></label>
                </div>

                <button type="button" id="resetBtn" class="btn btn-primary w-full text-white text-lg mb-4" disabled onclick="submitResetPw()">비밀번호 재설정</button>
                
                <input type="hidden" name="id" value="${targetId}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </form>
        </div>
    </main>

    <script>
    let isPwValid = false;
    let isPwMatch = false;

    // 비밀번호 실시간 유효성 및 일치 검사 (기획서 멘트 완벽 반영)
    $('#userPw, #userPwConfirm').on('keyup', function() {
        const pw = $('#userPw').val();
        const pwConfirm = $('#userPwConfirm').val();
        const $msg1 = $('#pwMsg1');
        const $msg2 = $('#pwMsg2');
        
        // 정규식 (영문, 숫자, 특수문자 포함 8자리 이상)
        const pwRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_~]).{8,}$/;

        // 1. 첫 번째 입력칸 검증
        if (pw === "") {
            $msg1.text(""); 
            isPwValid = false;
            $('#userPwConfirm').prop('disabled', true).val('');
            $msg2.text("");
        } else if (!pwRegex.test(pw)) {
            $msg1.text("사용불가능한 비밀번호입니다.").removeClass("text-success").addClass("text-error");
            isPwValid = false;
            $('#userPwConfirm').prop('disabled', true).val('');
            $msg2.text("");
        } else {
            $msg1.text("사용가능한 비밀번호입니다.").removeClass("text-error").addClass("text-success");
            isPwValid = true;
            $('#userPwConfirm').prop('disabled', false); // 두 번째 칸 활성화
        }

        // 2. 두 번째 입력칸 검증
        if (pwConfirm !== "") {
            if (pw === pwConfirm) {
                $msg2.text("비밀번호가 일치합니다.").removeClass("text-error").addClass("text-success");
                isPwMatch = true;
            } else {
                $msg2.text("비밀번호가 일치하지 않습니다.").removeClass("text-success").addClass("text-error");
                isPwMatch = false;
            }
        } else {
            $msg2.text("");
            isPwMatch = false;
        }

        // 3. 둘 다 완벽할 때만 재설정 버튼 활성화
        if (isPwValid && isPwMatch) {
            $('#resetBtn').prop('disabled', false);
        } else {
            $('#resetBtn').prop('disabled', true);
        }
    });

    // 폼 제출 
    function submitResetPw() {
        if(isPwValid && isPwMatch) {
            $('#resetPwForm').submit();
        }
    }
    </script>
</body>
</html>