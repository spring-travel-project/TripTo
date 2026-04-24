package com.tripto.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.BoardCommentDTO;

@Repository
public class CommentDAO {

    @Autowired
    private SqlSessionTemplate template;

    public List<BoardCommentDTO> list(int seqBoardPost) {
        return template.selectList("boardComment.list", seqBoardPost);
    }

    public int add(BoardCommentDTO dto) {
        return template.insert("boardComment.add", dto);
    }

    public int edit(BoardCommentDTO dto) {
        return template.update("boardComment.edit", dto);
    }

    public int delete(int seqBoardComment) {
        return template.update("boardComment.delete", seqBoardComment);
    }

    public Integer getWriterSeq(int seqBoardComment) {
        return template.selectOne("boardComment.getWriterSeq", seqBoardComment);
    }
}