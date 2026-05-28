<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.ee/tags/core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Profil");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<c:if test="${not empty sessionScope.flash}">
    <div class="alert alertSuccess">
        <c:out value="${sessionScope.flash}" />
    </div>
    <c:remove var="flash" scope="session" />
</c:if>

<div class="card">
    <h1 class="h1">Profil</h1>
    <p class="muted">Met à jour ta bio, ta ville et tes centres d’intérêt.</p>

    <form method="post" action="${pageContext.request.contextPath}/app/settings" class="form">
        <label class="label">Ville</label>
        <input class="input" name="city" value="${profile.city}" />

        <label class="label">Bio</label>
        <textarea class="input" name="bio" rows="4">${profile.bio}</textarea>

        <div class="divider"></div>
        <div class="label">Centres d’intérêt</div>
        <div class="chips">
            <c:forEach var="it" items="${interests}">
                <c:set var="checked" value="false" />
                <c:forEach var="lbl" items="${myInterestLabels}">
                    <c:if test="${lbl == it.label}">
                        <c:set var="checked" value="true" />
                    </c:if>
                </c:forEach>
                <label class="chip">
                    <input type="checkbox" name="interestId" value="${it.id}" <c:if test="${checked}">checked</c:if> />
                    <span><c:out value="${it.label}"/></span>
                </label>
            </c:forEach>
        </div>

        <button class="primaryBtn" type="submit">Enregistrer</button>
    </form>
</div>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

