<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  request.setAttribute("pageTitle", "Onboarding (1/4)");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="wizard">
    <div class="wizardDots">
        <div class="dot active"></div><div class="dot"></div><div class="dot"></div><div class="dot"></div>
    </div>

    <div class="card">
        <h1 class="h1">Ton identité</h1>
        <p class="muted">Étape 1/4</p>

        <form method="post" action="${pageContext.request.contextPath}/onboarding" class="form">
            <input type="hidden" name="step" value="1" />

            <label class="label">Prénom</label>
            <input class="input" name="firstName" required />

            <label class="label">Nom</label>
            <input class="input" name="lastName" required />

            <label class="label">Date de naissance</label>
            <input class="input" type="date" name="birthdate" required />

            <button class="primaryBtn" type="submit">Suivant</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

