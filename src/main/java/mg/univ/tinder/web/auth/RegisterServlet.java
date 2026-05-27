package mg.univ.tinder.web.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.univ.tinder.dao.DbUtil;
import mg.univ.tinder.dao.UserDao;
import mg.univ.tinder.web.SessionKeys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/auth/register"})
public class RegisterServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = trim(req.getParameter("email"));
        String password = trim(req.getParameter("password"));

        if (email == null || !email.contains("@") || password == null || password.length() < 4) {
            req.setAttribute("error", "Email ou mot de passe invalide.");
            doGet(req, resp);
            return;
        }

        try (Connection c = DbUtil.getConnection()) {
            long userId = userDao.create(c, email.toLowerCase(), password);
            req.getSession().setAttribute(SessionKeys.USER_ID, userId);
            req.getSession().setAttribute(SessionKeys.USER_EMAIL, email.toLowerCase());
            resp.sendRedirect(req.getContextPath() + "/onboarding?step=1");
        } catch (SQLException e) {
            req.setAttribute("error", "Ce mail est déjà utilisé.");
            doGet(req, resp);
        }
    }

    private static String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

