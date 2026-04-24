package com.tripto.dto;

import lombok.Data;

@Data
public class BoardCommentDTO {

    private int seqBoardComment;
    private int seqBoardPost;
    private int seqMember;
    private String content;
    private String createDate;
    private String updateDate;
    private String status;

    private String writerName;
}