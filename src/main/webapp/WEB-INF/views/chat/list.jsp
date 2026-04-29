<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
      .unread-badge { color: #facc15; font-weight: bold; font-size: 11px; margin-bottom: 2px; line-height: 1; }
      
      /* 🌟 승인/거절 버튼 스타일 (HEAD) */
      .btn-group-request { display: flex; gap: 8px; justify-content: center; margin-top: 10px; border-top: 1px solid #cbd5e1; padding-top: 10px; }
      .btn-approve { background: #0ea5e9; color: white; padding: 6px 16px; border-radius: 8px; font-weight: bold; font-size: 12px; cursor: pointer; transition: background 0.2s; }
      .btn-approve:hover { background: #0284c7; }
      .btn-reject { background: #f43f5e; color: white; padding: 6px 16px; border-radius: 8px; font-weight: bold; font-size: 12px; cursor: pointer; transition: background 0.2s; }
      .btn-reject:hover { background: #e11d48; }

      /* 🌟 시스템 알림 스타일 (DEV) */
      .chat-system-row {
          position: relative;
          width: fit-content;
          display: flex;
          flex-direction: column;
          align-items: center;
          margin: 20px auto;
          max-width: 80%;
          clear: both;
      }
      .chat-system-msg {
          background: #f1f5f9;
          color: #475569;
          padding: 12px 20px;
          border-radius: 16px;
          font-size: 13px;
          line-height: 1.6;
          text-align: center;
          box-shadow: 0 2px 6px rgba(15, 23, 42, 0.05);
          border: 1px solid #e2e8f0;
      }
      .chat-system-msg a {
          display: inline-block;
          margin-top: 4px;
          color: #2563eb;
          font-weight: 700;
          text-decoration: underline;
      }
      
      /* 멤버 모달 스크롤바 스타일 */
      #memberModal div::-webkit-scrollbar { width: 6px; }
      #memberModal div::-webkit-scrollbar-thumb { background: #cbd5f5; border-radius: 9999px; }
   </style>
</head>
<body class="bg-slate-50 text-slate-800">
   <%@ include file="/WEB-INF/views/inc/header.jsp" %>

   <main class="page-wrap">
      <div class="content-card p-0 overflow-hidden">
         <div class="grid grid-cols-1 lg:grid-cols-[360px_minmax(0,1fr)] h-[86vh] min-h-[700px]">

            <%-- 사이드바: 채팅 목록 --%>
            <aside class="border-b lg:border-b-0 lg:border-r border-slate-200 bg-indigo-200 flex flex-col min-h-0">
                <div class="p-5 border-b border-slate-200 bg-white shrink-0">
                   <h2 class="text-2xl font-bold tracking-tight mb-4">채팅 목록</h2>
                   <p class="text-sm text-slate-500 mb-4">여행 동행자와 일정을 조율하고 대화를 나눠보세요.</p>
                   <div class="flex flex-wrap gap-2">
                      <a href="${pageContext.request.contextPath}/chat/list" class="px-4 py-2 rounded-full text-sm font-medium transition ${empty category ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">전체</a>
                      <a href="${pageContext.request.contextPath}/chat/list?category=0" class="px-4 py-2 rounded-full text-sm font-medium transition ${category == 0 ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">동행</a>
                      <a href="${pageContext.request.contextPath}/chat/list?category=1" class="px-4 py-2 rounded-full text-sm font-medium transition ${category == 1 ? 'bg-slate-900 text-white' : 'border border-slate-300 bg-white hover:bg-slate-100'}">개인</a>
                   </div>
                </div>

                <div class="chat-scroll flex-1 min-h-0 overflow-y-auto p-4 space-y-3">
                   <c:forEach items="${roomList}" var="room">
                      <c:url var="roomUrl" value="/chat/list">
                         <c:param name="roomId" value="${room.roomId}" />
                         <c:if test="${not empty category}">
                            <c:param name="category" value="${category}" />
                         </c:if>
                      </c:url>

                      <a href="${roomUrl}" class="block rounded-2xl p-4 shadow-sm transition ${room.active == 1 ? 'border-2 border-sky-500 bg-sky-50' : 'border border-slate-200 bg-white hover:bg-slate-50'}">
                         <div class="flex gap-3">
                            <div class="w-14 h-14 rounded-full bg-slate-200 overflow-hidden shrink-0">
                                <c:choose>
                                    <c:when test="${not empty room.partnerProfile and room.partnerProfile.startsWith('http')}">
                                        <img src="${room.partnerProfile}" class="w-full h-full object-cover">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/resources/upload/profile/${not empty room.partnerProfile ? room.partnerProfile : 'pic.png'}" class="w-full h-full object-cover">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <div class="min-w-0 flex-1">
                               <div class="flex items-start justify-between gap-2 mb-1">
                                  <div class="min-w-0">
                                     <div class="flex items-center gap-2 mb-1 flex-wrap">
                                        <h3 class="font-semibold text-slate-900 truncate">${room.roomName}</h3>
                                        <span class="inline-flex items-center rounded-full ${room.category == 0 ? 'bg-sky-100 text-sky-700' : 'bg-emerald-100 text-emerald-700'} text-[10px] font-bold px-2 py-0.5">
                                            ${room.category == 0 ? '동행' : '개인'}
                                        </span>
                                     </div>
                                     <p class="text-xs text-slate-500 truncate">
                                        <c:out value="${empty room.partnerNickname ? '참여자' : room.partnerNickname}" />
                                     </p>
                                  </div>
                                  <span class="text-[10px] text-slate-400 whitespace-nowrap">${room.roomTime}</span>
                               </div>
                               <p class="text-sm text-slate-600 truncate last-message-preview">
                                  <c:out value="${empty room.lastMessage ? '아직 메시지가 없습니다.' : room.lastMessage}" />
                               </p>
                            </div>
                         </div>
                      </a>
                   </c:forEach>
                </div>
            </aside>

            <%-- 메인 채팅창 영역 --%>
            <section class="flex flex-col min-w-0 min-h-0 bg-white">
               <c:choose>
                  <c:when test="${not empty selectedRoom}">
                     <div class="flex items-center justify-between gap-4 px-6 py-5 border-b border-slate-200 shrink-0">
                        <div class="min-w-0">
                           <div class="flex items-center gap-2 flex-wrap mb-1">
                              <h2 class="text-xl font-bold truncate">${selectedRoom.roomName}</h2>
                              <span class="inline-flex items-center rounded-full ${selectedRoom.category == 0 ? 'bg-sky-100 text-sky-700' : 'bg-emerald-100 text-emerald-700'} text-xs font-medium px-2 py-0.5">
                                  ${selectedRoom.category == 0 ? '동행' : '개인'}
                              </span>
                           </div>
                           <p class="text-sm text-slate-500">${selectedRoom.partnerNickname}</p>
                        </div>

                        <div class="flex items-center gap-2 shrink-0">
                           <%-- 멤버 버튼 (DEV) --%>
                           <button type="button" id="memberListBtn"
                                   class="px-4 py-2 rounded-xl border border-slate-300 bg-white text-slate-700 text-sm font-semibold hover:bg-slate-100 transition shadow-sm">
                               멤버
                           </button>
                           
                           <a href="${pageContext.request.contextPath}/chat/schedulePoll?roomId=${selectedRoomId}"
                              class="px-5 py-2 rounded-xl bg-sky-500 text-white text-sm font-bold hover:bg-sky-600 transition shadow-md">
                              일정/투표
                           </a>
                           
                           <button type="button" id="exitRoomBtn" class="px-4 py-2 rounded-xl border border-rose-200 bg-rose-50 text-rose-600 text-sm font-medium hover:bg-rose-100 transition shadow-sm">
                              나가기
                           </button>
                        </div>
                     </div>

                     <div id="chatScrollArea" class="chat-scroll flex-1 min-h-0 overflow-y-auto px-6 py-6 bg-indigo-100">
                        <div id="chatMessageList" class="space-y-5">
                            <c:forEach items="${messageList}" var="msg">
                                <c:choose>
                                    <%-- 시스템 메시지 처리 --%>
                                    <c:when test="${msg.seqMember == 0}">
                                        <div class="chat-system-row" id="sys-msg-${msg.seq}">
                                            <div class="chat-system-msg">
                                                <c:out value="${msg.detail}" escapeXml="false" />
                                                
                                                <c:if test="${myAuth == 0 && fn:contains(msg.detail, 'data-applicant-seq')}">
                                                    <div class="btn-group-request">
                                                        <button onclick="processJoinRequest(${selectedRoomId}, this, 1, ${msg.seq})" class="btn-approve">승인</button>
                                                        <button onclick="processJoinRequest(${selectedRoomId}, this, 0, ${msg.seq})" class="btn-reject">거절</button>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:when>

                                    <%-- 일반 메시지 처리 --%>
                                    <c:otherwise>
                                        <div class="flex ${msg.mine ? 'justify-end' : 'items-start gap-3'}">
                                            <c:if test="${!msg.mine}">
                                                <div class="w-10 h-10 rounded-full bg-slate-200 shrink-0 overflow-hidden">
                                                    <c:choose>
                                                        <c:when test="${not empty msg.partnerProfile and msg.partnerProfile.startsWith('http')}">
                                                            <img src="${msg.partnerProfile}" class="w-full h-full object-cover">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/resources/upload/profile/${not empty msg.partnerProfile ? msg.partnerProfile : 'pic.png'}" class="w-full h-full object-cover">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </c:if>

                                            <div class="max-w-[75%] ${msg.mine ? 'text-right' : ''}">
                                                <c:if test="${!msg.mine}">
                                                    <p class="text-xs text-slate-500 mb-1 ml-1">${msg.nickname}</p>
                                                </c:if>

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
                                                                <img src="${msg.savedName.startsWith('http') ? msg.savedName : pageContext.request.contextPath.concat('/upload/chat/').concat(msg.savedName)}" class="rounded-lg max-w-full h-auto mb-2 cursor-pointer" onclick="window.open(this.src)">
                                                            </c:if>
                                                            <c:out value="${msg.detail}" />
                                                        </div>
                                                    </c:if>

                                                    <c:if test="${!msg.mine}">
                                                        <div class="inline-block px-4 py-3 rounded-2xl rounded-tl-md bg-white border border-slate-200 text-slate-700 text-sm shadow-sm break-words text-left">
                                                            <c:if test="${not empty msg.savedName}">
                                                                <img src="${msg.savedName.startsWith('http') ? msg.savedName : pageContext.request.contextPath.concat('/upload/chat/').concat(msg.savedName)}" class="rounded-lg max-w-full h-auto mb-2 cursor-pointer" onclick="window.open(this.src)">
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
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                     </div>

                     <div class="border-t border-slate-200 p-4 bg-white relative shrink-0">
                        <form id="chatSendForm" class="flex items-end gap-3">
                           <input type="hidden" name="roomId" value="${selectedRoomId}">
                           <button type="button" id="fileAttachBtn" class="w-11 h-11 rounded-xl border border-slate-300 bg-white text-xl flex justify-center items-center hover:bg-slate-100">📎</button>
                           <input type="file" id="fileInput" class="hidden" accept="image/*" />
                           <div class="relative">
                               <button type="button" id="emojiToggleBtn" class="w-11 h-11 rounded-xl border border-slate-300 bg-white text-xl flex justify-center items-center hover:bg-slate-100">😀</button>
                               <div id="emojiPicker" class="hidden absolute bottom-full left-0 mb-2 w-64 p-3 bg-white border border-slate-200 rounded-xl shadow-lg grid grid-cols-6 gap-2 text-2xl z-50">
                                   <span class="cursor-pointer hover:scale-125 transition-transform" onclick="addEmoji('😀')">😀</span>
                                   <span class="cursor-pointer hover:scale-125 transition-transform" onclick="addEmoji('😂')">😂</span>
                                   <span class="cursor-pointer hover:scale-125 transition-transform" onclick="addEmoji('🥰')">🥰</span>
                                   <span class="cursor-pointer hover:scale-125 transition-transform" onclick="addEmoji('👍')">👍</span>
                                   <span class="cursor-pointer hover:scale-125 transition-transform" onclick="addEmoji('😭')">😭</span>
                                   <span class="cursor-pointer hover:scale-125 transition-transform" onclick="addEmoji('🙏')">🙏</span>
                               </div>
                           </div>
                           <div class="flex-1 relative">
                               <div id="filePreviewArea" class="hidden absolute bottom-full left-0 mb-2 p-2 bg-white border border-slate-200 rounded-lg shadow-md flex items-center gap-2">
                                   <span id="fileNameDisplay" class="text-xs font-medium text-slate-600 truncate max-w-[150px]"></span>
                                   <button type="button" id="fileCancelBtn" class="text-rose-500 font-bold px-1">X</button>
                               </div>
                               <textarea id="message" name="message" rows="1" placeholder="메시지를 입력하세요." class="w-full resize-none rounded-2xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-sky-400"></textarea>
                           </div>
                           <button type="submit" class="h-11 px-5 rounded-xl bg-sky-500 text-white text-sm font-semibold hover:bg-sky-600 transition shrink-0">전송</button>
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

      <%-- 멤버 목록 모달 (DEV) --%>
      <div id="memberModal" class="hidden fixed inset-0 z-50 bg-black/40 flex items-center justify-center">
          <div class="w-[380px] max-w-[90vw] rounded-2xl bg-white shadow-2xl overflow-hidden">
              <div class="flex items-center justify-between px-5 py-4 border-b border-slate-200">
                  <h3 class="text-lg font-bold text-slate-800">채팅방 멤버</h3>
                  <button type="button" id="memberModalClose" class="text-2xl leading-none text-slate-400 hover:text-slate-700">×</button>
              </div>
              <div class="p-5 space-y-3 max-h-[420px] overflow-y-auto">
                  <c:choose>
                      <c:when test="${not empty memberList}">
                          <c:forEach items="${memberList}" var="member">
                              <div class="flex items-center gap-3 rounded-xl border border-slate-100 bg-slate-50 px-4 py-3">
                                  <div class="w-11 h-11 rounded-full bg-slate-200 overflow-hidden shrink-0">
                                      <%-- 🌟 수정 포인트: 클라우디너리 URL 여부 확인 --%>
                                      <c:choose>
                                          <c:when test="${not empty member.profile and member.profile.startsWith('http')}">
                                              <img src="${member.profile}" class="w-full h-full object-cover">
                                          </c:when>
                                          <c:otherwise>
                                              <img src="${pageContext.request.contextPath}/resources/upload/profile/${not empty member.profile ? member.profile : 'pic.png'}" class="w-full h-full object-cover">
                                          </c:otherwise>
                                      </c:choose>
                                  </div>
                                  <div class="min-w-0">
                                      <p class="text-sm font-semibold text-slate-800 truncate">${member.nickname}</p>
                                  </div>
                              </div>
                          </c:forEach>
                      </c:when>
                      <c:otherwise>
                          <p class="text-sm text-slate-500 text-center py-6">멤버가 없습니다.</p>
                      </c:otherwise>
                  </c:choose>
              </div>
          </div>
      </div>
   </main>

   <script>
      const messageInput = document.getElementById('message');
      const chatForm = document.getElementById('chatSendForm');
      const chatMessageList = document.getElementById('chatMessageList');
      const fileInput = document.getElementById('fileInput');
      const filePreviewArea = document.getElementById('filePreviewArea');
      const fileNameDisplay = document.getElementById('fileNameDisplay');

      const selectedRoomId = '${selectedRoomId}';
      const loginUserId = '${loginUserId}';
      const myAuth = '${myAuth}';
      const contextPath = '${pageContext.request.contextPath}';
      const csrfHeader = '${_csrf.headerName}';
      const csrfToken = '${_csrf.token}';

      let socket = null;
      let selectedFile = null;

      document.addEventListener("DOMContentLoaded", () => {
          document.querySelectorAll('.last-message-preview').forEach(el => {
              let rawText = el.textContent.trim();
              if(rawText.includes('<') && rawText.includes('>')) {
                  rawText = rawText.replace(/<br\s*[\/]?>/gi, ' ');
                  let temp = document.createElement('div');
                  temp.innerHTML = rawText;
                  el.textContent = temp.textContent || temp.innerText || '아직 메시지가 없습니다.';
              }
          });
          scrollToBottom();
      });

      function escapeHtml(str) {
          return str ? str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;') : '';
      }

      function scrollToBottom() {
          const area = document.getElementById('chatScrollArea');
          if (area) area.scrollTop = area.scrollHeight;
      }

      window.addEmoji = (emoji) => {
          if (!messageInput) return;
          messageInput.value += emoji;
          document.getElementById('emojiPicker')?.classList.add('hidden');
          messageInput.focus();
      };

      document.getElementById('emojiToggleBtn')?.addEventListener('click', (e) => {
          e.stopPropagation();
          document.getElementById('emojiPicker')?.classList.toggle('hidden');
      });

      function appendMessage(data, isMine) {
          if (!chatMessageList) return;

          if (data.seqMember == 0) {
              let btnHtml = '';
              if (myAuth == '0' && data.message.includes('data-applicant-seq')) {
                  btnHtml = `
                  <div class="btn-group-request">
                      <button onclick="processJoinRequest(\${selectedRoomId}, this, 1, \${data.seq})" class="btn-approve">승인</button>
                      <button onclick="processJoinRequest(\${selectedRoomId}, this, 0, \${data.seq})" class="btn-reject">거절</button>
                  </div>`;
              }
              const sysHtml = `
              <div class="chat-system-row" id="sys-msg-\${data.seq}">
                  <div class="chat-system-msg">\${data.message}\${btnHtml}</div>
              </div>`;
              chatMessageList.insertAdjacentHTML('beforeend', sysHtml);
              scrollToBottom();
              return; 
          }

          let fileHtml = '';
          if (data.savedName) {
              const imgSrc = data.savedName.startsWith('http') ? data.savedName : `\${contextPath}/upload/chat/\${data.savedName}`;
              fileHtml = `<div class="mb-2"><img src="\${imgSrc}" class="rounded-lg max-w-full h-auto shadow-sm cursor-pointer" onclick="window.open(this.src)"></div>`;
          }

          const unreadBadge = (data.unreadCount > 0) ? `<span class="unread-badge unread-count-label">\${data.unreadCount}</span>` : '';
          const profileImgSrc = (data.partnerProfile && data.partnerProfile.startsWith('http')) ? data.partnerProfile : `\${contextPath}/resources/upload/profile/\${data.partnerProfile || 'pic.png'}`;

          let contentHtml = isMine ? `
            <div class="flex items-end justify-end gap-2">
                <div class="flex flex-col items-end min-w-fit">\${unreadBadge}<p class="text-[10px] text-slate-400">\${data.messageTime}</p></div>
                <div class="inline-block px-4 py-3 rounded-2xl rounded-tr-md bg-sky-500 text-white text-sm shadow-sm break-words text-left">\${fileHtml} \${escapeHtml(data.message)}</div>
            </div>` : `
            <div class="flex items-end justify-start gap-2">
                <div class="inline-block px-4 py-3 rounded-2xl rounded-tl-md bg-white border border-slate-200 text-slate-700 text-sm shadow-sm break-words text-left">\${fileHtml} \${escapeHtml(data.message)}</div>
                <div class="flex flex-col items-start min-w-fit">\${unreadBadge}<p class="text-[10px] text-slate-400">\${data.messageTime}</p></div>
            </div>`;

          const html = `<div class="flex \${isMine ? 'justify-end' : 'items-start gap-3'}">
                \${!isMine ? `<div class="w-10 h-10 rounded-full bg-slate-200 shrink-0 overflow-hidden"><img src="\${profileImgSrc}" class="w-full h-full object-cover"></div>` : ''}
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
         socket = new WebSocket(`\${protocol}//\${location.host}\${contextPath}/chatSocket`);
         socket.onopen = () => socket.send(JSON.stringify({ type: 'ENTER', roomId: Number(selectedRoomId), seqMember: Number(loginUserId) }));
         socket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            if (data.type === 'READ') {
                if (String(data.seqMember) !== String(loginUserId)) {
                    document.querySelectorAll('.unread-count-label').forEach(badge => {
                        let count = parseInt(badge.innerText);
                        if (count > 1) badge.innerText = count - 1; else badge.remove();
                    });
                }
            } else if (data.type === 'TALK') { appendMessage(data, String(data.seqMember) === String(loginUserId)); }
         };
      }

      // 모달 제어 (DEV)
      document.getElementById('memberListBtn')?.addEventListener('click', () => document.getElementById('memberModal')?.classList.remove('hidden'));
      document.getElementById('memberModalClose')?.addEventListener('click', () => document.getElementById('memberModal')?.classList.add('hidden'));
      document.getElementById('memberModal')?.addEventListener('click', function(e) { if (e.target === this) this.classList.add('hidden'); });

      document.getElementById('fileAttachBtn')?.addEventListener('click', () => fileInput.click());
      fileInput?.addEventListener('change', function() { if (this.files && this.files[0]) { selectedFile = this.files[0]; fileNameDisplay.textContent = selectedFile.name; filePreviewArea.classList.remove('hidden'); } });
      document.getElementById('fileCancelBtn')?.addEventListener('click', () => { selectedFile = null; fileInput.value = ''; filePreviewArea.classList.add('hidden'); });

      chatForm?.addEventListener('submit', async function (e) {
         e.preventDefault();
         const message = messageInput.value.trim();
         if (!message && !selectedFile) return;
         let seqFile = null;
         if (selectedFile) {
             const formData = new FormData(); formData.append("file", selectedFile); formData.append("roomId", selectedRoomId);
             const res = await fetch(`\${contextPath}/chat/uploadFile.do`, { method: 'POST', headers: { [csrfHeader]: csrfToken }, body: formData });
             const json = await res.json(); if (json.success) seqFile = json.seqFile;
         }
         socket.send(JSON.stringify({ type: 'TALK', roomId: Number(selectedRoomId), seqMember: Number(loginUserId), message: message, seqFile: seqFile }));
         messageInput.value = ''; fileInput.value = ''; selectedFile = null;
         if (filePreviewArea) filePreviewArea.classList.add('hidden');
      });
      
      document.getElementById('exitRoomBtn')?.addEventListener('click', function () {
          if (!selectedRoomId) return alert('선택된 채팅방이 없습니다.');
          if (!confirm('채팅방을 나가시겠습니까?')) return;
          fetch(contextPath + '/chat/exit', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded', [csrfHeader]: csrfToken }, body: 'roomId=' + encodeURIComponent(selectedRoomId) })
          .then(res => res.json()).then(data => {
              if (data.success) { if (socket) socket.close(); alert('채팅방에서 나갔습니다.'); location.href = contextPath + '/chat/list'; }
              else alert(data.message || '채팅방 나가기에 실패했습니다.');
          }).catch(err => alert('처리 중 오류가 발생했습니다.'));
      });

      window.processJoinRequest = function(roomId, btn, status, msgSeq) {
          const parentDiv = document.getElementById('sys-msg-' + msgSeq);
          const marker = parentDiv.querySelector('[data-applicant-seq]');
          if (!marker) return;
          const applicantSeq = marker.getAttribute('data-applicant-seq');
          const actionText = status === 1 ? '승인' : '거절';
          if (!confirm(`신청을 \${actionText}하시겠습니까?`)) return;
          fetch(`\${contextPath}/chat/processRequest.do`, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded', [csrfHeader]: csrfToken }, body: `roomId=\${roomId}&applicantSeq=\${applicantSeq}&status=\${status}&msgSeq=\${msgSeq}` })
          .then(res => res.json()).then(data => {
              if (data.success) { alert(data.msg); parentDiv.querySelector('.btn-group-request').innerHTML = `<p class="text-xs font-bold \${status === 1 ? 'text-sky-600' : 'text-rose-600'} mt-2">요청이 처리되었습니다.</p>`; }
              else alert(data.msg);
          }).catch(err => alert('처리 중 오류가 발생했습니다.'));
      };

      connectSocket();
   </script>
</body>
</html>