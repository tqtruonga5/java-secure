package com.kms.challenges.rbh.model;

import java.util.List;

/**
 * @author tkhuu.
 */
public class User {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private ROLE role;
    private List<UploadFile> userFiles;

    public User(Long id, String email, String firstName,String lastName, String password, ROLE role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }
    public static User getNewAnonymousUser() {
        return new User(0L, null, null, null, null, ROLE.ANNONYMOUS);
    }
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPassword() {
        return password;
    }

    public ROLE getRole() {
        return role;
    }

    public List<UploadFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(List<UploadFile> userFiles) {
        this.userFiles = userFiles;
    }

    public String getLastName() {
        return lastName;
    }

    public static enum ROLE{
        ADMIN, ANNONYMOUS,USER
    }
}
