<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
   <meta charset="UTF-8">
   <title>TripTo | 채팅</title>
   <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
   <style>
      .chat-scroll::-webkit-scrollbar { width: 8px; }
      .chat-scroll::-webkit-scrollbar-thumb { background-color: rgba(148, 163, 184, 0.7); border-radius: 9999px; }
      .chat-scroll::-webkit-scrollbar-track { background: transparent; }
      /* 🌟 안 읽은 숫자 스타일 (노란색 배지) */
      .unread-badge { color: #facc15; font-weight: bold; font-size: 11px; margin-bottom: 2px; line-height: 1; }
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
                      <a href="${pageContext.request.contextPath}/chat/list" class="px-4 py-2 rounded-full text-sm font-medium transition ${empty category ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">전체</a>
                      <a href="${pageContext.request.contextPath}/chat/list?category=0" class="px-4 py-2 rounded-full text-sm font-medium transition ${category == 0 ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">동행</a>
                      <a href="${pageContext.request.contextPath}/chat/list?category=1" class="px-4 py-2 rounded-full text-sm font-medium transition ${category == 1 ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">매칭</a>
                   </div>
                </div>

                <div class="chat-scroll h-[620px] lg:h-[720px] overflow-y-auto p-4 space-y-3">
                   <c:forEach items="${roomList}" var="room">
                      <c:url var="roomUrl" value="/chat/list">
                         <c:param name="roomId" value="${room.roomId}" />
                         <c:if test="${not empty category}"><c:param name="category" value="${category}" /></c:if>
                      </c:url>
                      <a href="${roomUrl}" class="block rounded-2xl p-4 shadow-sm transition ${room.active == 1 ? 'border-2 border-sky-500 bg-sky-50' : 'border border-slate-200 bg-white hover:bg-slate-50'}">
                         <div class="flex gap-3">
                            <div class="w-14 h-14 rounded-full bg-slate-200 overflow-hidden shrink-0 flex items-center justify-center text-xs text-slate-500">IMG</div>
                            <div class="min-w-0 flex-1">
                               <div class="flex items-start justify-between gap-2 mb-1">
                                  <h3 class="font-semibold text-slate-900 truncate">${room.roomName}</h3>
                                  <span class="text-xs text-slate-400 whitespace-nowrap">${room.roomTime}</span>
                               </div>
                               <p class="text-sm text-slate-600 truncate">${empty room.lastMessage ? '아직 메시지가 없습니다.' : room.lastMessage}</p>
                            </div>
                         </div>
                      </a>
                   </c:forEach>
                </div>
            </aside>

            <section class="flex flex-col min-w-0 bg-white">
               <c:choose>
                  <c:when test="${not empty selectedRoom}">
                     <div class="flex items-center justify-between gap-4 px-6 py-5 border-b border-slate-200">
                        <div class="min-w-0">
                           <h2 class="text-xl font-bold truncate">${selectedRoom.roomName}</h2>
                           <p class="text-sm text-slate-500">${selectedRoom.partnerNickname}</p>
                        </div>
                        <div class="flex items-center gap-2">
                           <button type="button" id="exitRoomBtn" class="px-4 py-2 rounded-xl border border-rose-200 bg-rose-50 text-rose-600 text-sm font-medium">채팅방 나가기</button>
                        </div>
                     </div>

                     <div class="chat-scroll flex-1 h-[500px] lg:h-[560px] overflow-y-auto px-6 py-6 bg-indigo-100">
                        <div id="chatMessageList" class="space-y-5">
                            <c:forEach items="${messageList}" var="msg">
                                <div class="flex ${msg.mine ? 'justify-end' : 'items-start gap-3'}">
                                    <c:if test="${!msg.mine}"><div class="w-10 h-10 rounded-full bg-slate-200 shrink-0 flex items-center justify-center text-[10px] text-slate-500">IMG</div></c:if>
                                    <div class="max-w-[75%] ${msg.mine ? 'text-right' : ''}">
                                        <c:if test="${!msg.mine}"><p class="text-xs text-slate-500 mb-1 ml-1">${msg.nickname}</p></c:if>
                                        
                                        <div class="flex items-end ${msg.mine ? 'justify-end' : 'justify-start'} gap-2">
                                            <c:if test="${msg.mine}">
                                                <div class="flex flex-col items-end min-w-fit">
                                                    <c:if test="${msg.unreadCount > 0}">
                                                        <span class="unread-badge unread-count-label">${msg.unreadCount}</span>
                                                    </c:if>
                                                    <p class="text-[10px] text-slate-400">${msg.messageTime}</p>
                                                </div>
                                                <div class="inline-block px-4 py-3 rounded-2xl rounded-tr-md bg-sky-500 text-white text-sm shadow-sm break-words text-left">
                                                    <c:if test="${not empty msg.savedName}">
                                                        <img src="${pageContext.request.contextPath}/upload/chat/${msg.savedName}" class="rounded-lg max-w-full h-auto mb-2">
                                                    </c:if>
                                                    <c:out value="${msg.detail}" />
                                                </div>
                                            </c:if>

                                            <c:if test="${!msg.mine}">
                                                <div class="inline-block px-4 py-3 rounded-2xl rounded-tl-md bg-white border border-slate-200 text-slate-700 text-sm shadow-sm break-words text-left">
                                                    <c:if test="${not empty msg.savedName}">
                                                        <img src="${pageContext.request.contextPath}/upload/chat/${msg.savedName}" class="rounded-lg max-w-full h-auto mb-2">
                                                    </c:if>
                                                    <c:out value="${msg.detail}" />
                                                </div>
                                                <div class="flex flex-col items-start min-w-fit">
                                                    <c:if test="${msg.unreadCount > 0}">
                                                        <span class="unread-badge unread-count-label">${msg.unreadCount}</span>
                                                    </c:if>
                                                    <p class="text-[10px] text-slate-400">${msg.messageTime}</p>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                     </div>

                     <div class="border-t border-slate-200 p-4 bg-white relative">
                        <form id="chatSendForm" class="flex items-end gap-3">
                           <input type="hidden" name="roomId" value="${selectedRoomId}">
                           <button type="button" id="fileAttachBtn" class="w-11 h-11 rounded-xl border border-slate-300 text-xl flex justify-center items-center hover:bg-slate-100">📎</button>
                           <input type="file" id="fileInput" class="hidden" accept="image/*" />
                           <div class="flex-1">
                               <textarea id="message" name="message" rows="1" placeholder="메시지를 입력하세요." class="w-full resize-none rounded-2xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400"></textarea>
                           </div>
                           <button type="submit" class="h-11 px-5 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition">전송</button>
                        </form>
                     </div>
                  </c:when>
                  <c:otherwise>
                     <div class="flex-1 flex items-center justify-center bg-slate-50">
                        <div class="text-center">
                           <h2 class="text-lg font-semibold text-slate-700">채팅방을 선택해주세요.</h2>
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
      const fileInput = document.getElementById('fileInput');
   
      const selectedRoomId = '${selectedRoomId}';
      const loginUserId = '${loginUserId}'; 
      const contextPath = '${pageContext.request.contextPath}';
      const csrfHeader = '${_csrf.headerName}';
      const csrfToken = '${_csrf.token}';
      
      let socket = null;
      let selectedFile = null;
   
      function escapeHtml(str) {
         if (!str) return '';
         return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
      }
   
      function scrollToBottom() {
         const area = document.querySelector('.chat-scroll.flex-1');
         if (area) area.scrollTop = area.scrollHeight;
      }

      // 🌟 실시간 메시지 추가 함수
      function appendMessage(data, isMine) {
          if (!chatMessageList) return;
          
          let fileHtml = data.savedName ? `<img src="\${contextPath}/upload/chat/\${data.savedName}" class="rounded-lg max-w-full h-auto mb-2">` : '';
          const unreadBadge = (data.unreadCount > 0) ? `<span class="unread-badge unread-count-label">\${data.unreadCount}</span>` : '';

          let contentHtml = isMine ? `
            <div class="flex items-end justify-end gap-2">
                <div class="flex flex-col items-end min-w-fit">\${unreadBadge}<p class="text-[10px] text-slate-400">\${data.messageTime}</p></div>
                <div class="inline-block px-4 py-3 rounded-2xl rounded-tr-md bg-sky-500 text-white text-sm shadow-sm break-words text-left">
                    \${fileHtml} \${escapeHtml(data.message)}
                </div>
            </div>` : `
            <div class="flex items-end justify-start gap-2">
                <div class="inline-block px-4 py-3 rounded-2xl rounded-tl-md bg-white border border-slate-200 text-slate-700 text-sm shadow-sm break-words text-left">
                    \${fileHtml} \${escapeHtml(data.message)}
                </div>
                <div class="flex flex-col items-start min-w-fit">\${unreadBadge}<p class="text-[10px] text-slate-400">\${data.messageTime}</p></div>
            </div>`;

          const html = `
            <div class="flex \${isMine ? 'justify-end' : 'items-start gap-3'}">
                \${!isMine ? '<div class="w-10 h-10 rounded-full bg-slate-200 shrink-0 flex items-center justify-center text-[10px] text-slate-500">IMG</div>' : ''}
                <div class="max-w-[75%] \${isMine ? 'text-right' : ''}">
                    \${!isMine ? `<p class="text-xs text-slate-500 mb-1 ml-1">\${data.nickname}</p>` : ''}
                    \${contentHtml}
                </div>
            </div>`;

          chatMessageList.insertAdjacentHTML('beforeend', html);
          scrollToBottom();
      }
   
      function connectSocket() {
         if (!selectedRoomId) return;
         const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
         const socketUrl = `\${protocol}//\${location.host}\${contextPath}/chatSocket`;
         socket = new WebSocket(socketUrl);
   
         socket.onopen = () => {
            socket.send(JSON.stringify({ type: 'ENTER', roomId: Number(selectedRoomId), seqMember: Number(loginUserId) }));
         };
   
         socket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            
            // 🌟 실시간 읽음 신호(READ) 처리
            if (data.type === 'READ') {
                // 신호를 보낸 사람이 '나'가 아닐 때만 (상대방 중 누군가 읽었을 때만)
                if (String(data.seqMember) !== String(loginUserId)) {
                    // 화면에 있는 모든 '안 읽은 숫자' 라벨을 찾아 숫자를 1 줄이거나 제거
                    const badges = document.querySelectorAll('.unread-count-label');
                    badges.forEach(badge => {
                        let currentCount = parseInt(badge.innerText);
                        if (currentCount > 1) {
                            badge.innerText = currentCount - 1;
                        } else {
                            badge.remove();
                        }
                    });
                }
            } 
            else if (data.type === 'TALK') {
                appendMessage(data, String(data.seqMember) === String(loginUserId));
            }
         };
      }
   
      // (파일 첨부 및 전송 이벤트 리스너 생략 - 기존과 동일)
      document.getElementById('fileAttachBtn')?.addEventListener('click', () => fileInput.click());
      chatForm?.addEventListener('submit', async function (e) {
         e.preventDefault();
         const message = messageInput.value.trim();
         if (!message && !fileInput.files[0]) return;

         let seqFile = null;
         if (fileInput.files[0]) {
             const formData = new FormData();
             formData.append("file", fileInput.files[0]);
             formData.append("roomId", selectedRoomId);
             const res = await fetch(`\${contextPath}/chat/uploadFile.do`, {
                 method: 'POST',
                 headers: { [csrfHeader]: csrfToken },
                 body: formData
             });
             const json = await res.json();
             if (json.success) seqFile = json.seqFile;
         }

         socket.send(JSON.stringify({
            type: 'TALK', roomId: Number(selectedRoomId), seqMember: Number(loginUserId), message: message, seqFile: seqFile
         }));
         messageInput.value = '';
         fileInput.value = '';
      });

      connectSocket();
      scrollToBottom();
   </script>
</body>
</html>