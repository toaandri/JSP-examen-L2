<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Chat");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="card">
    <h1 class="h1">Chat</h1>
    <p class="muted">Match #<c:out value="${matchId}"/></p>

    <div class="chatBox" id="chatBox">
        <c:forEach var="m" items="${messages}">
            <div class="msg ${m.fromUserId == userId ? 'me' : 'them'}" data-id="${m.id}">
                <div class="bubble"><c:out value="${m.body}"/></div>
            </div>
        </c:forEach>
    </div>

    <form class="chatForm" method="post" action="${pageContext.request.contextPath}/app/chat/send">
        <input type="hidden" name="matchId" value="${matchId}"/>
        <input class="input" name="body" placeholder="Écrire un message..." autocomplete="off" />
        <button class="primaryBtn" type="submit">Envoyer</button>
    </form>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

