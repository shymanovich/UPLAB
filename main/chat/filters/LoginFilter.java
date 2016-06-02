package main.chat.filters;

/**
 * Created by vlad on 02.06.2016.
 */
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(value = "/*")
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(((HttpServletRequest) servletRequest).getRequestURI().equals("/") ||
                ((HttpServletRequest) servletRequest).getRequestURI().equals("/pages/login.jsp")) {
            AuthenticationChecker filter = new AuthenticationChecker(servletRequest);
            if (!filter.checkAuthenticated()) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletRequest) servletRequest).getSession().setAttribute("nickname", filter.getUsername());
                ((HttpServletResponse) servletResponse).sendRedirect("/pages/chat.jsp");
            } else {
                servletResponse.getOutputStream().println("403, Forbidden");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
