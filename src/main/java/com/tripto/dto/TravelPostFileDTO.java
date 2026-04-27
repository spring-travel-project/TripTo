package com.tripto.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TravelPostFileDTO {

    private int seqTravelPostFile;
    private int seqTravelPost;
    private String originalName;
    private String savedName;
    private String filePath;
    private long fileSize;
    private String fileType;
    private String createDate;
}