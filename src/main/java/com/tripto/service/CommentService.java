package com.tripto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tripto.dao.CommentDAO;
import com.tripto.dto.BoardCommentDTO;

@Service
public class CommentService {

    @Autowired
    private CommentDAO dao;

    public List<BoardCommentDTO> list(int seqBoardPost) {
        return dao.list(seqBoardPost);
    }

    public int add(BoardCommentDTO dto) {
        return dao.add(dto);
    }

    public int edit(BoardCommentDTO dto, int currentSeqMember) {
        Integer writerSeq = dao.getWriterSeq(dto.getSeqBoardComment());

        if (writerSeq == null) {
            return 0;
        }

        if (writerSeq != currentSeqMember) {
            return 0;
        }

        return dao.edit(dto);
    }

    public int delete(int seqBoardComment, int currentSeqMember, boolean isAdmin) {
        Integer writerSeq = dao.getWriterSeq(seqBoardComment);

        if (writerSeq == null) {
            return 0;
        }

        if (!isAdmin && writerSeq != currentSeqMember) {
            return 0;
        }

        return dao.delete(seqBoardComment);
    }
}