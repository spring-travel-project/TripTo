package com.tripto.dto;

import java.util.Date;

public class ChatRoomDTO {

    private int roomId;
    private String roomName;
    private int category;
    private String displayCategory;
    private String partnerNickname;
    private String lastMessage;
    private Date regdate;
    private String roomTime;
    private int active;
    private String partnerProfile;

    public String getPartnerProfile() {
		return partnerProfile;
	}

	public void setPartnerProfile(String partnerProfile) {
		this.partnerProfile = partnerProfile;
	}

	public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(String displayCategory) {
        this.displayCategory = displayCategory;
    }

    public String getPartnerNickname() {
        return partnerNickname;
    }

    public void setPartnerNickname(String partnerNickname) {
        this.partnerNickname = partnerNickname;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getRoomTime() {
        return roomTime;
    }

    public void setRoomTime(String roomTime) {
        this.roomTime = roomTime;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}