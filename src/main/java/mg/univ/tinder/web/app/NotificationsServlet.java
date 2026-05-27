package mg.univ.tinder.web.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.univ.tinder.dao.DbUtil;
import mg.univ.tinder.dao.NotificationDao;
import mg.univ.tinder.web.SessionKeys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "NotificationsServlet", urlPatterns = {"/app/notifications"})
public class NotificationsServlet extends HttpServlet {
    private final NotificationDao notificationDao = new NotificationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        try (Connection c = DbUtil.getConnection()) {
            req.setAttribute("pageTitle", "Notifications");
            req.setAttribute("notifications", notificationDao.listForUser(c, userId));
            req.getRequestDispatcher("/WEB-INF/views/app/notifications.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = (long) req.getSession().getAttribute(SessionKeys.USER_ID);
        long id = parseLong(req.getParameter("id"), -1);
        if (id <= 0) {
            resp.sendRedirect(req.getContextPath() + "/app/notifications");
            return;
        }
        try (Connection c = DbUtil.getConnection()) {
            notificationDao.markRead(c, userId, id);
            resp.sendRedirect(req.getContextPath() + "/app/notifications");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private static long parseLong(String s, long fallback) {
        try { return Long.parseLong(s); } catch (Exception e) { return fallback; }
    }
}

