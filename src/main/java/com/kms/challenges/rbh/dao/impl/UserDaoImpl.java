package com.kms.challenges.rbh.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kms.challenges.rbh.dao.ConnectionManager;
import com.kms.challenges.rbh.dao.UserDao;
import com.kms.challenges.rbh.model.User;

/**
 * @author tkhuu.
 */
public class UserDaoImpl extends AbstractMethodError implements UserDao {
    @Override
    public User getUser(long userId) throws SQLException {
        String query = "select * from user_accounts where id = ?";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);) {
            ps.setLong(1, userId);
            try (ResultSet resultSet = ps.executeQuery();) {
                if (resultSet.next()) {
                    return convertResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    @Override
    public User getUserByEmailAndPassword(String email, String password) throws SQLException {
        String query = "select * from user_accounts where email = ? and password = ?";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet resultSet = ps.executeQuery()) {
                User user = null;
                if (resultSet.next()) {
                    user = convertResultSetToUser(resultSet);
                }
                return user;
            }
        }
    }

    @Override
    public User getAdminUser() throws SQLException {
        String query = "select * from user_accounts where role = ?";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, User.ROLE.ADMIN.name());
            try (ResultSet resultSet = ps.executeQuery();) {
                User user = null;
                if (resultSet.next()) {
                    user = convertResultSetToUser(resultSet);
                }
                return user;
            }
        }
    }

    private User convertResultSetToUser(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String email = resultSet.getString("email");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        User.ROLE role = User.ROLE.valueOf(resultSet.getString("role"));
        User user = new User(id, email, firstName, lastName, null, role);
        return user;
    }

    @Override
    public void addUser(User user) throws SQLException {
        String query = "insert into user_accounts(email,first_name,last_name,password,role) values(?,?,?,?,?)";
        try (Connection connection = ConnectionManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());
            ps.executeUpdate();
        }
    }
}
