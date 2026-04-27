package com.tripto.dto;

public class PollContentDTO {

    private int seq;
    private int seqPoll;
    private String pollContent;
    private int voteCount;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeqPoll() {
        return seqPoll;
    }

    public void setSeqPoll(int seqPoll) {
        this.seqPoll = seqPoll;
    }

    public String getPollContent() {
        return pollContent;
    }

    public void setPollContent(String pollContent) {
        this.pollContent = pollContent;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}