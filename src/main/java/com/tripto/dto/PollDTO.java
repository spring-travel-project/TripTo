package com.tripto.dto;

import java.util.Date;

public class PollDTO {

    private int seq;
    private int seqChattingroom;
    private String pollTitle;
    private Date pollEnddate;
    private String polldetail;

    private String writerNickname;
    private String enddateText;
    private int participantCount;
    private int seqMember;

    private String pollEnddateInput;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeqChattingroom() {
        return seqChattingroom;
    }

    public void setSeqChattingroom(int seqChattingroom) {
        this.seqChattingroom = seqChattingroom;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public Date getPollEnddate() {
        return pollEnddate;
    }

    public void setPollEnddate(Date pollEnddate) {
        this.pollEnddate = pollEnddate;
    }

    public String getPolldetail() {
        return polldetail;
    }

    public void setPolldetail(String polldetail) {
        this.polldetail = polldetail;
    }

    public String getWriterNickname() {
        return writerNickname;
    }

    public void setWriterNickname(String writerNickname) {
        this.writerNickname = writerNickname;
    }

    public String getEnddateText() {
        return enddateText;
    }

    public void setEnddateText(String enddateText) {
        this.enddateText = enddateText;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
    
    public int getSeqMember() {
        return seqMember;
    }

    public void setSeqMember(int seqMember) {
        this.seqMember = seqMember;
    }
    
    public String getPollEnddateInput() {
        return pollEnddateInput;
    }

    public void setPollEnddateInput(String pollEnddateInput) {
        this.pollEnddateInput = pollEnddateInput;
    }
    
}