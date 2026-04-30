package com.tripto.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TravelPostDTO {

    private int seqTravelPost;
    private int seqMember;

    private String title;
    private String content;
    private int viewCount;
    private String status;
    private String createDate;
    private String updateDate;

    private String writerName;
    private String writerNickname;

    private String originalName;
    private String savedName;
    private String fileType;
    private long fileSize;

    private String searchWord;
    private String category;
    private int begin;
    private int end;
    private int page;

    private List<TravelPostFileDTO> fileList;
    private String filePath;
    
    private Integer seqLocation;

    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String mapProviderId;
    
    private String thumbnailUrl;
}