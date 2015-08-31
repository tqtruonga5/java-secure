package com.kms.challenges.rbh.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kms.challenges.rbh.util.RabbitHolesUtil;

/**
 * @author tkhuu.
 */
@WebServlet(name = "download-servlet", urlPatterns = "/download")
public class DownloadServlet extends HttpServlet {
    private static Logger LOGGER = LoggerFactory.getLogger(DownloadServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Download allow all user to download so let save sql select here
        String fileName = FileSystems.getDefault().getPath("/" + req.getParameter("fileName")).normalize().toString();
        String userId = req.getParameter("userId");
        String filePath = RabbitHolesUtil.properties.get("upload.location") + userId + fileName;
        File file = new File(filePath);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            resp.setHeader("Content-disposition", "attachment; filename=" + fileName);
            IOUtils.copy(inputStream, resp.getOutputStream());
        }

    }
}
