package com.tripto.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BoardPostDTO {

    private int seqBoardPost;
    private int seqCategory;
    private int seqMember;

    private String title;
    private String content;
    private int viewCount;
    private String status;
    private String createDate;
    private String updateDate;

    // 조인용
    private String categoryName;
    private String writerName;
    private String writerNickname;

    // 첨부파일 대표값
    private String originalName;
    private String savedName;
    private String fileType;
    private long fileSize;

    // 검색/페이징
    private String searchWord;
    private String category;
    private int begin;
    private int end;
    private int page;

    // 상세 첨부목록
    private List<BoardPostFileDTO> fileList;
    private String filePath;
}