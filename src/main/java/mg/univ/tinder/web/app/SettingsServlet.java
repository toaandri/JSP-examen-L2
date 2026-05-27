package mg.univ.tinder.web.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.univ.tinder.dao.*;
import mg.univ.tinder.web.SessionKeys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SettingsServlet", urlPatterns = {"/app/settings"})
public class SettingsServlet extends HttpServlet {
    private final ProfileDao profileDao = new ProfileDao();
    private final InterestDao interestDao = new InterestDao();
    private final UserInterestDao userInterestDao = new UserInterestDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        try (Connection c = DbUtil.getConnection()) {
            req.setAttribute("pageTitle", "Paramètres");
            req.setAttribute("profile", profileDao.findByUserId(c, userId).orElse(null));
            req.setAttribute("interests", interestDao.listAll(c));
            req.setAttribute("myInterestLabels", userInterestDao.listLabelsForUser(c, userId));
            req.getRequestDispatcher("/WEB-INF/views/app/settings.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        String city = trim(req.getParameter("city"));
        String bio = trim(req.getParameter("bio"));
        String[] ids = req.getParameterValues("interestId");

        List<Long> interestIds = new ArrayList<>();
        if (ids != null) {
            for (String s : ids) {
                try { interestIds.add(Long.parseLong(s)); } catch (NumberFormatException ignored) {}
            }
        }

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            profileDao.updateBioCity(c, userId, bio, city);
            interestDao.replaceUserInterests(c, userId, interestIds);
            c.commit();
            req.getSession().setAttribute("flash", "Profil mis à jour.");
            resp.sendRedirect(req.getContextPath() + "/app/settings");
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

