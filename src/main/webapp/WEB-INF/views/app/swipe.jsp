<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.ee/tags/core" prefix="c" %>
<%
  request.setAttribute("pageTitle", "Swipe");
%>
<jsp:include page="/WEB-INF/views/_layout.jspf" />

<c:if test="${not empty sessionScope.flash}">
    <div class="alert alertSuccess">
        <c:out value="${sessionScope.flash}" />
    </div>
    <c:remove var="flash" scope="session" />
</c:if>

<c:choose>
    <c:when test="${empty == true}">
        <div class="card">
            <h1 class="h1">Plus de profils</h1>
            <p class="muted">Reviens plus tard, ou ajoute plus d’utilisateurs dans le seed.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="swipeWrap">
            <div class="card filterCard">
                <form method="get" action="${pageContext.request.contextPath}/app/swipe" class="filterRow">
                    <div class="filterItem">
                        <div class="label">Âge min</div>
                        <input class="input" type="number" name="minAge" min="18" max="99" value="${param.minAge}" />
                    </div>
                    <div class="filterItem">
                        <div class="label">Âge max</div>
                        <input class="input" type="number" name="maxAge" min="18" max="99" value="${param.maxAge}" />
                    </div>
                    <button class="ghostBtn" type="submit">Filtrer</button>
                </form>
            </div>

            <div class="profileCard">
                <div class="photo" style="background-image:url('${candidate.photoUrl}')"></div>

                <div class="profileBody">
                    <div class="titleRow">
                        <div class="name"><c:out value="${candidate.firstName}" /> <span class="age"><c:out value="${candidate.age}" /></span></div>
                        <div class="pct"><c:out value="${matchPercent}" />%</div>
                    </div>
                    <div class="sub"><c:out value="${candidate.city}" /></div>
                    <div class="bio"><c:out value="${candidate.bio}" /></div>

                    <c:if test="${not empty commonInterests}">
                        <div class="commonTitle">Vous avez en commun</div>
                        <div class="chips">
                            <c:forEach var="t" items="${commonInterests}">
                                <div class="pill"><c:out value="${t}" /></div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${not empty commonPreferenceTags}">
                        <div class="commonTitle">Preferences similaires</div>
                        <div class="chips">
                            <c:forEach var="t" items="${commonPreferenceTags}">
                                <div class="pill"><c:out value="${t}" /></div>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </div>

            <form class="actions" method="post" action="${pageContext.request.contextPath}/app/swipe">
                <input type="hidden" name="toUserId" value="${candidate.userId}" />
                <button class="roundBtn nope" name="decision" value="NOPE" type="submit">✕</button>
                <button class="roundBtn like" name="decision" value="LIKE" type="submit">♥</button>
            </form>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/_footer.jspf" />

