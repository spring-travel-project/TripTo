package com.tripto.dto;

import lombok.Data;

@Data
public class MainRecommendDTO {

    private int seqMainRecommend;
    private String targetType;
    private int seqTarget;
    private String activeYn;
    private String createdDate;

    private String title;
    private String content;
    private String thumbnail;
    private String detailUrl;
}