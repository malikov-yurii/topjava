var ajaxUrl = 'ajax/profile/meals/';
var datatableApi;

function updateTable() {
    $.get(ajaxUrl, updateTableByData);
}

$(function () {
    datatableApi = $('#datatable').DataTable({
        "ajax": {
            "url": ajaxUrl,
            "dataSrc": ""
        },
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "dateTime"
            },
            {
                "data": "description"
            },
            {
                "data": "calories"
            },
            {
                "orderable": false,
                "defaultContent": "",
                "render": renderEditBtn
            },
            {
                "orderable": false,
                "defaultContent": "",
                "render": renderDeleteBtn
            }
        ],
        "createdRow": function (row, data, dataIndex) {
            if (data.exceed) {
                $(row).css("color", "red");
            } else {
                $(row).css("color", "blue");
            }
        },
        "order": [
            [
                0,
                "desc"
            ]
        ],
        "initComplete": makeEditable
    });
});