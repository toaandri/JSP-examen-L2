package mg.univ.tinder.web.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.univ.tinder.dao.DbUtil;
import mg.univ.tinder.dao.MatchDao;
import mg.univ.tinder.dao.MessageDao;
import mg.univ.tinder.web.SessionKeys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "ChatServlet", urlPatterns = {"/app/chat"})
public class ChatServlet extends HttpServlet {
    private final MessageDao messageDao = new MessageDao();
    private final MatchDao matchDao = new MatchDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        long matchId = parseLong(req.getParameter("matchId"), -1);
        if (matchId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/app/matches");
            return;
        }

        try (Connection c = DbUtil.getConnection()) {
            // Basic authorization: user must be in match list (cheap check)
            boolean allowed = matchDao.listForUser(c, userId).stream().anyMatch(m -> m.getMatchId() == matchId);
            if (!allowed) {
                resp.sendRedirect(req.getContextPath() + "/app/matches");
                return;
            }

            req.setAttribute("pageTitle", "Chat");
            req.setAttribute("matchId", matchId);
            req.setAttribute("userId", userId);
            req.setAttribute("messages", messageDao.listMessages(c, matchId, 0, 50));
            req.getRequestDispatcher("/WEB-INF/views/app/chat.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private static long parseLong(String s, long fallback) {
        try { return Long.parseLong(s); } catch (Exception e) { return fallback; }
    }
}

