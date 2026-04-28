package com.tripto.dto;

import com.tripto.dto.FileDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO {
    private int seq;
    private String originalName;
    private String savedName;
    private String filePath;
    private Long fileSize;
    private String fileType;
}
