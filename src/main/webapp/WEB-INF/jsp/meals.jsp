<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<html>
<jsp:include page="fragments/headTag.jsp"/>
<link rel="stylesheet" href="webjars/datatables/1.10.12/css/jquery.dataTables.min.css">
<body>
<jsp:include page="fragments/bodyHeader.jsp"/>

div class="jumbotron">
<div class="container">
    <div class="shadow">
        <br/>
        <h3><fmt:message key="meals.title"/></h3>

        <div class="view-box">

            <form class="form-horizontal" method="post" id="filterForm">

                <div class="form-group">
                    <label for="date_time" class="control-label col-xs-3">Start DateTime</label>

                    <div class="col-xs-9">
                        <input type="datetime-local" class="form-control" id="start_date_time" name="start_date_time"
                               placeholder="Start DateTime">
                    </div>
                </div>

                <div class="form-group">
                    <label for="date_time" class="control-label col-xs-3">End DateTime</label>

                    <div class="col-xs-9">
                        <input type="datetime-local" class="form-control" id="end_date_time" name="end_date_time"
                               placeholder="End DateTime">
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-xs-offset-3 col-xs-9">
                        <button type="submit" class="btn btn-primary">Filter</button>
                    </div>
                </div>
            </form>

            <a class="btn btn-sm btn-info" onclick="add()"><fmt:message key="meals.add"/></a>

            <table class="table table-striped display" id="datatable">
                <thead>
                <tr>
                    <th><fmt:message key="meals.dateTime"/></th>
                    <th><fmt:message key="meals.description"/></th>
                    <th><fmt:message key="meals.calories"/></th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <c:forEach items="${meals}" var="meal">
                    <jsp:useBean id="meal" scope="page" type="ru.javawebinar.topjava.to.MealWithExceed"/>
                    <tr>
                        <td><c:out value="${fn:formatDateTime(meal.dateTime)}"/></td>
                            <%--<td><${meal.dateTime}"/></td>--%>
                        <td>${meal.description}</td>
                        <td>${meal.calories}</td>
                        <td><a class="btn btn-xs btn-primary edit" id="${meal.id}"><fmt:message
                                key="common.update"/></a></td>
                        <td><a class="btn btn-xs btn-danger delete" id="${meal.id}"><fmt:message
                                key="common.delete"/></a></td>
                    </tr>
                </c:forEach>
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
                <h2 class="modal-title"><fmt:message key="meals.edit"/></h2>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" method="post" id="detailsForm">
                    <input type="text" hidden="hidden" id="id" name="id">

                    <div class="form-group">
                        <label for="date_time" class="control-label col-xs-3">Date Time</label>

                        <div class="col-xs-9">
                            <input type="datetime-local" class="form-control" id="date_time" name="date_time"
                                   placeholder="Date Time">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description" class="control-label col-xs-3">Description</label>

                        <div class="col-xs-9">
                            <input type="text" class="form-control" id="description" name="description"
                                   placeholder="description">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="calories" class="control-label col-xs-3">Calories</label>

                        <div class="col-xs-9">
                            <input type="number" class="form-control" id="calories" name="calories"
                                   placeholder="calories">
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-xs-offset-3 col-xs-9">
                            <button type="submit" class="btn btn-primary">Save</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>


<script type="text/javascript" src="webjars/jquery/2.2.4/jquery.min.js"></script>
<script type="text/javascript" src="webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="webjars/noty/2.3.8/js/noty/packaged/jquery.noty.packaged.min.js"></script>
<script type="text/javascript" src="resources/js/datatablesUtil.js"></script>
<script type="text/javascript">

    var ajaxUrl = 'ajax/profile/meallist/';
    var datatableApi;
    //    var filterApi;

       $(function () {
//     filterApi = $('#filterForm').dataTable({
//     "bPaginate": false,
//     "bInfo": false,
//     "aoColumns": [
//     {
//     "mData": "start_date_time"
//     },
//     {
//     "mData": "end_date_time"
//     }
//     ],
//     "aaSorting": [
//     [
//     0,
//     "asc"
//     ]
//     ]
//     });*/

    datatableApi = $('#datatable').dataTable({
        "bPaginate": false,
        "bInfo": false,
        "aoColumns": [
            {
                "mData": "date_time"
            },
            {
                "mData": "description"
            },
            {
                "mData": "calories"
            },
            {
                "sDefaultContent": "Edit",
                "bSortable": false
            },
            {
                "sDefaultContent": "Delete",
                "bSortable": false
            }
        ],
        "aaSorting": [
            [
                0,
                "asc"
            ]
        ]
    });
    makeEditable();
    })
    ;
</script>
</html>
