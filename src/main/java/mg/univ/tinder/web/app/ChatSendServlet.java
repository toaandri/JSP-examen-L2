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

@WebServlet(name = "ChatSendServlet", urlPatterns = {"/app/chat/send"})
public class ChatSendServlet extends HttpServlet {
    private final MessageDao messageDao = new MessageDao();
    private final MatchDao matchDao = new MatchDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        long matchId = parseLong(req.getParameter("matchId"), -1);
        String body = trim(req.getParameter("body"));

        if (matchId <= 0 || body == null) {
            resp.sendRedirect(req.getContextPath() + "/app/matches");
            return;
        }

        try (Connection c = DbUtil.getConnection()) {
            boolean allowed = matchDao.listForUser(c, userId).stream().anyMatch(m -> m.getMatchId() == matchId);
            if (!allowed) {
                resp.sendRedirect(req.getContextPath() + "/app/matches");
                return;
            }

            messageDao.sendMessage(c, matchId, userId, body);
            resp.sendRedirect(req.getContextPath() + "/app/chat?matchId=" + matchId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private static long parseLong(String s, long fallback) {
        try { return Long.parseLong(s); } catch (Exception e) { return fallback; }
    }

    private static String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

