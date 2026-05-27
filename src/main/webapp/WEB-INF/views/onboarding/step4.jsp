<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  request.setAttribute("pageTitle", "Onboarding (4/4)");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="wizard">
    <div class="wizardDots">
        <div class="dot"></div><div class="dot"></div><div class="dot"></div><div class="dot active"></div>
    </div>

    <div class="card">
        <h1 class="h1">Bio & ville</h1>
        <p class="muted">Étape 4/4</p>

        <form method="post" action="${pageContext.request.contextPath}/onboarding" class="form">
            <input type="hidden" name="step" value="4" />

            <label class="label">Ville</label>
            <input class="input" name="city" placeholder="ex: Antananarivo" />

            <label class="label">Bio</label>
            <textarea class="input" name="bio" rows="4" placeholder="Une phrase cool, simple, vraie."></textarea>

            <button class="primaryBtn" type="submit">Terminer</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

