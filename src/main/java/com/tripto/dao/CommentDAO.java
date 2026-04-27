package com.tripto.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tripto.dto.BoardCommentDTO;
import com.tripto.dto.TravelCommentDTO;

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
    
    public List<TravelCommentDTO> travelList(int seqTravelPost) {
        return template.selectList("boardComment.travelList", seqTravelPost);
    }

    public int travelAdd(TravelCommentDTO dto) {
        return template.insert("boardComment.travelAdd", dto);
    }

    public int travelEdit(TravelCommentDTO dto) {
        return template.update("boardComment.travelEdit", dto);
    }

    public int travelDelete(int seqTravelComment) {
        return template.update("boardComment.travelDelete", seqTravelComment);
    }

    public Integer getTravelWriterSeq(int seqTravelComment) {
        return template.selectOne("boardComment.getTravelWriterSeq", seqTravelComment);
    }
}