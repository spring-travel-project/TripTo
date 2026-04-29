package com.tripto.dto;

import java.util.Date;

public class RoutineDTO {

    private int seq;
    private int seqMember;
    private int seqChattingroom;
    private Integer seqTravelPost;
    private int seqLocation;
    private String title;
    private String detail;
    private Date regdate;
    private Date dDay;

    private String writerNickname;
    private String regdateText;
    private String dDayText;
    
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String mapProviderId;
    private int status;
    private String dDayDateText;
    private String dDayInput;
    
    private String filePath;   // 파일 경로
    private String fileType;

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

    public Integer getSeqTravelPost() {
        return seqTravelPost;
    }

    public void setSeqTravelPost(Integer seqTravelPost) {
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
    
    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMapProviderId() {
        return mapProviderId;
    }

    public void setMapProviderId(String mapProviderId) {
        this.mapProviderId = mapProviderId;
    }
    

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getdDayDateText() {
        return dDayDateText;
    }

    public void setdDayDateText(String dDayDateText) {
        this.dDayDateText = dDayDateText;
    }
    
    public String getdDayInput() {
        return dDayInput;
    }

    public void setdDayInput(String dDayInput) {
        this.dDayInput = dDayInput;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}