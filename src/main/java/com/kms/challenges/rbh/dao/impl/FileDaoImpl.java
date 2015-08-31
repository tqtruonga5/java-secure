package com.kms.challenges.rbh.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kms.challenges.rbh.dao.AbstractRabbitHoleDao;
import com.kms.challenges.rbh.dao.ConnectionManager;
import com.kms.challenges.rbh.dao.FileDao;
import com.kms.challenges.rbh.dao.UserDao;
import com.kms.challenges.rbh.model.FileMetadata;
import com.kms.challenges.rbh.model.UploadFile;
import com.kms.challenges.rbh.model.User;

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
        String fileQurey = "select * from files";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement select = connection.prepareStatement(fileQurey)) {
            try (ResultSet resultSet = select.executeQuery()) {
                while (resultSet.next()) {
                    // get file metadata
                    long file_metadata_id = resultSet.getLong("file_metadata_id");
                    FileMetadata metadata = getFileMetadata(file_metadata_id);
                    // try to get the user
                    User user = null;
                    String userQuery = "select * from user_files where file_id = ?";
                    try (PreparedStatement selectUserFiles = connection.prepareStatement(userQuery)) {
                        selectUserFiles.setLong(1, resultSet.getLong("id"));

                        try (ResultSet userFileResultSet = selectUserFiles.executeQuery()) {
                            if (userFileResultSet.next()) {
                                user = userDao.getUser(userFileResultSet.getLong("user_id"));
                            }
                        }
                        UploadFile uploadFile = new UploadFile(resultSet.getLong("id"),
                                resultSet.getString("file_name"), resultSet.getString("upload_note"), metadata, user);
                        files.add(uploadFile);
                    }
                }
            }
        }
        return files;

    }

    @Override
    public UploadFile getFile(Long fileId) throws SQLException {
        String query = "select * from files where id = ?";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, fileId);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    UploadFile uploadFile = new UploadFile(fileId, resultSet.getString("file_name"),
                            resultSet.getString("upload_note"), getFileMetadata(resultSet.getLong("file_metadata_id")),
                            getUploader(fileId));
                    return uploadFile;
                } else {
                    throw new RuntimeException("File not found");
                }
            }

        }
    }

    @Override
    public User getUploader(Long fileId) throws SQLException {
        String query = "select * from user_files where file_id = ?";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, fileId);
            try (ResultSet resultSet = ps.executeQuery()) {
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
        String deleteFileQuery = "delete from files where id = ?";
        String deleteFileMetadataQuery = "delete from file_metadatas where id = ?";
        String deleteFilesUsersQuery = "delete from user_files where file_id = ?";

        try (Connection connection = ConnectionManager.getConnection();) {
            try (PreparedStatement ps = connection.prepareStatement(deleteFilesUsersQuery)) {
                ps.setLong(1, file.getFileMetadata().getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(deleteFileQuery)) {
                ps.setLong(1, file.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(deleteFileMetadataQuery)) {

                ps.setLong(1, file.getId());
                ps.executeUpdate();
            }

            // delete.execute(String.format("delete from user_files where file_id=%s",
            // file.getId()));
            // delete.execute(String.format("delete from files where id=%s",
            // file.getId()));
            // delete.execute(String.format("delete from file_metadatas where id=%s",
            // file.getFileMetadata().getId()));
        }
    }

    @Override
    public FileMetadata getFileMetadata(long file_metadata_id) throws SQLException {
        FileMetadata metadata = null;
        String query = "select * from file_metadatas where id = ?";

        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement statementSql = connection.prepareStatement(query);) {

            statementSql.setLong(1, file_metadata_id);
            try (ResultSet metadataResultSet = statementSql.executeQuery()) {
                if (metadataResultSet.next()) {
                    metadata = new FileMetadata(metadataResultSet.getLong("id"),
                            metadataResultSet.getString("file_type"), metadataResultSet.getLong("file_size"));
                }
            }

        }
        return metadata;
    }

    @Override
    public Long addFile(UploadFile uploadFile) throws SQLException {
        Long metadataId = insertMetadata(uploadFile.getFileMetadata());
        Long fileId = null;
        String insertFileQuery = "insert into files(file_name,upload_note,file_metadata_id) values(?,?,?)";
        String insertFilesUsersQuery = "insert into user_files(user_id,file_id) values(?,?)";

        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement insertFile = connection.prepareStatement(insertFileQuery)) {
            insertFile.setString(1, uploadFile.getFileName());
            insertFile.setString(2, uploadFile.getUploadNote());
            insertFile.setLong(3, metadataId);
            insertFile.executeUpdate();
            ResultSet resultSet = insertFile.getGeneratedKeys();
            if (resultSet.next()) {
                fileId = resultSet.getLong(1);
            }
        }
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement insertFileUser = connection.prepareStatement(insertFilesUsersQuery)) {
            insertFileUser.setLong(1, uploadFile.getUploader().getId());
            insertFileUser.setLong(2, fileId);
            insertFileUser.executeUpdate();
        }
        return fileId;
    }

    @Override
    public Long insertMetadata(FileMetadata metadata) throws SQLException {
        String query = "insert into file_metadatas(file_type,file_size) values(?,?)";

        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement insertMetadata = connection.prepareStatement(query)) {
            insertMetadata.setString(1, metadata.getFileType());
            insertMetadata.setLong(2, metadata.getFileSize());
            insertMetadata.executeUpdate();
            try (ResultSet resultSet = insertMetadata.getGeneratedKeys();) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        }
        return null;
    }

    @Override
    public List<UploadFile> getFileByUserId(Long userId) throws SQLException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        String query = "select * from user_files where user_id = ?";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement select = connection.prepareStatement(query)) {
            select.setLong(1, userId);
            ResultSet resultSet = select.executeQuery();
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
        String query = "select * from files where file_name like ? ";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + fileName + "%");
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Long fileId = resultSet.getLong("id");
                    uploadFiles.add(getFile(fileId));
                }
            }
        }
        return uploadFiles;
    }
}
