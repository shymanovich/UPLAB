package main.chat.servlets;

/**
 * Created by vlad on 02.06.2016.
 */
import main.chat.classes.DBWorker;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/login")
public class LoginServlet extends HttpServlet {

    DBWorker dbWorker;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("uid")) {
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
        }
        resp.sendRedirect("/pages/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        dbWorker = new DBWorker();

        if(dbWorker.validateUser(username, password)) {
            req.getSession().setAttribute("nickname", username);
            saveUIdCookies(username, resp);
            resp.sendRedirect("/pages/chat.jsp");
        } else {
            req.getRequestDispatcher("pages/login.jsp").include(req, resp);
        }
    }

    private void saveUIdCookies(String username, HttpServletResponse resp) {
        int cookieLifeTime = -1;
        Cookie userIdCookie = new Cookie("uid", String.valueOf(dbWorker.getUserId(username)));
        userIdCookie.setMaxAge(cookieLifeTime);
        resp.addCookie(userIdCookie);
    }
}