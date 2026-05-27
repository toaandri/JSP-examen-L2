<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Matches");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="card">
    <h1 class="h1">Matches</h1>
    <p class="muted">Si vous match-back, le chat s’ouvre.</p>

    <c:choose>
        <c:when test="${empty matches}">
            <p class="muted">Aucun match pour l’instant.</p>
        </c:when>
        <c:otherwise>
            <div class="matchGrid">
                <c:forEach var="m" items="${matches}">
                    <a class="matchItem" href="${pageContext.request.contextPath}/app/chat?matchId=${m.matchId}">
                        <div class="matchAvatar" style="background-image:url('${m.otherPhotoUrl}')"></div>
                        <div class="matchName"><c:out value="${m.otherName}"/></div>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

