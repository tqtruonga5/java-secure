package com.kms.challenges.rbh.servlet;

import com.kms.challenges.rbh.model.User;
import com.kms.challenges.rbh.util.RabbitHolesUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tkhuu.
 * Servlet for admin to enter command directly to deal with the upload folder
 */
@WebServlet(name="command-servlet",urlPatterns = "/admin/command")
public class AdminCommand extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<User.ROLE> requireRoles = new HashSet<>();
        requireRoles.add(User.ROLE.ADMIN);
        User user = (User) req.getSession().getAttribute("user");
        if (!RabbitHolesUtil.authenticate(user, requireRoles)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String command = req.getParameter("command");
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(RabbitHolesUtil.getUploadLocation()));
        builder.start();
    }
}
