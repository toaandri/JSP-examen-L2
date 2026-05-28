<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Notifications");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="card">
    <h1 class="h1">Notifications</h1>
    <p class="muted">Tes derniers événements.</p>

    <c:choose>
        <c:when test="${empty notifications}">
            <p class="muted">Rien pour le moment.</p>
        </c:when>
        <c:otherwise>
            <div class="notifList">
                <c:forEach var="n" items="${notifications}">
                    <div class="notifItem ${empty n.readAt ? 'unread' : ''}">
                        <div class="notifText">
                            <c:choose>
                                <c:when test="${n.type == 'NEW_MATCH'}">Nouveau match !</c:when>
                                <c:otherwise>Notification</c:otherwise>
                            </c:choose>
                            <div class="muted small"><c:out value="${n.payloadJson}"/></div>
                        </div>
                        <c:if test="${empty n.readAt}">
                            <form method="post" action="${pageContext.request.contextPath}/app/notifications">
                                <input type="hidden" name="id" value="${n.id}"/>
                                <button class="ghostBtn" type="submit">Marquer lu</button>
                            </form>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

