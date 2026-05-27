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

@WebServlet(name = "LoginServlet", urlPatterns = {"/auth/login"})
public class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = trim(req.getParameter("email"));
        String password = trim(req.getParameter("password"));

        if (email == null || password == null) {
            req.setAttribute("error", "Email / mot de passe requis.");
            doGet(req, resp);
            return;
        }

        try (Connection c = DbUtil.getConnection()) {
            var opt = userDao.findByEmail(c, email.toLowerCase());
            if (opt.isEmpty()) {
                req.setAttribute("error", "Identifiants invalides.");
                doGet(req, resp);
                return;
            }
            UserDao.User u = opt.get();
            if (!userDao.verifyPasswordAndUpgradeSeedIfNeeded(c, u.getId(), u.getPasswordHash(), password)) {
                req.setAttribute("error", "Identifiants invalides.");
                doGet(req, resp);
                return;
            }

            userDao.touchLastLogin(c, u.getId());
            req.getSession().setAttribute(SessionKeys.USER_ID, u.getId());
            req.getSession().setAttribute(SessionKeys.USER_EMAIL, u.getEmail());
            resp.sendRedirect(req.getContextPath() + "/app/swipe");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private static String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

