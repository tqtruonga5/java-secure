package com.kms.challenges.rbh.web.servlet;

import com.kms.challenges.rbh.util.RabbitHolesUtil;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author tkhuu.
 */
@WebServlet(name = "download-servlet", urlPatterns = "/download")
public class DownloadServlet extends HttpServlet {
    private static Logger LOGGER = LoggerFactory.getLogger(DownloadServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        // Download allow all user to download so let save sql select here
        String fileName = req.getParameter("fileName");
        String userId = req.getParameter("userId");
        File file = new File(RabbitHolesUtil.properties.get("upload.location") + userId + "/" + fileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            resp.setHeader("Content-disposition", "attachment; filename=" + fileName);
            IOUtils.copy(inputStream, resp.getOutputStream());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
