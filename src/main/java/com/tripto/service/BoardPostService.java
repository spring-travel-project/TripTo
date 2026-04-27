package com.tripto.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.BoardPostDAO;
import com.tripto.dto.BoardCategoryDTO;
import com.tripto.dto.BoardPostDTO;
import com.tripto.dto.BoardPostFileDTO;

@Service
public class BoardPostService {

    @Autowired
    private BoardPostDAO dao;

    public List<BoardCategoryDTO> categoryList() {
        return dao.categoryList();
    }

    public int getTotalCount(BoardPostDTO dto) {
        return dao.getTotalCount(dto);
    }

    public List<BoardPostDTO> list(BoardPostDTO dto) {

        List<BoardPostDTO> list = dao.list(dto);

        for (BoardPostDTO post : list) {
            post.setThumbnailUrl(extractFirstImageSrc(post.getContent()));
        }

        return list;
    }

    private String extractFirstImageSrc(String content) {

        if (content == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private String getUploadPath(HttpServletRequest req) {
        return req.getServletContext().getRealPath("/resources/upload/board");
    }

    public int add(BoardPostDTO dto, HttpServletRequest req) {

        int result = dao.add(dto);

        if (result == 1) {
            try {
                String uploadPath = getUploadPath(req);

                List<String> srcList = extractImageSrcList(dto.getContent());

                for (String src : srcList) {
                    String savedName = extractSavedName(src);

                    if (savedName == null || savedName.isEmpty()) {
                        continue;
                    }

                    File file = new File(uploadPath, savedName);

                    if (!file.exists()) {
                        continue;
                    }

                    BoardPostFileDTO fileDto = new BoardPostFileDTO();
                    fileDto.setSeqBoardPost(dto.getSeqBoardPost());
                    fileDto.setOriginalName(savedName);
                    fileDto.setSavedName(savedName);
                    fileDto.setFilePath(file.getAbsolutePath());
                    fileDto.setFileType("image");
                    fileDto.setFileSize(file.length());

                    dao.addFile(fileDto);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public BoardPostDTO get(int seqBoardPost, boolean increaseViewCount) {

        if (increaseViewCount) {
            dao.increaseViewCount(seqBoardPost);
        }

        BoardPostDTO dto = dao.get(seqBoardPost);

        if (dto != null) {
            dto.setFileList(dao.fileList(seqBoardPost));
        }

        return dto;
    }

    public int edit(BoardPostDTO dto, HttpServletRequest req) {

        int result = dao.edit(dto);

        if (result == 1) {
            try {
                dao.deleteFiles(dto.getSeqBoardPost());

                String uploadPath = getUploadPath(req);
                List<String> srcList = extractImageSrcList(dto.getContent());

                for (String src : srcList) {
                    String savedName = extractSavedName(src);

                    if (savedName == null || savedName.isEmpty()) {
                        continue;
                    }

                    File file = new File(uploadPath, savedName);

                    if (!file.exists()) {
                        continue;
                    }

                    BoardPostFileDTO fileDto = new BoardPostFileDTO();
                    fileDto.setSeqBoardPost(dto.getSeqBoardPost());
                    fileDto.setOriginalName(savedName);
                    fileDto.setSavedName(savedName);
                    fileDto.setFilePath(file.getAbsolutePath());
                    fileDto.setFileType("image");
                    fileDto.setFileSize(file.length());

                    dao.addFile(fileDto);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public int delete(int seqBoardPost) {
        dao.deleteFiles(seqBoardPost);
        return dao.deletePost(seqBoardPost);
    }

    public boolean isWriter(int seqBoardPost, int seqMember) {
        Integer writerSeq = dao.getWriterSeq(seqBoardPost);
        return writerSeq != null && writerSeq == seqMember;
    }

    private List<String> extractImageSrcList(String content) {
        List<String> srcList = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return srcList;
        }

        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            srcList.add(matcher.group(1));
        }

        return srcList;
    }

    private String extractSavedName(String src) {
        if (src == null || src.trim().isEmpty()) {
            return null;
        }

        int index = src.lastIndexOf("/");
        if (index == -1 || index == src.length() - 1) {
            return null;
        }

        return src.substring(index + 1);
    }
}