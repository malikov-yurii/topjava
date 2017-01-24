<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>
<link rel="stylesheet" href="webjars/datatables/1.10.12/css/dataTables.bootstrap.min.css">

<body>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron">
    <div class="container">
        <div class="shadow">
            <h3><spring:message code="users.title"/></h3>

            <div class="view-box">
                <a class="btn btn-sm btn-info" onclick="add('<spring:message code="users.add"/>')"><spring:message code="users.add"/></a>

                <table class="table table-striped display" id="datatable">
                    <thead>
                    <tr>
                        <th><spring:message code="users.name"/></th>
                        <th><spring:message code="users.email"/></th>
                        <th><spring:message code="users.roles"/></th>
                        <th><spring:message code="users.active"/></th>
                        <th><spring:message code="users.registered"/></th>
                        <th></th>
                        <th></th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>

<div class="modal fade" id="editRow">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="modalTitle"></h2>
            </div>
            <div class="modal-body">
                <form:form class="form-horizontal" method="post" id="detailsForm">
                    <input type="text" hidden="hidden" id="id" name="id">

                    <div class="form-group">
                        <label for="name" class="control-label col-xs-3"><spring:message code="users.name"/></label>

                        <div class="col-xs-9">
                            <input type="text" class="form-control" id="name" name="name" placeholder="<spring:message code="users.name"/>">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="email" class="control-label col-xs-3"><spring:message code="users.email"/></label>

                        <div class="col-xs-9">
                            <input type="email" class="form-control" id="email" name="email" placeholder="<spring:message code="users.email"/>">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="password" class="control-label col-xs-3"><spring:message code="users.password"/></label>

                        <div class="col-xs-9">
                            <input type="password" class="form-control" id="password" name="password" placeholder="<spring:message code="users.password"/>">
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-xs-offset-3 col-xs-9">
                            <button class="btn btn-primary" type="button" onclick="save()"><spring:message code="common.save"/></button>
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    var edit_title ='<spring:message code="users.edit"/>';
</script>
<script type="text/javascript" src="webjars/datatables/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.12/js/dataTables.bootstrap.min.js"></script>
<script type="text/javascript" src="webjars/noty/2.3.8/js/noty/packaged/jquery.noty.packaged.min.js"></script>
<script type="text/javascript" src="resources/js/datatablesUtil.js"></script>
<script type="text/javascript" src="resources/js/userDatatables.js"></script>
</html>