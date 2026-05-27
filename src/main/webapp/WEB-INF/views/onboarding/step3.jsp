<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Onboarding (3/4)");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="wizard">
    <div class="wizardDots">
        <div class="dot"></div><div class="dot"></div><div class="dot active"></div><div class="dot"></div>
    </div>

    <div class="card">
        <h1 class="h1">Centres d’intérêt</h1>
        <p class="muted">Étape 3/4</p>

        <form method="post" action="${pageContext.request.contextPath}/onboarding" class="form">
            <input type="hidden" name="step" value="3" />

            <div class="chips">
                <c:forEach var="it" items="${interests}">
                    <label class="chip">
                        <input type="checkbox" name="interestId" value="${it.id}" />
                        <span><c:out value="${it.label}"/></span>
                    </label>
                </c:forEach>
            </div>

            <button class="primaryBtn" type="submit">Suivant</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

