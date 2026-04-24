package com.tripto.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.BoardCategoryDTO;
import com.tripto.dto.BoardPostDTO;
import com.tripto.dto.BoardPostFileDTO;

@Repository
public class BoardPostDAO {

    @Autowired
    private SqlSessionTemplate template;

    public List<BoardCategoryDTO> categoryList() {
        return template.selectList("boardPost.categoryList");
    }

    public int getTotalCount(BoardPostDTO dto) {
        return template.selectOne("boardPost.getTotalCount", dto);
    }

    public List<BoardPostDTO> list(BoardPostDTO dto) {
        return template.selectList("boardPost.list", dto);
    }

    public int add(BoardPostDTO dto) {
        return template.insert("boardPost.add", dto);
    }

    public int getCurrentSeq() {
        return template.selectOne("boardPost.getCurrentSeq");
    }

    public int addFile(BoardPostFileDTO dto) {
        return template.insert("boardPost.addFile", dto);
    }

    public BoardPostDTO get(int seqBoardPost) {
        return template.selectOne("boardPost.get", seqBoardPost);
    }

    public List<BoardPostFileDTO> fileList(int seqBoardPost) {
        return template.selectList("boardPost.fileList", seqBoardPost);
    }

    public int increaseViewCount(int seqBoardPost) {
        return template.update("boardPost.increaseViewCount", seqBoardPost);
    }

    public int edit(BoardPostDTO dto) {
        return template.update("boardPost.edit", dto);
    }

    public int deleteFiles(int seqBoardPost) {
        return template.delete("boardPost.deleteFiles", seqBoardPost);
    }

    public int deletePost(int seqBoardPost) {
        return template.update("boardPost.deletePost", seqBoardPost);
    }

    public Integer getWriterSeq(int seqBoardPost) {
        return template.selectOne("boardPost.getWriterSeq", seqBoardPost);
    }
}