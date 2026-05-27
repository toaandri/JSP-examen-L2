<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Onboarding (4/5)");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="wizard">
    <div class="wizardDots">
        <div class="dot"></div><div class="dot"></div><div class="dot"></div><div class="dot active"></div><div class="dot"></div>
    </div>

    <div class="onboardSwipeWrap">
        <div class="swipeCard" id="questionSwipeCard">
            <div class="swipeMeta">Question ${idx + 1} / ${total}</div>
            <h1 class="h1"><c:out value="${question.label}" /></h1>
            <p class="muted">Choisis ta vibe, puis swipe droite pour valider.</p>
            <form method="post" action="${pageContext.request.contextPath}/onboarding" class="form">
                <input type="hidden" name="step" value="4" />
                <input type="hidden" name="idx" value="${idx}" />
                <input type="hidden" name="questionId" value="${question.id}" />
                <label class="label">Ta reponse</label>
                <select class="input" name="optionId" required>
                    <option value="">Choisir...</option>
                    <c:forEach var="o" items="${options}">
                        <option value="${o.id}"><c:out value="${o.label}" /></option>
                    </c:forEach>
                </select>
                <div class="actions">
                    <button class="roundBtn nope" type="button" onclick="history.back()">↺</button>
                    <button class="roundBtn like" type="submit">♥</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/onboarding-swipe.js"></script>
<jsp:include page="/WEB-INF/views/_footer.jspf" />

