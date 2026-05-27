<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  request.setAttribute("pageTitle", "Onboarding (2/4)");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="wizard">
    <div class="wizardDots">
        <div class="dot"></div><div class="dot active"></div><div class="dot"></div><div class="dot"></div>
    </div>

    <div class="card">
        <h1 class="h1">Genre & orientation</h1>
        <p class="muted">Étape 2/4</p>

        <form method="post" action="${pageContext.request.contextPath}/onboarding" class="form">
            <input type="hidden" name="step" value="2" />

            <label class="label">Genre</label>
            <select class="input" name="genderIdentity" required>
                <option value="">Choisir…</option>
                <option>Femme</option>
                <option>Homme</option>
                <option>Non-binaire</option>
                <option>Autre</option>
            </select>

            <label class="label">Orientation sexuelle</label>
            <select class="input" name="sexualOrientation" required>
                <option value="">Choisir…</option>
                <option>Hétéro</option>
                <option>Gay</option>
                <option>Lesbienne</option>
                <option>Bi</option>
                <option>Pan</option>
                <option>Autre</option>
            </select>

            <label class="label">Tu cherches</label>
            <select class="input" name="lookingFor" required>
                <option value="">Choisir…</option>
                <option>Homme</option>
                <option>Femme</option>
                <option>Tous</option>
            </select>

            <button class="primaryBtn" type="submit">Suivant</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

