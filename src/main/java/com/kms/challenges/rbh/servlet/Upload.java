package com.kms.challenges.rbh.servlet;

import com.kms.challenges.rbh.dao.RabbitHolesDao;
import com.kms.challenges.rbh.error.ValidationError;
import com.kms.challenges.rbh.model.FileMetadata;
import com.kms.challenges.rbh.model.UploadFile;
import com.kms.challenges.rbh.model.User;
import com.kms.challenges.rbh.util.RabbitHolesUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author tkhuu.
 */
@WebServlet(name = "upload-servlet", urlPatterns = "/upload")
@MultipartConfig
public class Upload extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<User.ROLE> requireRoles = new HashSet<>();
        requireRoles.add(User.ROLE.ADMIN);
        requireRoles.add(User.ROLE.USER);
        User user = (User) req.getSession().getAttribute("user");
        if (!RabbitHolesUtil.authenticate(user, requireRoles)) {
            resp.sendRedirect("/login");
        }
        try {
            req.setAttribute("files", RabbitHolesDao.getInstance().getFileByUserId(user.getId()));
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uploadNote = req.getParameter("upload_note");
        Part uploadedFile = req.getPart("file");
        Map<String, ValidationError> errorMap = new HashMap<>();
        if (uploadedFile.getSize() == 0) {
            errorMap.put("file", new ValidationError("file", "Upload file is required"));
            req.setAttribute("validationErrors", errorMap);
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
            return;
        }
        User user = (User)
                req.getSession().getAttribute("user");
        File storeFolder = new File(RabbitHolesUtil.getUploadLocation() + user.getId() + "/");
        storeFolder.mkdirs();
        File storeFile = new File(RabbitHolesUtil.getUploadLocation() + user.getId()+"/"+ uploadedFile.getSubmittedFileName());
        try(FileOutputStream fileOutputStream=new FileOutputStream(storeFile)) {
            IOUtils.copy(uploadedFile.getInputStream(), fileOutputStream);
        }
        uploadedFile.getSize();
        UploadFile file = new UploadFile(null, uploadedFile.getSubmittedFileName(), uploadNote,
                new FileMetadata(null, uploadedFile.getContentType(), uploadedFile.getSize()), user);
        try {
            RabbitHolesDao.getInstance().addFile(file);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect("/index");
    }
}
