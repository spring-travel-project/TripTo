package com.tripto.dto;

import java.util.Date;

public class MemberDTO {

    private int seqMember;     // PK
    private String name;
    private String id;
    private String pw;
    private String pic;
    private String nickname;
    private String email;

    private int status;        // 0 Á¤»ó / 1 Àá±è / 2 Å»Åð
    private int type;          // 0 ÀÏ¹Ý / 1 °ü¸®ÀÚ
    private int failCount;

    private Date regDate;
    private int gender;        // 0 ³² / 1 ¿©
    private Date birth;
    private String region;
    private String intro;

    // =========================
    // Getter / Setter
    // =========================

    public int getSeqMember() {
        return seqMember;
    }

    public void setSeqMember(int seqMember) {
        this.seqMember = seqMember;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}