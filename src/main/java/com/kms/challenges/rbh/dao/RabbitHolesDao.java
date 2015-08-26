package com.kms.challenges.rbh.dao;

import com.kms.challenges.rbh.model.FileMetadata;
import com.kms.challenges.rbh.model.UploadFile;
import com.kms.challenges.rbh.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author tkhuu.
 */
public class RabbitHolesDao {
    private Connection conn;
    private static RabbitHolesDao INSTANCE;
    private RabbitHolesDao() {

    }

    private RabbitHolesDao(Connection conn) {
        this.conn = conn;
    }
    public static RabbitHolesDao getInstance() throws IOException, SQLException {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        return init();
    }

    private synchronized static RabbitHolesDao init() throws IOException, SQLException {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        // default to get configuratyion from ./config/database.properties file
        Properties properties = new Properties();
        properties.load(RabbitHolesDao.class.getResourceAsStream("/database.properties"));
        String connectionString = properties.getProperty("connectionString");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        INSTANCE = new RabbitHolesDao(DriverManager.getConnection(connectionString, username, password));
        INSTANCE.initDatabase();
        return INSTANCE;
    }

    protected void initDatabase() throws SQLException, IOException {
        for (String statement : getInitStatements()) {
            try(Statement statementSql = conn.createStatement()) {
                statementSql.execute(statement);
            }
        }
    }

    protected List<String> getInitStatements() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(RabbitHolesDao.class.getResourceAsStream("/init-database.sql")));
        String line = "";
        String statement = "";
        List<String> statementList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if(line.endsWith(";")) {
                statementList.add(statement+line);
                statement = "";
                continue;
            }
            if (!line.isEmpty()) {
                statement = statement + line;
            }
        }
        return statementList;
    }

    public User getUser(long userId) throws SQLException {
        try (Statement select = conn.createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(String.format("select * from user_accounts where id=%s", userId))) {
                if (resultSet.next()) {
                    return convertResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    public User getUserByEmailAndPassword(String email, String password) throws SQLException {
        try (Statement select = conn.createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(
                            String.format("select * from user_accounts where email='%s' and password='%s'", email, password))) {
                User user = null;

                if (resultSet.next()) {
                    user=convertResultSetToUser(resultSet);
                }
                return user;
            }
        }
    }
    public User getAdminUser() throws SQLException {
        try (Statement select = conn.createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(
                            String.format("select * from user_accounts where role='%s'", User.ROLE.ADMIN))) {
                User user = null;

                if (resultSet.next()) {
                    user=convertResultSetToUser(resultSet);
                }
                return user;
            }
        }
    }
    private User convertResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"), null,
                User.ROLE.valueOf(resultSet.getString("role")));
    }

    public void addUser(User user) throws SQLException {
        try (Statement insert = conn.createStatement()) {
            insert.execute(String.format(
                    "insert into user_accounts(email,first_name,last_name,password,role) values('%s','%s','%s','%s','%s')",
                    user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword(), user.getRole()));
        }
    }

    public List<UploadFile> getAllFiles() throws SQLException {
        List<UploadFile> files = new ArrayList<>();
        try (Statement select = conn.createStatement()) {
            try(ResultSet resultSet = select.executeQuery("select * from files")) {
                while (resultSet.next()) {
                    //get file metadata
                    long file_metadata_id = resultSet.getLong("file_metadata_id");
                    FileMetadata metadata = getFileMetadata(file_metadata_id);
                    //try to get the user
                    User user = null;
                    try (Statement selectUserFiles = conn
                            .createStatement(); ResultSet userFileResultSet = selectUserFiles.executeQuery(
                            String.format("select * from user_files where file_id=%s", resultSet.getLong("id")))) {
                        if (userFileResultSet.next()) {
                            user = getUser(userFileResultSet.getLong("user_id"));
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

    public UploadFile getFile(Long fileId) throws SQLException {
        try (Statement statement = conn.createStatement()) {
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

    public User getUploader(Long fileId) throws SQLException {
        try (Statement select = conn.createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(String.format("select * from user_files where file_id=%s", fileId))) {
                if (resultSet.next()) {
                    Long userId = resultSet.getLong("user_id");
                    return getUser(userId);
                }else {
                    throw new RuntimeException("Uploader not found");
                }
            }

        }
    }

    public void deleteFile(UploadFile file ) throws SQLException{
        try (Statement delete = conn.createStatement()) {
            delete.execute(String.format("delete from user_files where file_id=%s", file.getId()));
            delete.execute(String.format("delete from files where id=%s", file.getId()));
            delete.execute(String.format("delete from file_metadatas where id=%s", file.getFileMetadata().getId()));
        }
    }
    public FileMetadata getFileMetadata(long file_metadata_id) throws SQLException {
        FileMetadata metadata = null;
        try (Statement selectMetadata = conn.createStatement(); ResultSet metadataResultSet = selectMetadata
                .executeQuery(String.format("select * from file_metadatas where id=%s",
                        file_metadata_id))) {
            if (metadataResultSet.next()) {
                metadata = new FileMetadata(metadataResultSet.getLong("id"), metadataResultSet.getString("file_type"),
                        metadataResultSet.getLong("file_size"));
            }
        }
        return metadata;
    }

    public Long addFile(UploadFile uploadFile) throws SQLException {
        Long metadataId = insertMetadata(uploadFile.getFileMetadata());
        Long fileId = null;
        try (Statement insertFile = conn.createStatement()) {
            insertFile.execute(
                    String.format("insert into files(file_name,upload_note,file_metadata_id) values('%s','%s',%s)",
                            uploadFile.getFileName(), uploadFile.getUploadNote(), metadataId));
            ResultSet resultSet = insertFile.getGeneratedKeys();
            if (resultSet.next()) {
                fileId = resultSet.getLong(1);
            }
        }
        try (Statement insertFileUser = conn.createStatement()) {
            insertFileUser.execute(String.format("insert into user_files(user_id,file_id) values(%s,%s)",
                    uploadFile.getUploader().getId(), fileId));
        }
        return fileId;
    }

    public Long insertMetadata(FileMetadata metadata) throws SQLException {
        try (Statement insertMetadata = conn.createStatement()) {
            insertMetadata.execute(String.format("insert into file_metadatas(file_type,file_size) values('%s',%s)",
                    metadata.getFileType(), metadata.getFileSize()));
            ResultSet resultSet = insertMetadata.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        }
        return null;
    }

    public List<UploadFile> getFileByUserId(Long userId) throws SQLException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        try (Statement select = conn.createStatement()) {
            ResultSet resultSet = select
                    .executeQuery(String.format("select * from user_files where user_id=%s", userId));
            while (resultSet.next()) {
                Long fileId = resultSet.getLong("file_id");
                uploadFiles.add(getFile(fileId));
            }
        }
        return uploadFiles;
    }

    public List<UploadFile> searchByFileName(String fileName) throws SQLException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        try (Statement select = conn.createStatement()) {
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
