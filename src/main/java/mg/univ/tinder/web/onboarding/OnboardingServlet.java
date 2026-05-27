package mg.univ.tinder.web.onboarding;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.univ.tinder.dao.DbUtil;
import mg.univ.tinder.dao.InterestDao;
import mg.univ.tinder.dao.ProfileDao;
import mg.univ.tinder.web.SessionKeys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "OnboardingServlet", urlPatterns = {"/onboarding"})
public class OnboardingServlet extends HttpServlet {
    private final ProfileDao profileDao = new ProfileDao();
    private final InterestDao interestDao = new InterestDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int step = parseStep(req.getParameter("step"), 1);
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);

        try (Connection c = DbUtil.getConnection()) {
            if (step == 4) {
                req.setAttribute("interests", interestDao.listAll(c));
            }
            req.setAttribute("step", step);
            req.getRequestDispatcher("/WEB-INF/views/onboarding/step" + step + ".jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int step = parseStep(req.getParameter("step"), 1);
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);

            if (step == 1) {
                String firstName = trim(req.getParameter("firstName"));
                String lastName = trim(req.getParameter("lastName"));
                LocalDate birthdate = parseDate(req.getParameter("birthdate"));
                if (firstName == null || lastName == null || birthdate == null) {
                    req.setAttribute("error", "Tous les champs sont requis.");
                    DbUtil.rollbackQuietly(c);
                    doGet(req, resp);
                    return;
                }
                profileDao.upsertIdentity(c, userId, firstName, lastName, birthdate);
                c.commit();
                resp.sendRedirect(req.getContextPath() + "/onboarding?step=2");
                return;
            }

            if (step == 2) {
                String genderIdentity = trim(req.getParameter("genderIdentity"));
                String sexualOrientation = trim(req.getParameter("sexualOrientation"));
                String lookingFor = trim(req.getParameter("lookingFor"));
                if (genderIdentity == null || sexualOrientation == null || lookingFor == null) {
                    req.setAttribute("error", "Tous les champs sont requis.");
                    DbUtil.rollbackQuietly(c);
                    doGet(req, resp);
                    return;
                }
                profileDao.updateGenderAndOrientation(c, userId, genderIdentity, sexualOrientation, lookingFor);
                c.commit();
                resp.sendRedirect(req.getContextPath() + "/onboarding?step=3");
                return;
            }

            if (step == 3) {
                String[] ids = req.getParameterValues("interestId");
                List<Long> interestIds = new ArrayList<>();
                if (ids != null) {
                    for (String s : ids) {
                        try { interestIds.add(Long.parseLong(s)); } catch (NumberFormatException ignored) {}
                    }
                }
                interestDao.replaceUserInterests(c, userId, interestIds);
                c.commit();
                resp.sendRedirect(req.getContextPath() + "/onboarding?step=4");
                return;
            }

            if (step == 4) {
                String bio = trim(req.getParameter("bio"));
                String city = trim(req.getParameter("city"));
                profileDao.updateBioCity(c, userId, bio, city);
                c.commit();
                resp.sendRedirect(req.getContextPath() + "/app/swipe");
                return;
            }

            c.commit();
            resp.sendRedirect(req.getContextPath() + "/app/swipe");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private static int parseStep(String s, int fallback) {
        try {
            int v = Integer.parseInt(s);
            if (v < 1) return fallback;
            if (v > 4) return 4;
            return v;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static LocalDate parseDate(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

