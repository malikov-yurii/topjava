<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="topjava" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>

<body>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron">
    <div class="container">
        <div class="shadow">
            <h3>
                <c:if test="${register}">
                    <spring:message code="app.register"/>
                    <spring:message code="common.add" var="saveButton"/>
                </c:if>
                <c:if test="${not register}">
                    ${userTo.name} <spring:message code="app.profile"/>
                    <spring:message code="common.update" var="saveButton"/>
                </c:if>
            </h3>

            <div class="view-box">
                <form:form modelAttribute="userTo" class="form-horizontal" method="post"
                           action="${register ? 'register' : 'profile'}" charset="utf-8" accept-charset="UTF-8">

                    <spring:message code="users.name" var="userName"/>
                    <topjava:inputField label='${userName}' name="name"/>

                    <spring:message code="users.email" var="userEmail"/>
                    <topjava:inputField label='${userEmail}' name="email"/>

                    <spring:message code="users.password" var="userPassword"/>
                    <topjava:inputField label='${userPassword}' name="password" inputType="password"/>

                    <spring:message code="users.caloriesPerDay" var="caloriesPerDay"/>
                    <topjava:inputField label='${caloriesPerDay}' name="caloriesPerDay" inputType="number"/>

                    <div class="form-group">
                        <div class="col-xs-offset-2 col-xs-10">
                            <button type="submit" class="btn btn-primary">${saveButton}</button>
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
</body>
</html>
