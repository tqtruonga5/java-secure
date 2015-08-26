package com.kms.challenges.rbh.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tkhuu.
 */
@WebServlet(name = "logout-servlet",urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().invalidate();
        Cookie adminCookie = new Cookie("admin", "false");
        adminCookie.setMaxAge(0);
        resp.addCookie(adminCookie);
        resp.sendRedirect("/login");
    }
}
