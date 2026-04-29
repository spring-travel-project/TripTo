package com.tripto.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyActivityDTO {
    private String type;         // 구분 (TRAVEL, BOARD, COMMENT)
    private int seq;             // 글 번호 (링크 이동용)
    private String postType;     // 실제 게시판 종류 (TRAVEL, BOARD) - 클릭 이동용
    private String title;        // 제목 (댓글은 null)
    private String content;      // 본문 내용 또는 댓글 내용
    private String thumbnailUrl; // 썸네일 이미지 파일명 (없으면 null)
    private String regDate;      // 작성일
}