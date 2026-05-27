<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Onboarding (3/5)");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="wizard">
    <div class="wizardDots">
        <div class="dot"></div><div class="dot"></div><div class="dot active"></div><div class="dot"></div><div class="dot"></div>
    </div>

    <div class="onboardSwipeWrap">
        <div class="swipeCard" id="interestSwipeCard">
            <div class="swipeMeta">Interet ${idx + 1} / ${total}</div>
            <h1 class="h1">Tu aimes: <c:out value="${interest.label}" /> ?</h1>
            <p class="muted">Swipe gauche pour passer, droite pour garder.</p>
            <div class="onboardCounter">
                Selectionnes pour l'instant: ${pickedInterestIds.size()}
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/onboarding" class="actions">
            <input type="hidden" name="step" value="3" />
            <input type="hidden" name="idx" value="${idx}" />
            <input type="hidden" name="interestId" value="${interest.id}" />
            <button class="roundBtn nope js-swipe-nope" name="decision" value="NOPE" type="submit">✕</button>
            <button class="roundBtn like js-swipe-like" name="decision" value="LIKE" type="submit">♥</button>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/onboarding-swipe.js"></script>
<jsp:include page="/WEB-INF/views/_footer.jspf" />

