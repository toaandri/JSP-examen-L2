<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  request.setAttribute("pageTitle", "Créer un compte");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="card">
    <h1 class="h1">Créer un compte</h1>
    <p class="muted">On commence par les infos de base, puis on passe au questionnaire.</p>

    <form method="post" action="${pageContext.request.contextPath}/auth/register" class="form">
        <label class="label">Email</label>
        <input class="input" type="email" name="email" placeholder="ex: toi@mail.com" required />

        <label class="label">Mot de passe</label>
        <input class="input" type="password" name="password" minlength="4" required />

        <button class="primaryBtn" type="submit">Continuer</button>
    </form>

    <div class="divider"></div>
    <a class="link" href="${pageContext.request.contextPath}/auth/login">J’ai déjà un compte</a>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

