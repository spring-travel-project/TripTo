<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
   <meta charset="UTF-8">
   <title>TripTo | 채팅</title>
   <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
   <style>
      .chat-scroll::-webkit-scrollbar {
         width: 8px;
      }
      .chat-scroll::-webkit-scrollbar-thumb {
         background-color: rgba(148, 163, 184, 0.7);
         border-radius: 9999px;
      }
      .chat-scroll::-webkit-scrollbar-track {
         background: transparent;
      }
   </style>
</head>
<body class="bg-slate-50 text-slate-800">
   <%@ include file="/WEB-INF/views/inc/header.jsp" %>

   <main class="page-wrap">

      <div class="mb-8">
         <h1 class="section-title">채팅</h1>
         <p class="section-desc">여행 동행자와 일정을 조율하고 대화를 나눠보세요.</p>
      </div>

      <div class="content-card p-0 overflow-hidden">
         <div class="grid grid-cols-1 lg:grid-cols-[360px_minmax(0,1fr)] min-h-[720px]">

            <aside class="border-b lg:border-b-0 lg:border-r border-slate-200 bg-indigo-200">
               <div class="p-5 border-b border-slate-200 bg-white">
                  <h2 class="text-2xl font-bold tracking-tight mb-4">채팅 목록</h2>

                  <div class="flex flex-wrap gap-2">
                     <a href="${pageContext.request.contextPath}/chat/list"
                        class="px-4 py-2 rounded-full text-sm font-medium transition
                        ${empty category ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">
                        전체
                     </a>

                     <a href="${pageContext.request.contextPath}/chat/list?category=0"
                        class="px-4 py-2 rounded-full text-sm font-medium transition
                        ${category == 0 ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">
                        동행
                     </a>

                     <a href="${pageContext.request.contextPath}/chat/list?category=1"
                        class="px-4 py-2 rounded-full text-sm font-medium transition
                        ${category == 1 ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">
                        매칭
                     </a>
                  </div>
               </div>

               <div class="chat-scroll h-[620px] lg:h-[720px] overflow-y-auto p-4 space-y-3">
                  <c:choose>
                     <c:when test="${not empty roomList}">
                        <c:forEach items="${roomList}" var="room">

                           <c:url var="roomUrl" value="/chat/list">
                              <c:param name="roomId" value="${room.roomId}" />
                              <c:if test="${not empty category}">
                                 <c:param name="category" value="${category}" />
                              </c:if>
                           </c:url>

                           <a href="${roomUrl}"
                              class="block rounded-2xl p-4 shadow-sm transition
                              ${room.active == 1
                                    ? 'border-2 border-sky-500 bg-sky-50'
                                    : 'border border-slate-200 bg-white hover:bg-slate-50'}">

                              <div class="flex gap-3">
                                 <div class="w-14 h-14 rounded-full bg-slate-200 overflow-hidden shrink-0 flex items-center justify-center text-xs text-slate-500">
                                    IMG
                                 </div>

                                 <div class="min-w-0 flex-1">
                                    <div class="flex items-start justify-between gap-2 mb-1">
                                       <div class="min-w-0">
                                          <div class="flex items-center gap-2 mb-1 flex-wrap">
                                             <h3 class="font-semibold text-slate-900 truncate">${room.roomName}</h3>

                                             <c:choose>
                                                <c:when test="${room.category == 0}">
                                                   <span class="inline-flex items-center rounded-full bg-sky-100 text-sky-700 text-xs font-medium px-2 py-0.5">
                                                      동행
                                                   </span>
                                                </c:when>
                                                <c:when test="${room.category == 1}">
                                                   <span class="inline-flex items-center rounded-full bg-emerald-100 text-emerald-700 text-xs font-medium px-2 py-0.5">
                                                      매칭
                                                   </span>
                                                </c:when>
                                             </c:choose>
                                          </div>

                                          <p class="text-sm text-slate-500 truncate">
                                             <c:out value="${empty room.partnerNickname ? '참여자' : room.partnerNickname}" />
                                          </p>
                                       </div>

                                       <span class="text-xs text-slate-400 whitespace-nowrap">
                                          <c:out value="${room.roomTime}" />
                                       </span>
                                    </div>

                                    <p class="text-sm text-slate-600 truncate">
                                       <c:out value="${empty room.lastMessage ? '아직 메시지가 없습니다.' : room.lastMessage}" />
                                    </p>
                                 </div>
                              </div>
                           </a>
                        </c:forEach>
                     </c:when>

                     <c:otherwise>
                        <div class="rounded-2xl border border-slate-200 bg-white p-8 text-center text-slate-500">
                           표시할 채팅방이 없습니다.
                        </div>
                     </c:otherwise>
                  </c:choose>
               </div>
            </aside>

            <section class="flex flex-col min-w-0 bg-white">

               <c:choose>
                  <c:when test="${not empty selectedRoom}">
                     <div class="flex items-center justify-between gap-4 px-6 py-5 border-b border-slate-200">
                        <div class="min-w-0">
                           <div class="flex items-center gap-2 flex-wrap mb-1">
                              <h2 class="text-xl font-bold truncate">${selectedRoom.roomName}</h2>

                              <c:choose>
                                 <c:when test="${selectedRoom.category == 0}">
                                    <span class="inline-flex items-center rounded-full bg-sky-100 text-sky-700 text-xs font-medium px-2 py-0.5">
                                       동행
                                    </span>
                                 </c:when>
                                 <c:when test="${selectedRoom.category == 1}">
                                    <span class="inline-flex items-center rounded-full bg-emerald-100 text-emerald-700 text-xs font-medium px-2 py-0.5">
                                       매칭
                                    </span>
                                 </c:when>
                              </c:choose>
                           </div>

                           <p class="text-sm text-slate-500">
                              <c:out value="${empty selectedRoom.partnerNickname ? '참여자 정보 없음' : selectedRoom.partnerNickname}" />
                           </p>
                        </div>

                        <div class="flex items-center gap-2 shrink-0">
                           <a href="${pageContext.request.contextPath}/chat/schedulePoll?roomId=${selectedRoomId}"
                              class="px-4 py-2 rounded-xl border border-slate-300 bg-white text-sm font-medium hover:bg-slate-100 transition">
                               일정/투표
                           </a>
                           <button type="button" id="exitRoomBtn"
                                   class="px-4 py-2 rounded-xl border border-rose-200 bg-rose-50 text-rose-600 text-sm font-medium hover:bg-rose-100 transition">
                               채팅방 나가기
                           </button>
                        </div>
                     </div>

                     <div class="chat-scroll flex-1 h-[500px] lg:h-[560px] overflow-y-auto px-6 py-6 bg-indigo-100">
                        <div id="chatMessageList" class="space-y-5">
                           <c:choose>
                              <c:when test="${not empty messageList}">
                                 <c:forEach items="${messageList}" var="msg">

                                    <c:choose>
                                       <c:when test="${msg.mine}">
                                          <div class="flex justify-end">
                                             <div class="max-w-[75%] text-right">
                                                <div class="inline-block px-4 py-3 rounded-2xl rounded-tr-md bg-sky-500 text-white text-sm shadow-sm break-words">
                                                   <c:out value="${msg.detail}" />
                                                </div>
                                                <p class="text-[11px] text-slate-400 mt-1 mr-1">
                                                   <c:out value="${msg.messageTime}" />
                                                </p>
                                             </div>
                                          </div>
                                       </c:when>

                                       <c:otherwise>
                                          <div class="flex items-start gap-3">
                                             <div class="w-10 h-10 rounded-full bg-slate-200 overflow-hidden shrink-0 flex items-center justify-center text-[10px] text-slate-500">
                                                IMG
                                             </div>

                                             <div class="max-w-[75%]">
                                                <p class="text-xs text-slate-500 mb-1 ml-1">
                                                   <c:out value="${msg.nickname}" />
                                                </p>
                                                <div class="inline-block px-4 py-3 rounded-2xl rounded-tl-md bg-white border border-slate-200 text-sm text-slate-700 shadow-sm break-words">
                                                   <c:out value="${msg.detail}" />
                                                </div>
                                                <p class="text-[11px] text-slate-400 mt-1 ml-1">
                                                   <c:out value="${msg.messageTime}" />
                                                </p>
                                             </div>
                                          </div>
                                       </c:otherwise>
                                    </c:choose>

                                 </c:forEach>
                              </c:when>

                              <c:otherwise>
                                 <div class="h-full flex items-center justify-center text-slate-400 text-sm">
                                    아직 대화가 없습니다. 첫 메시지를 보내보세요.
                                 </div>
                              </c:otherwise>
                           </c:choose>
                        </div>
                     </div>

                     <div class="border-t border-slate-200 p-4 bg-white">
                        <c:if test="${not empty selectedRoomId}">
                           <form id="chatSendForm" method="post" action="${pageContext.request.contextPath}/chat/send" class="flex items-end gap-3">
                              
                              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                              
                              <input type="hidden" name="roomId" value="${selectedRoomId}">

                              <c:if test="${not empty category}">
                                 <input type="hidden" name="category" value="${category}">
                              </c:if>

                              <button
                                 type="button"
                                 class="w-11 h-11 rounded-xl border border-slate-300 bg-white text-slate-500 text-xl font-semibold hover:bg-slate-100 transition shrink-0">
                                 +
                              </button>

                              <div class="flex-1">
                                 <label for="message" class="sr-only">채팅 입력</label>
                                 <textarea
                                    id="message"
                                    name="message"
                                    rows="1"
                                    placeholder="메시지를 입력하세요."
                                    class="w-full resize-none rounded-2xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400"></textarea>
                              </div>

                              <button
                                 type="submit"
                                 class="h-11 px-5 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition shrink-0">
                                 전송
                              </button>
                           </form>
                        </c:if>
                     </div>
                  </c:when>

                  <c:otherwise>
                     <div class="flex-1 flex items-center justify-center bg-slate-50">
                        <div class="text-center">
                           <h2 class="text-lg font-semibold text-slate-700 mb-2">선택된 채팅방이 없습니다.</h2>
                           <p class="text-sm text-slate-500">왼쪽 목록에서 채팅방을 선택해주세요.</p>
                        </div>
                     </div>
                  </c:otherwise>
               </c:choose>

            </section>
         </div>
      </div>

   </main>

   <script>
      const messageInput = document.getElementById('message');
      const chatForm = document.getElementById('chatSendForm');
      const chatMessageList = document.getElementById('chatMessageList');
   
      const selectedRoomId = '${selectedRoomId}';
      // 🌟 수정 2: 기존 자바 코드(model.addAttribute...)가 JS에 들어있던 오타 수정
      const loginUserId = '${loginUserId}'; 
      const contextPath = '${pageContext.request.contextPath}';
      
      // 🌟 수정 3: fetch AJAX 요청용 CSRF 변수 세팅
      const csrfHeader = '${_csrf.headerName}';
      const csrfToken = '${_csrf.token}';
      
      const exitBtn = document.getElementById('exitRoomBtn');
      
      let socket = null;
   
      function escapeHtml(str) {
         if (!str) return '';
         return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
      }
   
      function scrollToBottom() {
         const chatScrollArea = document.querySelector('.chat-scroll.flex-1');
         if (chatScrollArea) {
            chatScrollArea.scrollTop = chatScrollArea.scrollHeight;
         }
      }
   
      function appendMyMessage(message, time) {
         if (!chatMessageList) return;
   
         const html = `
            <div class="flex justify-end">
               <div class="max-w-[75%] text-right">
                  <div class="inline-block px-4 py-3 rounded-2xl rounded-tr-md bg-sky-500 text-white text-sm shadow-sm break-words">
                     \${escapeHtml(message)}
                  </div>
                  <p class="text-[11px] text-slate-400 mt-1 mr-1">
                     \${escapeHtml(time)}
                  </p>
               </div>
            </div>
         `;
   
         chatMessageList.insertAdjacentHTML('beforeend', html);
         scrollToBottom();
      }
   
      function appendOtherMessage(nickname, message, time) {
         if (!chatMessageList) return;
   
         const html = `
            <div class="flex items-start gap-3">
               <div class="w-10 h-10 rounded-full bg-slate-200 overflow-hidden shrink-0 flex items-center justify-center text-[10px] text-slate-500">
                  IMG
               </div>
   
               <div class="max-w-[75%]">
                  <p class="text-xs text-slate-500 mb-1 ml-1">
                     \${escapeHtml(nickname)}
                  </p>
                  <div class="inline-block px-4 py-3 rounded-2xl rounded-tl-md bg-white border border-slate-200 text-sm text-slate-700 shadow-sm break-words">
                     \${escapeHtml(message)}
                  </div>
                  <p class="text-[11px] text-slate-400 mt-1 ml-1">
                     \${escapeHtml(time)}
                  </p>
               </div>
            </div>
         `;
   
         chatMessageList.insertAdjacentHTML('beforeend', html);
         scrollToBottom();
      }
   
      function connectSocket() {
         if (!selectedRoomId) return;
   
         const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
         const socketUrl = protocol + '//' + location.host + contextPath + '/chatSocket';
   
         socket = new WebSocket(socketUrl);
   
         socket.onopen = function () {
            socket.send(JSON.stringify({
               type: 'ENTER',
               roomId: Number(selectedRoomId),
               seqMember: Number(loginUserId)
            }));
         };
   
         socket.onmessage = function (event) {
             const data = JSON.parse(event.data);

             // 🔥 채팅방 나가기 이벤트 처리
             if (data.type === 'EXIT') {
                 appendOtherMessage('시스템', data.message, '');
                 return;
             }

             // 🔥 일반 채팅 메시지
             if (String(data.seqMember) === String(loginUserId)) {
                 appendMyMessage(data.message, data.messageTime);
             } else {
                 appendOtherMessage(data.nickname, data.message, data.messageTime);
             }
         };
   
         socket.onclose = function () {
            console.log('WebSocket 연결 종료');
         };
   
         socket.onerror = function (e) {
            console.error('WebSocket 오류', e);
         };
      }
   
      if (messageInput) {
         messageInput.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
         });
   
         messageInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && !e.shiftKey) {
               e.preventDefault();
               if (chatForm) {
                  chatForm.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
               }
            }
         });
      }
   
      if (chatForm) {
         chatForm.addEventListener('submit', function (e) {
            e.preventDefault();
   
            const message = messageInput.value.trim();
   
            if (!message) {
               return;
            }
   
            if (!socket || socket.readyState !== WebSocket.OPEN) {
               alert('채팅 서버에 연결되지 않았습니다.');
               return;
            }
   
            socket.send(JSON.stringify({
               type: 'TALK',
               roomId: Number(selectedRoomId),
               seqMember: Number(loginUserId),
               message: message
            }));
   
            messageInput.value = '';
            messageInput.style.height = 'auto';
         });
      }
   
      connectSocket();
      scrollToBottom();
      
      if (exitBtn) {
          exitBtn.addEventListener('click', function () {

              if (!selectedRoomId) return;

              if (!confirm('정말 채팅방을 나가시겠습니까?')) {
                  return;
              }

              fetch(contextPath + '/chat/exit', {
                  method: 'POST',
                  headers: {
                      'Content-Type': 'application/x-www-form-urlencoded',
                      [csrfHeader]: csrfToken // 🌟 수정 4: fetch 헤더에 CSRF 토큰 세팅!
                  },
                  body: 'roomId=' + encodeURIComponent(selectedRoomId)
              })
              .then(res => res.json())
              .then(data => {

                  if (data.success) {

                      // 🔥 WebSocket 알림 보내기 (선택)
                      if (socket && socket.readyState === WebSocket.OPEN) {
                          socket.send(JSON.stringify({
                              type: 'EXIT',
                              roomId: Number(selectedRoomId),
                              seqMember: Number(loginUserId)
                          }));
                      }

                      alert('채팅방을 나갔습니다.');

                      // 목록으로 이동
                      window.location.href = contextPath + '/chat/list';

                  } else {
                      alert('채팅방 나가기 실패');
                  }
              })
              .catch(err => {
                  console.error(err);
                  alert('서버 오류');
              });
          });
      }
   </script>
</body>
</html>