package mg.univ.tinder.web.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.univ.tinder.dao.*;
import mg.univ.tinder.service.MatchService;
import mg.univ.tinder.web.SessionKeys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@WebServlet(name = "SwipeServlet", urlPatterns = {"/app/swipe"})
public class SwipeServlet extends HttpServlet {
    private final BrowseDao browseDao = new BrowseDao();
    private final SwipeDao swipeDao = new SwipeDao();
    private final ProfileDao profileDao = new ProfileDao();
    private final UserInterestDao userInterestDao = new UserInterestDao();
    private final OnboardingQuestionDao onboardingQuestionDao = new OnboardingQuestionDao();
    private final NotificationDao notificationDao = new NotificationDao();
    private final MatchService matchService = new MatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        int minAge = parseInt(req.getParameter("minAge"), 18);
        int maxAge = parseInt(req.getParameter("maxAge"), 99);
        if (minAge < 18) minAge = 18;
        if (maxAge > 99) maxAge = 99;
        if (minAge > maxAge) {
            int tmp = minAge;
            minAge = maxAge;
            maxAge = tmp;
        }

        try (Connection c = DbUtil.getConnection()) {
            Optional<ProfileDao.Profile> me = profileDao.findByUserId(c, userId);
            if (me.isEmpty() || !profileDao.isOnboardingComplete(me.get())) {
                resp.sendRedirect(req.getContextPath() + "/onboarding?step=1");
                return;
            }
            int questionCount = onboardingQuestionDao.countActiveQuestions(c);
            int answerCount = onboardingQuestionDao.countAnswersForUser(c, userId);
            if (questionCount > 0 && answerCount < questionCount) {
                resp.sendRedirect(req.getContextPath() + "/onboarding?step=4&idx=0");
                return;
            }

            Optional<BrowseDao.Candidate> cand = Optional.empty();
            for (int i = 0; i < 8; i++) {
                Optional<BrowseDao.Candidate> tmp = browseDao.pickNextCandidate(c, userId);
                if (tmp.isEmpty()) break;
                if (tmp.get().getAge() >= minAge && tmp.get().getAge() <= maxAge) {
                    cand = tmp;
                    break;
                } else {
                    swipeDao.upsertSwipe(c, userId, tmp.get().getUserId(), SwipeDao.Decision.NOPE);
                }
            }
            if (cand.isEmpty()) {
                req.setAttribute("pageTitle", "Swipe");
                req.setAttribute("empty", true);
                req.getRequestDispatcher("/WEB-INF/views/app/swipe.jsp").forward(req, resp);
                return;
            }

            BrowseDao.Candidate p = cand.get();
            Set<String> my = userInterestDao.setLabelsForUser(c, userId);
            Set<String> other = userInterestDao.setLabelsForUser(c, p.getUserId());
            Set<String> myPrefs = onboardingQuestionDao.setScoreTagsForUser(c, userId);
            Set<String> otherPrefs = onboardingQuestionDao.setScoreTagsForUser(c, p.getUserId());

            int myAge = ageFromBirth(me.get().getBirthdate());
            int diff = Math.abs(myAge - p.getAge());
            boolean sameCity = me.get().getCity() != null && p.getCity() != null && me.get().getCity().equalsIgnoreCase(p.getCity());

            MatchService.MatchResult result = matchService.compute(my, other, myPrefs, otherPrefs, diff, sameCity);

            req.setAttribute("pageTitle", "Swipe");
            req.setAttribute("candidate", p);
            req.setAttribute("matchPercent", result.getPercent());
            req.setAttribute("commonInterests", result.getCommonInterests());
            req.setAttribute("commonPreferenceTags", result.getCommonPreferenceTags());
            req.getRequestDispatcher("/WEB-INF/views/app/swipe.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        long toUserId = parseLong(req.getParameter("toUserId"), -1);
        String decisionStr = req.getParameter("decision");
        SwipeDao.Decision decision = "LIKE".equalsIgnoreCase(decisionStr) ? SwipeDao.Decision.LIKE : SwipeDao.Decision.NOPE;

        if (toUserId <= 0 || toUserId == userId) {
            resp.sendRedirect(req.getContextPath() + "/app/swipe");
            return;
        }

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            swipeDao.upsertSwipe(c, userId, toUserId, decision);

            if (decision == SwipeDao.Decision.LIKE) {
                boolean mutual = swipeDao.hasLiked(c, toUserId, userId);
                if (mutual) {
                    Optional<Long> matchId = swipeDao.ensureMatchIfMutualLike(c, userId, toUserId);
                    if (matchId.isPresent()) {
                        notificationDao.createNewMatch(c, userId, matchId.get(), toUserId);
                        notificationDao.createNewMatch(c, toUserId, matchId.get(), userId);
                        req.getSession().setAttribute("flash", "Nouveau match !");
                    }
                }
            }
            c.commit();
            resp.sendRedirect(req.getContextPath() + "/app/swipe");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private static long parseLong(String s, long fallback) {
        try { return Long.parseLong(s); } catch (Exception e) { return fallback; }
    }

    private static int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (Exception e) { return fallback; }
    }

    private static int ageFromBirth(java.time.LocalDate birth) {
        if (birth == null) return 0;
        java.time.LocalDate now = java.time.LocalDate.now();
        int years = now.getYear() - birth.getYear();
        if (birth.plusYears(years).isAfter(now)) years--;
        return Math.max(0, years);
    }
}

