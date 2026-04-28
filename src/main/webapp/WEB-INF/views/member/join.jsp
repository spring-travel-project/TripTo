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
            <!-- 이미지 파일을 서버로 전송하기 위한 -->
            <!-- enctype="multipart/form-data" 태그 추가 -->
            <form action="${pageContext.request.contextPath}/member/join.do?${_csrf.parameterName}=${_csrf.token}" method="POST" id="joinForm" enctype="multipart/form-data">
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">이름</span></label>
                    <input type="text" name="name" placeholder="이름을 입력하세요" class="input input-bordered w-full" />
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">닉네임</span></label>
                    <div class="flex gap-2">
                        <input type="text" name="nickname" id="userNickname" placeholder="닉네임을 입력하세요" class="input input-bordered w-full" required />
                        <button type="button" class="btn btn-neutral shrink-0" onclick="checkNickname()">중복확인</button>
                    </div>
                    <label class="label"><span class="label-text-alt" id="nicknameCheckMsg">닉네임은 10자 이내로만 사용 가능합니다.</span></label>
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">아이디</span></label>
                    <div class="flex gap-2">
                        <input type="text" name="id" id="userId" placeholder="아이디를 입력하세요" class="input input-bordered w-full" />
                        <button type="button" class="btn btn-neutral" onclick="checkId()">중복확인</button>
                    </div>
                    <label class="label"><span class="label-text-alt text-error" id="idCheckMsg"></span></label>
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">비밀번호</span></label>
                    <input type="password" name="pw" id="userPw" placeholder="비밀번호를 입력하세요" class="input input-bordered w-full mb-2" />
                    <input type="password" name="pwConfirm" id="userPwConfirm" placeholder="비밀번호를 다시 입력하세요" class="input input-bordered w-full" />
                    <label class="label"><span class="label-text-alt text-error" id="pwCheckMsg"></span></label>
                </div>

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">이메일</span></label>
                    
                    <div class="flex gap-2 mb-2">
                        <input type="email" name="email" id="emailInput" placeholder="이메일을 입력하세요" class="input input-bordered flex-1" />
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

                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">거주 국가</span></label>
                    
                    <!-- 사용자에게 보이는 검색 input -->
                    <div class="relative" id="regionWrapper">
                        <input 
                            type="text" 
                            id="regionSearch" 
                            placeholder="국가명을 입력하세요 (예: Korea, Japan)" 
                            class="input input-bordered w-full" 
                            autocomplete="off"
                        />
                        <!-- 실제 폼에 전송되는 값 (선택 완료된 국가명 저장) -->
                        <input type="hidden" name="region" id="regionValue" />
                        
                        <!-- 자동완성 드롭다운 -->
                        <ul 
                            id="regionDropdown" 
                            class="absolute z-50 w-full bg-white border border-slate-200 rounded-lg shadow-lg mt-1 max-h-48 overflow-y-auto hidden"
                        ></ul>
                    </div>
                    <label class="label">
                        <span class="label-text-alt text-error hidden" id="regionCheckMsg">국가를 목록에서 선택해주세요.</span>
                    </label>
                </div>
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">생년월일</span></label>
                    <input type="date" name="birth" class="input input-bordered w-full" />
                </div>
                
                <div class="form-control mb-4">
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

                <div class="form-control mb-6">
                    <label class="label"><span class="label-text font-bold">프로필 사진</span></label>
                    <input type="file" name="picFile" class="file-input file-input-bordered w-full" accept="image/*" />
                    <label class="label">
                        <span class="label-text-alt text-slate-500">※ 10MB 이하의 이미지 파일만 업로드 가능합니다. (미첨부 시 기본 프로필 적용)</span>
                    </label>
                </div>
                
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                
                <button type="submit" class="btn btn-primary w-full text-white text-lg">회원가입</button>
            </form>
        </div>
    </main>
    
    <!-- 공통 모달(Modal) HTML 추가 -->
    <dialog id="customModal" class="modal">
        <div class="modal-box text-center">
            <h3 class="font-bold text-lg mb-2">알림</h3>
            <p class="py-4 text-slate-600" id="modalMessage">여기에 알림 메시지가 들어갑니다.</p>
            <div class="modal-action justify-center mt-2">
                <form method="dialog">
                    <button class="btn btn-neutral w-24">확인</button>
                </form>
            </div>
        </div>
    </dialog>
    
    <script>
    let timerInterval; // 타이머를 담을 변수
    
    // 공통 알림 모달 띄우기 함수
    function showAlert(msg) {
        $('#modalMessage').text(msg); // 모달 안의 글자 변경
        document.getElementById('customModal').showModal(); // 모달 창 열기
    }

    // 1. 이메일 인증번호 전송 (또는 재발송)
    function sendAuthCode() {
        const email = $('#emailInput').val();
        // 스프링 시큐리티의 CSRF 토큰 지참하기
        const csrfToken = $("input[name='_csrf']").val();
        
        if(!email) {
        	showAlert("이메일을 입력해주세요!");
            $('#emailInput').focus();
            return;
        }

        const $btn = $('#sendBtn');
        $btn.prop('disabled', true).text('전송 중...');

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/member/sendAuthEmail.do",
            // 데이터 보낼 때 스프링 시큐리티의 CSRF 토큰 지참하기
            data: { email: email, _csrf: csrfToken },
            success: function(response) {
                if(response === "SUCCESS") {
                	showAlert("인증번호가 발송되었습니다. 10분 안에 입력해주세요.");
                    $btn.text('인증번호 재발송'); // 버튼 텍스트 변경
                    $btn.prop('disabled', false);
                    
                    // 입력창 & 확인 버튼 활성화
                    $('#authCodeInput').prop('disabled', false).focus();
                    $('#verifyBtn').prop('disabled', false);
                    
                    startTimer(10 * 60); // 10분 타이머 시작
                } else if(response === "DUPLICATE") {
                	// 서버에서 중복 이메일이라고 응답이 온 경우
                    showAlert("이미 가입된 이메일입니다. 다른 이메일을 사용해주세요.");
                    $btn.prop('disabled', false).text('인증번호 전송'); // 버튼 원상복구
                    $('#emailInput').focus();
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
                showAlert("인증 시간이 만료되었습니다. 인증번호 재발송을 눌러주세요.");
            }
        }, 1000);
    }

    // 2. 인증번호 확인 기능 (AJAX)
    function verifyAuthCode() {
        const inputCode = $('#authCodeInput').val();
        const csrfToken = $("input[name='_csrf']").val();
        
        if(!inputCode) {
        	showAlert("인증번호를 입력해주세요!");
            return;
        }

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/member/verifyAuthCode.do",
            data: { inputCode: inputCode, _csrf: csrfToken },
            success: function(response) {
                if(response === "MATCH") {
                	showAlert("이메일 인증이 완료되었습니다!");
                    clearInterval(timerInterval); // 타이머 멈춤
                    $('#timer').text("인증완료").removeClass('text-error').addClass('text-success');
                 // 1. 버튼과 인증번호 입력칸은 아예 쓸모없어졌으니 
                 //disabled로 완전히 잠금 (서버 전송 안 됨)
                    $('#authCodeInput, #sendBtn, #verifyBtn').prop('disabled', true); 

                    // 2. 이메일은 DB에 저장해야 하므로 readonly로 변경
                    // 시각적으로 잠긴 것처럼 회색 배경 추가
                    $('#emailInput').prop('readonly', true).addClass('bg-slate-200');
                } else if(response === "EXPIRED") {
                	showAlert("인증 시간이 만료되었습니다. 재발송을 눌러주세요.");
                } else if(response === "MISMATCH") {
                	showAlert("인증번호가 일치하지 않습니다.");
                } else {
                	showAlert("인증 요청을 먼저 진행해주세요.");
                }
            },
            error: function() {
            	showAlert("서버 통신 오류가 발생했습니다.");
            }
        });
    }

    // 3. 아이디 중복확인 기능
    function checkId() { 
        const id = $('#userId').val();
        const $msg = $('#idCheckMsg');
        const csrfToken = $("input[name='_csrf']").val();
        
        if(!id) {
        	showAlert("아이디를 입력해주세요.");
            $('#userId').focus();
            return;
        }
        
        // 아이디 정규식 검사 (영문 대/소문자와 숫자만 허용, 4~15자리)
        const idRegex = /^[a-zA-Z0-9]{4,15}$/;
        if (!idRegex.test(id)) {
            $msg.text("아이디는 영문과 숫자 조합 4~15자리로 입력해주세요.").removeClass("text-success").addClass("text-error");
            $('#userId').focus();
            return; // 정규식 통과 못하면 여기서 멈춤 (AJAX 안 보냄)
        }

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/member/checkId.do",
            data: { id: id, _csrf: csrfToken },
            success: function(response) {
                if(response === "AVAILABLE") {
                    // 글자색을 초록색(text-success)으로 바꾸고 메시지 출력
                    $msg.text("사용 가능한 아이디입니다.").removeClass("text-error").addClass("text-success");
                } else if(response === "DUPLICATE") {
                    // 글자색을 빨간색(text-error)으로 바꾸고 메시지 출력
                    $msg.text("이미 사용 중인 아이디입니다.").removeClass("text-success").addClass("text-error");
                }
            },
            error: function() {
            	showAlert("서버 통신 오류가 발생했습니다.");
            }
        });
    }
    
    // 4. 닉네임 중복 확인
    $('#userNickname').on('input', function() {
        isNicknameChecked = false;
        $('#nicknameCheckMsg').text("닉네임 중복확인이 필요합니다.").removeClass("text-success").addClass("text-error");
    });

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

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/member/checkNickname.do",
            data: { nickname: nickname, _csrf: csrfToken },
            success: function(response) {
                if(response === "AVAILABLE") {
                    $msg.text("사용 가능한 닉네임입니다.").removeClass("text-error").addClass("text-success");
                    isNicknameChecked = true; // ★ 체크 완료
                } else if(response === "DUPLICATE") {
                    $msg.text("이미 사용 중인 닉네임입니다.").removeClass("text-success").addClass("text-error");
                    isNicknameChecked = false;
                }
            },
            error: function() {
                showAlert("서버 통신 오류가 발생했습니다.");
            }
        });
    }
    
    // 5. 비밀번호 실시간 유효성 및 일치 검사
    $('#userPw, #userPwConfirm').on('keyup', function() {
        const pw = $('#userPw').val();
        const pwConfirm = $('#userPwConfirm').val();
        const $msg = $('#pwCheckMsg');
        
        // ★ 비밀번호 정규식 (영문, 숫자, 특수문자 포함 8자리 이상)
        const pwRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_~]).{8,}$/;

        if (pw === "") {
            $msg.text(""); 
            return;
        }

        if (!pwRegex.test(pw)) {
            $msg.text("비밀번호는 영문, 숫자, 특수문자를 포함해 8자리 이상이어야 합니다.")
                .removeClass("text-success").addClass("text-error");
            return;
        }

        if (pwConfirm !== "") {
            if (pw === pwConfirm) {
                $msg.text("비밀번호가 일치합니다.").removeClass("text-error").addClass("text-success");
            } else {
                $msg.text("비밀번호가 일치하지 않습니다.").removeClass("text-success").addClass("text-error");
            }
        } else {
            $msg.text("안전한 비밀번호입니다. 한 번 더 입력해주세요.")
                .removeClass("text-error").addClass("text-success");
        }
    });
    
    // 6. 거주 국가 선택을 위한
    // 전세계 국가 목록 (한글 검색 지원을 위해 한글명도 병기)
    const countries = [
        { name: "대한민국", en: "South Korea" },
        { name: "미국", en: "United States" },
        { name: "일본", en: "Japan" },
        { name: "중국", en: "China" },
        { name: "영국", en: "United Kingdom" },
        { name: "프랑스", en: "France" },
        { name: "독일", en: "Germany" },
        { name: "이탈리아", en: "Italy" },
        { name: "스페인", en: "Spain" },
        { name: "캐나다", en: "Canada" },
        { name: "호주", en: "Australia" },
        { name: "브라질", en: "Brazil" },
        { name: "인도", en: "India" },
        { name: "러시아", en: "Russia" },
        { name: "멕시코", en: "Mexico" },
        { name: "인도네시아", en: "Indonesia" },
        { name: "태국", en: "Thailand" },
        { name: "베트남", en: "Vietnam" },
        { name: "필리핀", en: "Philippines" },
        { name: "말레이시아", en: "Malaysia" },
        { name: "싱가포르", en: "Singapore" },
        { name: "홍콩", en: "Hong Kong" },
        { name: "대만", en: "Taiwan" },
        { name: "아르헨티나", en: "Argentina" },
        { name: "칠레", en: "Chile" },
        { name: "남아프리카공화국", en: "South Africa" },
        { name: "이집트", en: "Egypt" },
        { name: "터키", en: "Turkey" },
        { name: "사우디아라비아", en: "Saudi Arabia" },
        { name: "아랍에미리트", en: "United Arab Emirates" },
        { name: "이스라엘", en: "Israel" },
        { name: "스웨덴", en: "Sweden" },
        { name: "노르웨이", en: "Norway" },
        { name: "덴마크", en: "Denmark" },
        { name: "핀란드", en: "Finland" },
        { name: "스위스", en: "Switzerland" },
        { name: "오스트리아", en: "Austria" },
        { name: "벨기에", en: "Belgium" },
        { name: "네덜란드", en: "Netherlands" },
        { name: "포르투갈", en: "Portugal" },
        { name: "그리스", en: "Greece" },
        { name: "폴란드", en: "Poland" },
        { name: "체코", en: "Czech Republic" },
        { name: "헝가리", en: "Hungary" },
        { name: "루마니아", en: "Romania" },
        { name: "우크라이나", en: "Ukraine" },
        { name: "뉴질랜드", en: "New Zealand" },
        { name: "파키스탄", en: "Pakistan" },
        { name: "방글라데시", en: "Bangladesh" },
        { name: "스리랑카", en: "Sri Lanka" },
        { name: "네팔", en: "Nepal" },
        { name: "미얀마", en: "Myanmar" },
        { name: "캄보디아", en: "Cambodia" },
        { name: "라오스", en: "Laos" },
        { name: "몽골", en: "Mongolia" },
        { name: "카자흐스탄", en: "Kazakhstan" },
        { name: "우즈베키스탄", en: "Uzbekistan" },
        { name: "이란", en: "Iran" },
        { name: "이라크", en: "Iraq" },
        { name: "쿠웨이트", en: "Kuwait" },
        { name: "카타르", en: "Qatar" },
        { name: "바레인", en: "Bahrain" },
        { name: "오만", en: "Oman" },
        { name: "요르단", en: "Jordan" },
        { name: "레바논", en: "Lebanon" },
        { name: "나이지리아", en: "Nigeria" },
        { name: "케냐", en: "Kenya" },
        { name: "에티오피아", en: "Ethiopia" },
        { name: "가나", en: "Ghana" },
        { name: "탄자니아", en: "Tanzania" },
        { name: "코트디부아르", en: "Ivory Coast" },
        { name: "카메룬", en: "Cameroon" },
        { name: "알제리", en: "Algeria" },
        { name: "모로코", en: "Morocco" },
        { name: "튀니지", en: "Tunisia" },
        { name: "리비아", en: "Libya" },
        { name: "콜롬비아", en: "Colombia" },
        { name: "페루", en: "Peru" },
        { name: "베네수엘라", en: "Venezuela" },
        { name: "에콰도르", en: "Ecuador" },
        { name: "볼리비아", en: "Bolivia" },
        { name: "파라과이", en: "Paraguay" },
        { name: "우루과이", en: "Uruguay" },
        { name: "쿠바", en: "Cuba" },
        { name: "코스타리카", en: "Costa Rica" },
        { name: "파나마", en: "Panama" },
        { name: "과테말라", en: "Guatemala" },
        { name: "온두라스", en: "Honduras" },
        { name: "엘살바도르", en: "El Salvador" },
        { name: "도미니카공화국", en: "Dominican Republic" },
        { name: "자메이카", en: "Jamaica" },
        { name: "크로아티아", en: "Croatia" },
        { name: "세르비아", en: "Serbia" },
        { name: "슬로바키아", en: "Slovakia" },
        { name: "슬로베니아", en: "Slovenia" },
        { name: "불가리아", en: "Bulgaria" },
        { name: "아이슬란드", en: "Iceland" },
        { name: "아일랜드", en: "Ireland" },
        { name: "룩셈부르크", en: "Luxembourg" },
        { name: "몰타", en: "Malta" },
        { name: "키프로스", en: "Cyprus" },
        { name: "에스토니아", en: "Estonia" },
        { name: "라트비아", en: "Latvia" },
        { name: "리투아니아", en: "Lithuania" },
        { name: "벨라루스", en: "Belarus" },
        { name: "몰도바", en: "Moldova" },
        { name: "알바니아", en: "Albania" },
        { name: "북마케도니아", en: "North Macedonia" },
        { name: "보스니아헤르체고비나", en: "Bosnia and Herzegovina" },
        { name: "몬테네그로", en: "Montenegro" },
        { name: "조지아", en: "Georgia" },
        { name: "아르메니아", en: "Armenia" },
        { name: "아제르바이잔", en: "Azerbaijan" },
        { name: "투르크메니스탄", en: "Turkmenistan" },
        { name: "타지키스탄", en: "Tajikistan" },
        { name: "키르기스스탄", en: "Kyrgyzstan" },
        { name: "파푸아뉴기니", en: "Papua New Guinea" },
        { name: "피지", en: "Fiji" },
        { name: "솔로몬제도", en: "Solomon Islands" }
        // 국가 더 추가 가능
    ];

    // 6-1. 거주 국가 자동완성 로직
    const $regionSearch = $('#regionSearch');
    const $regionValue  = $('#regionValue');
    const $dropdown     = $('#regionDropdown');

    $regionSearch.on('input', function() {
        const query = $(this).val().trim().toLowerCase();
        $regionValue.val(''); // 입력 중엔 hidden 값 초기화 (직접 타이핑한 값 방지)
        
        if (!query) {
            $dropdown.addClass('hidden').empty();
            return;
        }

        // 한글명 또는 영문명에 검색어가 포함되는 항목을 필터
        const filtered = countries.filter(c =>
            c.name.toLowerCase().includes(query) ||
            c.en.toLowerCase().includes(query)
        ).slice(0, 8); // 최대 8개만 표시

        if (filtered.length === 0) {
            $dropdown.addClass('hidden').empty();
            return;
        }
        
        // JSP EL태그와 충돌을 막기 위해 문자열 더하기(+) 방식을 사용
        const items = filtered.map(c =>
            '<li class="px-4 py-2 cursor-pointer hover:bg-slate-100 text-sm" ' + 
                 'data-value="' + c.name + '" ' + 
                 'data-display="' + c.name + ' (' + c.en + ')">' +
                 c.name + ' <span class="text-slate-400">' + c.en + '</span>' +
            '</li>'
        ).join('');

        $dropdown.html(items).removeClass('hidden');
    });

    // 드롭다운 항목 클릭 시 선택
    $dropdown.on('click', 'li', function() {
        const value   = $(this).data('value');
        const display = $(this).data('display');
        $regionSearch.val(display);     // input에는 "대한민국 (South Korea)" 표시
        $regionValue.val(value);         // hidden에는 "대한민국" 저장 → DB 전송
        $dropdown.addClass('hidden').empty();
        $('#regionCheckMsg').addClass('hidden');
    });

    // 드롭다운 외부 클릭 시 닫기
    $(document).on('click', function(e) {
        if (!$('#regionWrapper').is(e.target) && $('#regionWrapper').has(e.target).length === 0) {
            $dropdown.addClass('hidden');
        }
    });
    
    // 사용자가 목록에서 안 고르고 딴 데 클릭하면 엉뚱한 값 지우기
    $regionSearch.on('blur', function() {
        // hidden 값이 비어있다 = 목록에서 정상적으로 클릭하지 않았다
        if (!$regionValue.val()) {
            $(this).val(''); // 사용자 임의로 작성한 쓰레기값 초기화
        }
    });

    // 폼 제출 전 검증
    $('#joinForm').on('submit', function(e) {
    	// 닉네임 중복 확인
    	if (!isNicknameChecked) {
            e.preventDefault();
            showAlert("닉네임 중복확인을 완료해주세요.");
            $('#userNickname').focus();
            return false;
        }
        
    	// 아이디 중복 확인
        if (!isIdChecked) {
            e.preventDefault();
            showAlert("아이디 중복확인을 완료해주세요.");
            $('#userId').focus();
            return false;
        }
        
    	// 국가 선택 검증(직접 타이핑을 방지)
        if (!$regionValue.val()) {
            e.preventDefault();
            showAlert("거주 국가를 자동완성 목록에서 선택해주세요.");
            $regionSearch.val(''); // 엉뚱한 값 초기화
            $regionSearch.focus();
            return false;
        }
    });
    
    </script>
</body>
</html>