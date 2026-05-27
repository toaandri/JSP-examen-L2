<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  request.setAttribute("pageTitle", "Connexion");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<div class="card">
    <h1 class="h1">Connexion</h1>
    <p class="muted">Connecte-toi pour commencer à swiper.</p>

    <form method="post" action="${pageContext.request.contextPath}/auth/login" class="form">
        <label class="label">Email</label>
        <input class="input" type="email" name="email" placeholder="ex: alice@example.com" required />

        <label class="label">Mot de passe</label>
        <input class="input" type="password" name="password" placeholder="••••" required />

        <button class="primaryBtn" type="submit">Se connecter</button>
    </form>

    <div class="divider"></div>
    <a class="link" href="${pageContext.request.contextPath}/auth/register">Créer un compte</a>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

