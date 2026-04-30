package com.tripto.dto;

public class RoutineFileDTO {

    private int seq;
    private int seqRoutine;
    private String fileName;
    private String filePath;
    private String fileType;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeqRoutine() {
        return seqRoutine;
    }

    public void setSeqRoutine(int seqRoutine) {
        this.seqRoutine = seqRoutine;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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