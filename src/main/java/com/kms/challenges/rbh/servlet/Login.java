package com.kms.challenges.rbh.servlet;

import com.kms.challenges.rbh.dao.RabbitHolesDao;
import com.kms.challenges.rbh.error.ValidationError;
import com.kms.challenges.rbh.model.LoginForm;
import com.kms.challenges.rbh.model.User;
import com.kms.challenges.rbh.util.RabbitHolesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tkhuu.
 */
@WebServlet(name = "login-servlet",urlPatterns = "/login")
public class Login extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class.getCanonicalName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.debug("Login page initialize");
        getServletContext().getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, ValidationError> errorMap = new HashMap<>();
        LoginForm form=null;
        try {
            form = RabbitHolesUtil.convertParameterMapToBean(LoginForm.class, req, errorMap);
        } catch (IllegalAccessException|InstantiationException e) {
            throw new ServletException(e);
        }
        req.setAttribute("validationErrors", errorMap);
        try {
            User user = RabbitHolesDao.getInstance().getUserByEmailAndPassword(form.getEmail(), form.getPassword());
            if (user != null) {
                req.getSession().setAttribute("user", user);
                //if the login user is our dear admin, set a cookie flag for him, more convenient for him when he
                // come back
                if (User.ROLE.ADMIN == user.getRole()) {
                    resp.addCookie(new Cookie("admin", "true"));
                }
                resp.sendRedirect("/index");
            } else {
                req.setAttribute("loginSuccess", false);
                getServletContext().getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

    }
}
