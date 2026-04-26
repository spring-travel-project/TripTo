package com.tripto.dto;

import lombok.Data;

@Data
public class ReportDTO {

    private int seqReport;

    // 신고한 회원
    private int seqMember;

    // 신고 대상 타입: USER / BOARD / TRAVEL
    private String targetType;

    // 신고 대상 번호
    private int seqTarget;

    private String createDate;
}