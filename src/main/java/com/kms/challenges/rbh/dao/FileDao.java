package com.kms.challenges.rbh.dao;

import com.kms.challenges.rbh.model.FileMetadata;
import com.kms.challenges.rbh.model.UploadFile;
import com.kms.challenges.rbh.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * @author tkhuu.
 */
public interface FileDao {
    List<UploadFile> getAllFiles() throws SQLException;

    UploadFile getFile(Long fileId) throws SQLException;

    User getUploader(Long fileId) throws SQLException;

    void deleteFile(UploadFile file) throws SQLException;

    FileMetadata getFileMetadata(long file_metadata_id) throws SQLException;

    Long addFile(UploadFile uploadFile) throws SQLException;

    Long insertMetadata(FileMetadata metadata) throws SQLException;

    List<UploadFile> getFileByUserId(Long userId) throws SQLException;

    List<UploadFile> searchByFileName(String fileName) throws SQLException;
}
