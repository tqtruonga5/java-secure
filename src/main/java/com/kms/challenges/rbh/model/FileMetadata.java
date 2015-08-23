package com.kms.challenges.rbh.model;

/**
 * @author tkhuu.
 */
public class FileMetadata {
    private Long id;
    private String fileType;
    private Long fileSize;

    public FileMetadata(Long id, String fileType, Long fileSize) {
        this.id = id;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public Long getId() {
        return id;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }
}
