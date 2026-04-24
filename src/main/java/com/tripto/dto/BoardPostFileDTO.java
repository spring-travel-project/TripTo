package com.tripto.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BoardPostFileDTO {

    private int seqBoardPostFile;
    private int seqBoardPost;
    private String originalName;
    private String savedName;
    private String filePath;
    private long fileSize;
    private String fileType;
    private String createDate;
}