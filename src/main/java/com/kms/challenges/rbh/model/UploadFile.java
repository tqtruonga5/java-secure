package com.kms.challenges.rbh.model;

/**
 * @author tkhuu.
 */
public class UploadFile {
    private Long id;
    private String fileName;
    private String uploadNote;
    private FileMetadata fileMetadata;
    private User uploader;
    public UploadFile(Long id, String fileName, String uploadNote, FileMetadata fileMetadata, User uploader) {
        this.id = id;
        this.fileName = fileName;
        this.uploadNote = uploadNote;
        this.fileMetadata = fileMetadata;
        this.uploader = uploader;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUploadNote() {
        return uploadNote;
    }

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public User getUploader() {
        return uploader;
    }
}
