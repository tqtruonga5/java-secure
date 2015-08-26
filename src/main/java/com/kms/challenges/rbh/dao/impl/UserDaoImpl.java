package com.kms.challenges.rbh.dao.impl;

import com.kms.challenges.rbh.dao.ConnectionManager;
import com.kms.challenges.rbh.dao.UserDao;
import com.kms.challenges.rbh.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author tkhuu.
 */
public class UserDaoImpl extends AbstractMethodError implements UserDao {
    @Override
    public User getUser(long userId) throws SQLException {
        try (Statement select = ConnectionManager.getConnection().createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(String.format("select * from user_accounts where id=%s", userId))) {
                if (resultSet.next()) {
                    return convertResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    @Override
    public User getUserByEmailAndPassword(String email, String password) throws SQLException {
        try (Statement select = ConnectionManager.getConnection().createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(
                            String.format("select * from user_accounts where email='%s' and password='%s'", email,
                                    password))) {
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
        try (Statement select = ConnectionManager.getConnection().createStatement()) {
            try (ResultSet resultSet = select
                    .executeQuery(
                            String.format("select * from user_accounts where role='%s'", User.ROLE.ADMIN))) {
                User user = null;

                if (resultSet.next()) {
                    user = convertResultSetToUser(resultSet);
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

    @Override
    public void addUser(User user) throws SQLException {
        try (Statement insert = ConnectionManager.getConnection().createStatement()) {
            insert.execute(String.format(
                    "insert into user_accounts(email,first_name,last_name,password,role) values('%s','%s','%s','%s'," +
                            "'%s')",
                    user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword(), user.getRole()));
        }
    }
}
