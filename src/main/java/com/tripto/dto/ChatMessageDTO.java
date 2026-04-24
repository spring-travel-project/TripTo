package com.tripto.dto;

import java.util.Date;

public class ChatMessageDTO {

    private int seq;
    private String detail;
    private Date regdate;
    private String messageTime;
    private String nickname;
    private int seqMember;
    private boolean mine;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSeqMember() {
        return seqMember;
    }

    public void setSeqMember(int seqMember) {
        this.seqMember = seqMember;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
    
    private Integer seqFile;
    private Integer seqChattingroom;

    public Integer getSeqFile() {
        return seqFile;
    }

    public void setSeqFile(Integer seqFile) {
        this.seqFile = seqFile;
    }

    public Integer getSeqChattingroom() {
        return seqChattingroom;
    }

    public void setSeqChattingroom(Integer seqChattingroom) {
        this.seqChattingroom = seqChattingroom;
    }
}