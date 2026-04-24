package com.tripto.dto;

import lombok.Data;

@Data
public class TravelCommentDTO {

    private int seqTravelComment;
    private int seqTravelPost;
    private int seqMember;

    private String content;
    private String createDate;
    private String updateDate;
    private String status;

    private String writerName;
}