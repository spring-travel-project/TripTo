<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 상세</title>

    <%@ include file="/WEB-INF/views/inc/asset.jsp" %>
    <link rel="stylesheet" href="${cp}/resources/css/board.css">
</head>
<body>

<%@ include file="/WEB-INF/views/inc/header.jsp" %>

<div class="board-page">
    <div class="board-detail-wrap">

        <!-- 게시글 카드 -->
        <article class="board-detail-card">

            <!-- 제목 -->
            <div class="board-detail-header">
                <div>
                    <span class="board-detail-category">${dto.categoryName}</span>
                    <h1 class="board-detail-title">${dto.title}</h1>

                    <div class="board-detail-meta">
                        <span>${dto.writerName}</span>
                        <span>${dto.createDate}</span>
                        <span>조회 ${dto.viewCount}</span>
                    </div>
                </div>
            </div>

            <!-- 내용 -->
            <div class="board-detail-content">
                <c:out value="${dto.content}" escapeXml="false" />
            </div>

            <!-- 댓글 영역 -->
			<section class="board-comment-box">
			    <div class="board-comment-title">댓글</div>
			
			    <!-- 댓글 등록 -->
			    <form method="post" action="${cp}/comment/add.do" class="board-comment-form">
				    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
				    <input type="hidden" name="seqBoardPost" value="${dto.seqBoardPost}">
				
				    <textarea name="content"
				              class="board-comment-textarea"
				              placeholder="댓글을 입력하세요."
				              required></textarea>
				
				    <div class="board-comment-form-actions">
				        <button type="submit" class="btn-board-outline btn-board-sm">
				            댓글 등록
				        </button>
				    </div>
				</form>
			
			    <!-- 댓글 목록 -->
			    <div class="board-comment-list">
			        <c:if test="${empty commentList}">
			            <div class="board-comment-empty">
			                아직 등록된 댓글이 없습니다.
			            </div>
			        </c:if>
			
			        <c:forEach items="${commentList}" var="comment">
			            <div class="board-comment-item">
			
			                <div class="board-comment-head">
							    <div class="board-comment-info">
							        <span class="board-comment-writer">${comment.writerName}</span>
							        <span class="board-comment-date">${comment.createDate}</span>
							    </div>
			
			                    <div class="board-comment-buttons">
			                        <!-- 수정: 댓글 작성자만 -->
			                        <c:if test="${comment.seqMember == currentSeqMember}">
			                            <button type="button"
			                                    class="board-comment-link"
			                                    onclick="toggleCommentEdit(${comment.seqBoardComment});">
			                                수정
			                            </button>
			                        </c:if>
			
			                        <!-- 삭제: 댓글 작성자 또는 관리자 -->
			                        <c:if test="${comment.seqMember == currentSeqMember || isAdmin}">
			                            <form method="post"
										    action="${cp}/comment/delete.do"
										    class="board-comment-delete-form"
										    onsubmit="return confirm('댓글을 삭제하시겠습니까?');">
										
										    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			                                <input type="hidden" name="seqBoardComment" value="${comment.seqBoardComment}">
			                                <input type="hidden" name="seqBoardPost" value="${dto.seqBoardPost}">
			                                <button type="submit" class="board-comment-link board-comment-delete">
			                                    삭제
			                                </button>
			                            </form>
			                        </c:if>
			                    </div>
			                </div>
			
			                <div id="comment-content-${comment.seqBoardComment}"
							     style="text-align:left; margin-top:10px; padding:0; width:100%; display:block;">
							    ${comment.content}
							</div>
			
			                <!-- 댓글 수정 폼 -->
			                <form method="post"
							      action="${cp}/comment/edit.do"
							      class="board-comment-edit-form"
							      id="comment-edit-${comment.seqBoardComment}"
							      style="display:none;">
							
							    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
							    <input type="hidden" name="seqBoardComment" value="${comment.seqBoardComment}">
							    <input type="hidden" name="seqBoardPost" value="${dto.seqBoardPost}">
							
							    <textarea name="content" class="board-comment-textarea" required>${comment.content}</textarea>
							
							    <div class="board-comment-form-actions">
							        <button type="button"
							                class="btn-board-outline btn-board-sm btn-board-outline-light"
							                onclick="toggleCommentEdit(${comment.seqBoardComment});">
							            취소
							        </button>
							
							        <button type="submit" class="btn-board-outline btn-board-sm">
							            저장
							        </button>
							    </div>
							</form>
			
			            </div>
			        </c:forEach>
			    </div>
			</section>

            <!-- 하단 버튼 -->
            <div class="board-detail-actions">
                <div>
                    <a href="${cp}/board/list.do" class="btn-board-outline btn-board-sm">
                        목록으로
                    </a>
                </div>

                <div class="board-detail-action-right">
                    <form method="post"
					      action="${cp}/report/add.do"
					      onsubmit="return confirm('이 게시글을 신고하시겠습니까?');">
					
					    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
					    <input type="hidden" name="targetType" value="BOARD">
					    <input type="hidden" name="seqTarget" value="${dto.seqBoardPost}">
					
					    <button type="submit" class="btn-board-outline btn-board-sm btn-board-report">
					        신고
					    </button>
					</form>

                    <c:if test="${isWriter}">
                        <a href="${cp}/board/edit.do?seqBoardPost=${dto.seqBoardPost}"
                           class="btn-board-outline btn-board-sm">
                            수정
                        </a>

                        <form method="post"
						      action="${cp}/board/delete.do"
						      class="board-delete-form"
						      onsubmit="return confirm('게시글을 삭제하시겠습니까?');">
						
						    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
						    <input type="hidden" name="seqBoardPost" value="${dto.seqBoardPost}">
						
						    <button type="submit" class="btn-board-outline btn-board-sm btn-board-danger">
						        삭제
						    </button>
						</form>
                    </c:if>
                </div>
            </div>

        </article>

    </div>
</div>

<c:if test="${not empty message}">
    <script>alert('${message}');</script>
</c:if>

<script>
    function toggleCommentEdit(seq) {
        const content = document.getElementById('comment-content-' + seq);
        const form = document.getElementById('comment-edit-' + seq);

        if (form.style.display === 'none') {
            form.style.display = 'block';
            content.style.display = 'none';
        } else {
            form.style.display = 'none';
            content.style.display = 'block';
        }
    }
    
    window.addEventListener('DOMContentLoaded', function () {
        const url = new URL(window.location.href);

        if (url.searchParams.has('searchWord') || url.searchParams.has('category')) {
            window.history.replaceState({}, '', url.pathname);
        }
    });
</script>

</body>
</html>