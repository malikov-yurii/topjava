<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron">
    <div class="container">
        <div class="shadow">
            <h3><fmt:message key="users.title"/></h3>

            <div class="view-box">
                <a class="btn btn-sm btn-info"><fmt:message key="users.add"/></a>

                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th><fmt:message key="users.name"/></th>
                        <th><fmt:message key="users.email"/></th>
                        <th><fmt:message key="users.roles"/></th>
                        <th><fmt:message key="users.active"/></th>
                        <th><fmt:message key="users.registered"/></th>
                        <th></th>
                        <th></th>
                    </tr>
                    </thead>
                    <c:forEach items="${users}" var="user">
                        <jsp:useBean id="user" scope="page" type="ru.javawebinar.topjava.model.User"/>
                        <tr>
                            <td><c:out value="${user.name}"/></td>
                            <td><a href="mailto:${user.email}">${user.email}</a></td>
                            <td>${user.roles}</td>
                            <td>
                                <input type="checkbox"
                                       <c:if test="${user.enabled}">checked</c:if>/>
                            </td>
                            <td><fmt:formatDate value="${user.registered}" pattern="dd-MMMM-yyyy"/></td>
                            <td><a class="btn btn-xs btn-primary"><fmt:message key="common.update"/></a></td>
                            <td><a class="btn btn-xs btn-danger"><fmt:message key="common.delete"/></a></td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>
