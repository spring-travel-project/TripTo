<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-slate-50 text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>
    
    <main class="flex-grow flex items-center justify-center py-10 px-4">
        <div class="bg-white p-8 rounded-xl shadow-sm border border-slate-200 w-full max-w-md">
            <div class="mb-8 text-center">
                <h1 class="text-3xl font-bold text-primary mb-2">TripTo</h1>
                <p class="text-slate-500">TripTo에 오신 것을 환영합니다.</p>
            </div>

            <form action="${pageContext.request.contextPath}/login" method="POST" id="loginForm">
                
                <div class="form-control mb-4">
                    <label class="label"><span class="label-text font-bold">아이디</span></label>
                    <input type="text" name="username" placeholder="아이디를 입력하세요" class="input input-bordered w-full" required autofocus />
                </div>

                <div class="form-control mb-6">
                    <label class="label"><span class="label-text font-bold">비밀번호</span></label>
                    <input type="password" name="password" placeholder="비밀번호를 입력하세요" class="input input-bordered w-full" required />
                </div>

                <c:if test="${not empty param.error}">
                    <div class="text-error text-sm mb-4 text-center font-bold">
                        아이디 또는 비밀번호가 일치하지 않습니다.
                    </div>
                </c:if>

                <button type="submit" class="btn btn-primary w-full text-white text-lg mb-4">로그인</button>
                
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </form>

            <div class="text-center text-sm text-slate-500 mt-4">
                아직 회원이 아니신가요? <a href="${pageContext.request.contextPath}/member/join.do" class="text-primary font-bold hover:underline">회원가입</a>
            </div>
        </div>
    </main>
</body>
</html>