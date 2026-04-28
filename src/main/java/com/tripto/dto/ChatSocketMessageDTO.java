package com.tripto.dto;

public class ChatSocketMessageDTO {

    private String type;          // ENTER, TALK
    private Integer roomId;
    private Integer seqMember;
    private String nickname;
    private String message;
    private String messageTime;
    
    private int unreadCount;
    

    public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}
    
    private Integer seqFile;
    private String savedName;

    public Integer getSeqFile() {
		return seqFile;
	}

	public void setSeqFile(Integer seqFile) {
		this.seqFile = seqFile;
	}

	public String getSavedName() {
		return savedName;
	}

	public void setSavedName(String savedName) {
		this.savedName = savedName;
	}

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getSeqMember() {
        return seqMember;
    }

    public void setSeqMember(Integer seqMember) {
        this.seqMember = seqMember;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}