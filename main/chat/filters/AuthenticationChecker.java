package main.chat.filters;

/**
 * Created by vlad on 02.06.2016.
 */
import main.chat.classes.DBWorker;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class AuthenticationChecker {

    private String username = null;

    public AuthenticationChecker(ServletRequest servletRequest) {
        String uid = null;
        if(servletRequest instanceof HttpServletRequest) {
            try {
                Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("uid")) {
                        uid = cookie.getValue();
                    }
                }
            } catch (NullPointerException e) {
                uid = null;
            }
        }
        if(uid != null) {
            username = new DBWorker().getUserById(new Integer(uid));
        }
    }

    public boolean checkAuthenticated() {
        return username != null;
    }

    public String getUsername() {
        return username;
    }
}
