package com.kms.challenges.rbh.servlet;

import com.kms.challenges.rbh.customtags.AuthorizeTag;
import com.kms.challenges.rbh.dao.RabbitHolesDao;
import com.kms.challenges.rbh.model.User;
import com.kms.challenges.rbh.util.RabbitHolesUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tkhuu.
 */
@WebServlet(name = "index-servlet",urlPatterns = "/index")
public class Index extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<User.ROLE> requireRoles = new HashSet<>();
        requireRoles.add(User.ROLE.ADMIN);
        requireRoles.add(User.ROLE.USER);
        if (!RabbitHolesUtil.authenticate((User) req.getSession().getAttribute("user"), requireRoles)) {
            resp.sendRedirect("/login");
        }
        try {
            req.setAttribute("files", RabbitHolesDao.getInstance().getAllFiles());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
    }
}
