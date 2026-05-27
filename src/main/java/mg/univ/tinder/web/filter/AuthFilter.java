package mg.univ.tinder.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.univ.tinder.web.SessionKeys;

import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/app/*", "/onboarding"})
public class AuthFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        Object userId = req.getSession().getAttribute(SessionKeys.USER_ID);
        if (userId == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }
        chain.doFilter(req, res);
    }
}

