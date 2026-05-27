package mg.univ.tinder.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = {"/"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object userId = req.getSession().getAttribute(SessionKeys.USER_ID);
        if (userId != null) {
            resp.sendRedirect(req.getContextPath() + "/app/swipe");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/auth/login");
    }
}

