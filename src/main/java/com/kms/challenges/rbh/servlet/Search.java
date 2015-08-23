package com.kms.challenges.rbh.servlet;

import com.kms.challenges.rbh.dao.RabbitHolesDao;
import com.kms.challenges.rbh.model.UploadFile;
import com.kms.challenges.rbh.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author tkhuu.
 */
@WebServlet(name = "search-servlet",urlPatterns = "/search")
public class Search extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<User.ROLE> reqRoles = new HashSet<>();
        reqRoles.add(User.ROLE.USER);
        reqRoles.add(User.ROLE.ADMIN);
        String searchText = req.getParameter("searchText");
        try {
            List<UploadFile> fileList = RabbitHolesDao.getInstance().searchByFileName(searchText);
            req.setAttribute("files", fileList);
            req.getRequestDispatcher("/jsp/search.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

    }
}
