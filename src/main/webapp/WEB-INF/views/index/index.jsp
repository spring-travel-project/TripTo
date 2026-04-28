<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>TripTo - 완벽한 여행 동행 찾기</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
    <style>
	    .main-slide {
	        position: absolute;
	        inset: 0;
	        opacity: 0;
	        transform: scale(1.03);
	        transition: opacity 0.8s ease, transform 0.8s ease;
	        cursor: pointer;
	    }
	
	    .main-slide.active {
	        opacity: 1;
	        transform: scale(1);
	        z-index: 1;
	    }
	    .slide-arrow {
		    position: absolute;
		    top: 0;
		    width: 80px;
		    height: 100%;
		    display: flex;
		    align-items: center;
		    justify-content: center;
		    font-size: 48px;
		    color: rgba(255,255,255,0.7);
		    cursor: pointer;
		    z-index: 10;
		    transition: 0.3s;
		}
		
		.slide-arrow:hover {
		    background: rgba(0,0,0,0.35);
		    color: #fff;
		}
		
		.slide-arrow.left {
		    left: 0;
		    border-top-left-radius: 3.5rem;
		    border-bottom-left-radius: 3.5rem;
		}
		
		.slide-arrow.right {
		    right: 0;
		    border-top-right-radius: 3.5rem;
		    border-bottom-right-radius: 3.5rem;
		}
	</style>
</head>
<body class="bg-[#F8FAFC] text-slate-800">
    
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="max-w-7xl mx-auto px-8 py-20">
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-12 items-stretch h-[650px]">
            
            <div class="lg:col-span-4 flex flex-col gap-10">
                
                <div class="space-y-8">
                    <h1 class="text-6xl font-black text-slate-900 leading-[1.1] tracking-tighter">
                        나와 딱 맞는<br>
                        <span class="text-[#1D63FF]">여행 동행</span><br>
                        TripTo
                    </h1>
                    <p class="text-xl text-slate-400 font-medium leading-relaxed">
                        MBTI부터 걸음수까지,<br>
                        취향을 넘어 데이터로 검증된<br>
                        완벽한 메이트를 만나보세요.
                    </p>
                </div>

                <div class="bg-white rounded-[3rem] border border-slate-100 flex items-center justify-center p-12 shadow-[0_10px_40px_-15px_rgba(0,0,0,0.05)] overflow-hidden relative group">
                    <img src="${pageContext.request.contextPath}/resources/img/main_logo.png" 
                         onerror="this.src='https://cdn-icons-png.flaticon.com/512/2060/2060284.png'"
                         class="w-40 h-40 object-contain group-hover:scale-110 transition-transform duration-500">
                </div>
            </div>

            <div class="lg:col-span-8">

                <c:choose>
                    <c:when test="${not empty mainList}">
                        
                        <div class="relative w-full h-full rounded-[3.5rem] overflow-hidden shadow-2xl bg-slate-200">

						    <c:forEach items="${mainList}" var="post" varStatus="status" begin="0" end="3">
						        <div class="main-slide ${status.first ? 'active' : ''}"
						             onclick="location.href='${pageContext.request.contextPath}${post.detailUrl}';">
						
						            <img src="${post.thumbnail}"
						                 onerror="this.src='https://images.unsplash.com/photo-1499856871958-5b9627545d1a?q=80&w=2020&auto=format&fit=crop'"
						                 class="w-full h-full object-cover">
						
						            <div class="absolute bottom-0 left-0 p-14 w-full">
						                <h2 class="text-5xl font-black text-white drop-shadow-md leading-tight">
						                    ${post.title}
						                </h2>
						            </div>
						        </div>
						    </c:forEach>
							<div class="slide-arrow left" onclick="prevSlide()">&#10094;</div>
    						<div class="slide-arrow right" onclick="nextSlide()">&#10095;</div>
						</div>

                    </c:when>

                    <c:otherwise>
                        <div class="w-full h-full rounded-[3.5rem] bg-slate-200 flex items-center justify-center text-slate-400 font-bold">
                            추천 게시물을 등록해주세요.
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>

        </div>
    </main>
    
	<script>
	    const slides = document.querySelectorAll('.main-slide');
	    let currentSlide = 0;
	
	    if (slides.length > 1) {
	        setInterval(function () {
	            slides[currentSlide].classList.remove('active');
	            currentSlide = (currentSlide + 1) % slides.length;
	            slides[currentSlide].classList.add('active');
	        }, 3500);
	    }
	    
	    function showSlide(index) {
	        slides.forEach(s => s.classList.remove('active'));
	        slides[index].classList.add('active');
	    }

	    function nextSlide() {
	        currentSlide = (currentSlide + 1) % slides.length;
	        showSlide(currentSlide);
	    }

	    function prevSlide() {
	        currentSlide = (currentSlide - 1 + slides.length) % slides.length;
	        showSlide(currentSlide);
	    }

	    // 자동 슬라이드
	    if (slides.length > 1) {
	        setInterval(nextSlide, 3500);
	    }
	</script>
</body>
</html>