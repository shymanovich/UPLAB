package main.chat.filters;

/**
 * Created by vlad on 02.06.2016.
 */
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Filter;

@WebFilter(value = "/pages/chat.jsp")
public class ChatFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        AuthenticationChecker filter = new AuthenticationChecker(servletRequest);
        if(filter.checkAuthenticated()) {
            ((HttpServletRequest) servletRequest).getSession().setAttribute("nickname", filter.getUsername());
            filterChain.doFilter(servletRequest, servletResponse);
        } else if(servletResponse instanceof HttpServletResponse) {
            ((HttpServletResponse) servletResponse).sendRedirect("login.jsp");
        } else {
            servletResponse.getOutputStream().println("403, Forbidden");
        }
    }

    @Override
    public void destroy() {
    }
}
