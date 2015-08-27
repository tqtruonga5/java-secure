package com.kms.challenges.rbh.dao.impl;

import com.kms.challenges.rbh.dao.AbstractRabbitHoleDao;
import com.kms.challenges.rbh.dao.ConnectionManager;
import com.kms.challenges.rbh.dao.FileDao;
import com.kms.challenges.rbh.dao.UserDao;
import com.kms.challenges.rbh.model.FileMetadata;
import com.kms.challenges.rbh.model.UploadFile;
import com.kms.challenges.rbh.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tkhuu.
 */
public class FileDaoImpl extends AbstractRabbitHoleDao implements FileDao {
    private UserDao userDao;

    public FileDaoImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<UploadFile> getAllFiles() throws SQLException {
        List<UploadFile> files = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection(); Statement select = connection
                .createStatement()) {
            try (ResultSet resultSet = select.executeQuery("select * from files")) {
                while (resultSet.next()) {
                    //get file metadata
                    long file_metadata_id = resultSet.getLong("file_metadata_id");
                    FileMetadata metadata = getFileMetadata(file_metadata_id);
                    //try to get the user
                    User user = null;
                    try (Statement selectUserFiles = connection
                            .createStatement(); ResultSet userFileResultSet = selectUserFiles.executeQuery(
                            String.format("select * from user_files where file_id=%s", resultSet.getLong("id")))) {
                        if (userFileResultSet.next()) {
                            user = userDao.getUser(userFileResultSet.getLong("user_id"));
                        }
                    }
                    UploadFile uploadFile = new UploadFile(resultSet.getLong("id"),
                            resultSet.getString("file_name"),
                            resultSet.getString("upload_note"), metadata, user);
                    files.add(uploadFile);
                }
            }
        }
        return files;

    }

    @Override
    public UploadFile getFile(Long fileId) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection(); Statement statement = connection
                .createStatement()) {
            try (ResultSet resultSet = statement
                    .executeQuery(String.format("select * from files where id=%s", fileId))) {
                if (resultSet.next()) {
                    UploadFile uploadFile = new UploadFile(fileId, resultSet.getString("file_name"),
                            resultSet.getString("upload_note"),
                            getFileMetadata(resultSet.getLong("file_metadata_id")), getUploader(fileId));
                    return uploadFile;
                } else {
                    throw new RuntimeException("File not found");
                }
            }

        }
    }

    @Override
    public User getUploader(Long fileId) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection(); Statement select = connection
                .createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(String.format("select * from user_files where file_id=%s", fileId))) {
                if (resultSet.next()) {
                    Long userId = resultSet.getLong("user_id");
                    return userDao.getUser(userId);
                } else {
                    throw new RuntimeException("Uploader not found");
                }
            }

        }
    }

    @Override
    public void deleteFile(UploadFile file) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection(); Statement delete = connection
                .createStatement()) {
            delete.execute(String.format("delete from user_files where file_id=%s", file.getId()));
            delete.execute(String.format("delete from files where id=%s", file.getId()));
            delete.execute(String.format("delete from file_metadatas where id=%s", file.getFileMetadata().getId()));
        }
    }

    @Override
    public FileMetadata getFileMetadata(long file_metadata_id) throws SQLException {
        FileMetadata metadata = null;
        try (Connection connection = ConnectionManager.getConnection(); Statement statementSql = connection
                .createStatement(); ResultSet metadataResultSet = statementSql
                .executeQuery(String.format("select * from file_metadatas where id=%s",
                        file_metadata_id))) {
            if (metadataResultSet.next()) {
                metadata = new FileMetadata(metadataResultSet.getLong("id"), metadataResultSet.getString("file_type"),
                        metadataResultSet.getLong("file_size"));
            }
        }
        return metadata;
    }

    @Override
    public Long addFile(UploadFile uploadFile) throws SQLException {
        Long metadataId = insertMetadata(uploadFile.getFileMetadata());
        Long fileId = null;
        try (Connection connection = ConnectionManager.getConnection(); Statement insertFile = connection
                .createStatement()) {
            insertFile.execute(
                    String.format("insert into files(file_name,upload_note,file_metadata_id) values('%s','%s',%s)",
                            uploadFile.getFileName(), uploadFile.getUploadNote(), metadataId));
            ResultSet resultSet = insertFile.getGeneratedKeys();
            if (resultSet.next()) {
                fileId = resultSet.getLong(1);
            }
        }
        try (Connection connection = ConnectionManager.getConnection(); Statement insertFileUser = connection
                .createStatement()) {
            insertFileUser.execute(String.format("insert into user_files(user_id,file_id) values(%s,%s)",
                    uploadFile.getUploader().getId(), fileId));
        }
        return fileId;
    }

    @Override
    public Long insertMetadata(FileMetadata metadata) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection(); Statement insertMetadata = connection
                .createStatement()) {
            insertMetadata.execute(String.format("insert into file_metadatas(file_type,file_size) values('%s',%s)",
                    metadata.getFileType(), metadata.getFileSize()));
            ResultSet resultSet = insertMetadata.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        }
        return null;
    }

    @Override
    public List<UploadFile> getFileByUserId(Long userId) throws SQLException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection(); Statement select = connection
                .createStatement()) {
            ResultSet resultSet = select
                    .executeQuery(String.format("select * from user_files where user_id=%s", userId));
            while (resultSet.next()) {
                Long fileId = resultSet.getLong("file_id");
                uploadFiles.add(getFile(fileId));
            }
        }
        return uploadFiles;
    }

    @Override
    public List<UploadFile> searchByFileName(String fileName) throws SQLException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection(); Statement select = connection
                .createStatement()) {
            ResultSet resultSet = select
                    .executeQuery(String.format("select * from files where file_name like '%%%s%%'", fileName));
            while (resultSet.next()) {
                Long fileId = resultSet.getLong("id");
                uploadFiles.add(getFile(fileId));
            }
        }
        return uploadFiles;
    }
}
