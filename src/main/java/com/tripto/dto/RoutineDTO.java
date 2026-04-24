package com.tripto.dto;

import java.util.Date;

public class RoutineDTO {

    private int seq;
    private int seqMember;
    private int seqChattingroom;
    private int seqTravelPost;
    private int seqLocation;
    private String title;
    private String detail;
    private Date regdate;
    private Date dDay;

    private String writerNickname;
    private String regdateText;
    private String dDayText;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeqMember() {
        return seqMember;
    }

    public void setSeqMember(int seqMember) {
        this.seqMember = seqMember;
    }

    public int getSeqChattingroom() {
        return seqChattingroom;
    }

    public void setSeqChattingroom(int seqChattingroom) {
        this.seqChattingroom = seqChattingroom;
    }

    public int getSeqTravelPost() {
        return seqTravelPost;
    }

    public void setSeqTravelPost(int seqTravelPost) {
        this.seqTravelPost = seqTravelPost;
    }

    public int getSeqLocation() {
        return seqLocation;
    }

    public void setSeqLocation(int seqLocation) {
        this.seqLocation = seqLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getdDay() {
        return dDay;
    }

    public void setdDay(Date dDay) {
        this.dDay = dDay;
    }

    public String getWriterNickname() {
        return writerNickname;
    }

    public void setWriterNickname(String writerNickname) {
        this.writerNickname = writerNickname;
    }

    public String getRegdateText() {
        return regdateText;
    }

    public void setRegdateText(String regdateText) {
        this.regdateText = regdateText;
    }

    public String getdDayText() {
        return dDayText;
    }

    public void setdDayText(String dDayText) {
        this.dDayText = dDayText;
    }
}