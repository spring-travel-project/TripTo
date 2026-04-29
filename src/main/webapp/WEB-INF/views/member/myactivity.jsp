<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>내 활동 내역 - TripTo</title>
    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
</head>
<body class="bg-[#F8FAFC] text-slate-800 flex flex-col min-h-screen">
    <%@ include file="/WEB-INF/views/inc/header.jsp" %>

    <main class="flex-grow max-w-6xl w-full mx-auto px-4 py-10 flex gap-8 items-start">
        
        <aside class="w-64 shrink-0 sticky top-24">
            <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-4">
                <ul class="menu w-full gap-2 font-bold text-slate-600 text-base">
                    <li><a href="${pageContext.request.contextPath}/member/mypage.do" class="hover:bg-slate-50 transition-colors">내 정보 보기</a></li>
                    <li><a href="${pageContext.request.contextPath}/member/myactivity.do?tab=TRAVEL" class="${currentTab == 'TRAVEL' ? 'active bg-blue-50 text-blue-600' : 'hover:bg-slate-50 transition-colors'}">내가 작성한 동행 게시글</a></li>
                    <li><a href="${pageContext.request.contextPath}/member/myactivity.do?tab=BOARD" class="${currentTab == 'BOARD' ? 'active bg-blue-50 text-blue-600' : 'hover:bg-slate-50 transition-colors'}">내가 작성한 일반 게시글</a></li>
                    <li><a href="${pageContext.request.contextPath}/member/myactivity.do?tab=COMMENT" class="${currentTab == 'COMMENT' ? 'active bg-blue-50 text-blue-600' : 'hover:bg-slate-50 transition-colors'}">내가 작성한 댓글</a></li>
                </ul>
            </div>
        </aside>

        <section class="flex-1 bg-white rounded-[2.5rem] shadow-sm border border-slate-200 p-10">
            
                <div class="flex items-center gap-6 pb-8 mb-8 border-b border-slate-200">
                    <div class="w-20 h-20 rounded-full bg-white border border-slate-200 shadow-sm overflow-hidden shrink-0">
                        <img src="${pageContext.request.contextPath}/resources/upload/profile/${empty member.pic ? 'pic.png' : member.pic}" class="w-full h-full object-cover">
                    </div>
                    
                    <div class="flex flex-col">
                        <div class="flex items-baseline gap-2 mb-1.5">
                            <h2 class="text-2xl font-black text-slate-900">${member.nickname}</h2>
                            <span class="text-[15px] font-bold text-slate-400">(${member.name})</span>
                        </div>
                        <div class="text-[15px] font-medium text-slate-600 flex items-center gap-3">
                            <span>${ageGroup} · ${member.gender == 0 ? '남성' : '여성'} · ${member.region}</span>
                            <span class="w-1 h-1 rounded-full bg-slate-300"></span>
                            <span class="text-blue-500 font-bold">MBTI: ${empty profile.mbti ? '미입력' : profile.mbti}</span>
                        </div>
                    </div>
                </div>
    
                <div class="tabs tabs-boxed bg-slate-100 p-1 rounded-xl inline-flex font-bold mb-6">
                    <a class="tab filter-btn" data-filter="ALL">전체보기</a>
                    <a class="tab filter-btn" data-filter="TRAVEL">동행 게시글</a>
                    <a class="tab filter-btn" data-filter="BOARD">일반 게시글</a>
                    <a class="tab filter-btn" data-filter="COMMENT">댓글</a>
                </div>
    
                <div class="flex flex-col gap-4" id="activity-list">
                    <c:choose>
                        <c:when test="${empty myActivities}">
                            <div class="bg-white border border-slate-200 rounded-2xl p-12 text-center text-slate-400 font-medium">
                                아직 활동 내역이 없습니다.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${myActivities}" var="item">
                                <div class="activity-row bg-white border border-slate-200 rounded-2xl p-6 hover:shadow-md hover:border-blue-200 transition-all cursor-pointer group" 
                                     data-type="${item.type}" 
                                     onclick="location.href='${pageContext.request.contextPath}/' + ('${item.postType}' === 'TRAVEL' ? 'travel/detail.do?seqTravelPost=' : 'board/detail.do?seqBoardPost=') + '${item.seq}'">
                                     
                                    <div class="flex items-center justify-between mb-4">
                                        <c:choose>
                                            <c:when test="${item.type == 'TRAVEL'}"><span class="badge badge-lg bg-blue-50 text-blue-600 border-0 font-bold px-3 rounded-lg">🛫 동행 게시글</span></c:when>
                                            <c:when test="${item.type == 'BOARD'}"><span class="badge badge-lg bg-emerald-50 text-emerald-600 border-0 font-bold px-3 rounded-lg">📝 일반 게시글</span></c:when>
                                            <c:when test="${item.type == 'COMMENT'}"><span class="badge badge-lg bg-slate-100 text-slate-600 border-0 font-bold px-3 rounded-lg">💬 댓글</span></c:when>
                                        </c:choose>
                                        <span class="text-slate-400 text-sm font-medium tracking-wide">${fn:substring(item.regDate, 0, 16)}</span>
                                    </div>
    
                                    <div class="flex gap-6 items-start">
                                        <div class="flex-1 min-w-0">
                                            <c:if test="${not empty item.title}">
                                                <h3 class="text-[1.15rem] font-bold text-slate-800 mb-2 truncate group-hover:text-blue-600 transition-colors">${item.title}</h3>
                                            </c:if>
                                            <p class="text-slate-500 text-[15px] leading-relaxed line-clamp-2">
                                                ${item.content}
                                            </p>
                                        </div>
    
                                        <c:if test="${not empty item.thumbnailUrl}">
                                            <div class="w-24 h-24 shrink-0 rounded-xl overflow-hidden border border-slate-100 bg-slate-50">
                                                <img src="${item.thumbnailUrl}" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110">
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </div> 
                <div id="scroll-target" class="h-10 mt-4"></div>
            
            <div id="last-message" class="text-center py-10 text-slate-400 font-bold hidden">
                마지막 활동 내역입니다.
            </div>
        </section>

    </main>

    <script>
        const startTab = '${currentTab}' || 'ALL'; 
        let currentPage = 1;
        let isLoading = false;
        let isLastPage = false;

        // 1. 탭 활성화 디자인
        $('.filter-btn').removeClass('tab-active bg-white text-blue-600 shadow-sm');
        $('.filter-btn[data-filter="' + startTab + '"]').addClass('tab-active bg-white text-blue-600 shadow-sm');

        // 2. 탭(게시글 or 댓글) 클릭 시 화면 새로고침 (서버에 1페이지부터 다시 달라고 요청)
        $('.filter-btn').on('click', function() {
            const clickedTab = $(this).data('filter');
            if(startTab === clickedTab) return; // 동일한 탭이면 무시
            location.href = '${pageContext.request.contextPath}/member/myactivity.do?tab=' + clickedTab;
        });

        // 3. 스크롤 감지 (Intersection Observer)
        const observer = new IntersectionObserver((entries) => {
            // 센서가 화면에 보이고, 로딩중이 아니고, 마지막 페이지가 아니면
            // 백엔드에 loadMore(); 로 요청
            if (entries[0].isIntersecting && !isLoading && !isLastPage) {
                loadMore();
            }
        }, { threshold: 0.1 });

        // 센서 작동 시작
        observer.observe(document.getElementById('scroll-target'));

        // 4. 백엔드에서 JSON으로 글 내용 가져오기 (Ajax)
        function loadMore() {
            isLoading = true;
            currentPage++; // 다음 페이지 요청

            $.ajax({
                url: '${pageContext.request.contextPath}/member/api/myactivity/more',
                type: 'GET',
                data: { tab: startTab, page: currentPage },
                dataType: 'json',
                success: function(data) {
                    // 데이터가 더 없으면 마지막 문구 띄우고 센서 끄기
                    if (data.length === 0) {
                        isLastPage = true;
                        $('#last-message').removeClass('hidden'); 
                        observer.unobserve(document.getElementById('scroll-target'));
                        return;
                    }

                    // 가져온 JSON 데이터 개수만큼 글 or 댓글 카드 만들어서 붙이기
                    data.forEach(item => {
                        $('#activity-list').append(makeCardHTML(item));
                    });
                    
                    isLoading = false;
                },
                error: function() {
                    alert('데이터를 불러오는데 실패했습니다.');
                    isLoading = false;
                }
            });
        }

        // 5. JSON 데이터를 HTML 카드로 만들어주기
        function makeCardHTML(item) {
            let badge = '';
            if(item.type === 'TRAVEL') badge = '<span class="badge badge-lg bg-blue-50 text-blue-600 border-0 font-bold px-3 rounded-lg">🛫 동행 게시글</span>';
            else if(item.type === 'BOARD') badge = '<span class="badge badge-lg bg-emerald-50 text-emerald-600 border-0 font-bold px-3 rounded-lg">📝 일반 게시글</span>';
            else badge = '<span class="badge badge-lg bg-slate-100 text-slate-600 border-0 font-bold px-3 rounded-lg">💬 댓글</span>';

            let link = '${pageContext.request.contextPath}/' + (item.postType === 'TRAVEL' ? 'travel/detail.do?seqTravelPost=' : 'board/detail.do?seqBoardPost=') + item.seq;
            
            let thumb = '';
            if(item.thumbnailUrl) {
                thumb = `<div class="w-24 h-24 shrink-0 rounded-xl overflow-hidden border border-slate-100 bg-slate-50"><img src="\${item.thumbnailUrl}" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"></div>`;
            }

            let title = item.title ? `<h3 class="text-[1.15rem] font-bold text-slate-800 mb-2 truncate group-hover:text-blue-600 transition-colors">\${item.title}</h3>` : '';
            let date = item.regDate ? item.regDate.substring(0, 16) : '';
            let content = item.content || '';

            return `
                <div class="activity-row bg-white border border-slate-200 rounded-2xl p-6 hover:shadow-md hover:border-blue-200 transition-all cursor-pointer group" onclick="location.href='\${link}'">
                    <div class="flex items-center justify-between mb-4">
                        \${badge}
                        <span class="text-slate-400 text-sm font-medium tracking-wide">\${date}</span>
                    </div>
                    <div class="flex gap-6 items-start">
                        <div class="flex-1 min-w-0">
                            \${title}
                            <p class="text-slate-500 text-[15px] leading-relaxed line-clamp-2">\${content}</p>
                        </div>
                        \${thumb}
                    </div>
                </div>
            `;
        }
    </script>
</body>
</html>