function makeEditable() {
    /*$('.checkbox').change(function () {
        // deleteRow($(this).attr("id"));
        deleteRow($(this).closest('tr').attr('id'))
    });*/

    // $('input:checkbox').change(
    //     function(){
    //
    //         saveActiveChange($(this).closest('tr').attr('id'));
    //         /*if ($(this).is(':checked')) {
    //             alert('checked');
    //         }*/
    //     });

    $('.delete').click(function () {
        // deleteRow($(this).attr("id"));
        deleteRow($(this).closest('tr').attr('id'))
    });

    $('#detailsForm').submit(function () {
        save();
        return false;
    });

    $('#filterForm').submit(function () {
        updateTableFiltered();
        return false;
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(event, jqXHR, options, jsExc);
    });
}
function updateTableFiltered() {
    var form = $('#filterForm');
    debugger;
    $.ajax({
        type: "POST",
        url: ajaxUrl + "filtered",
        data: form.serialize(),
        success: function () {
            updateTable();
            successNoty('Filter success');
        }
    });
}

function updateTable() {
    $.get(ajaxUrl, function (data) {
        datatableApi.fnClearTable();
        $.each(data, function (key, item) {
            datatableApi.fnAddData(item);
        });
        datatableApi.fnDraw();
    });
}

function save() {
    var form = $('#detailsForm');
    debugger;
    $.ajax({
        type: "POST",
        url: ajaxUrl,
        data: form.serialize(),
        success: function () {
            $('#editRow').modal('hide');
            updateTable();
            successNoty('Saved');
        }
    });
}

function add() {
    $('#id').val(null);
    $('#editRow').modal();
}

function deleteRow(id) {
    $.ajax({
        url: ajaxUrl + id,
        type: 'DELETE',
        success: function () {
            updateTable();
            successNoty('Deleted');
        }
    });
}

var failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(text) {
    closeNoty();
    noty({
        text: text,
        type: 'success',
        layout: 'bottomRight',
        timeout: true
    });
}

function failNoty(event, jqXHR, options, jsExc) {
    closeNoty();
    failedNote = noty({
        text: 'Failed: ' + jqXHR.statusText + "<br>",
        type: 'error',
        layout: 'bottomRight'
    });
}


// function saveActiveChange() {
//     var data = $(this).dataTable( {
//         "id": $(this).closest('tr').attr('id'),
//         "name": $('#user_name').val(),
//         "asdfname": $('#user').val().,
//         "email": true,
//         "password": "sources/sample.json",
//         "registered": "sources/sample.json",
//         "enabled": "sources/sample.json",
//         "calories_per_day": "sources/sample.json"
//     } );
//     // debugger;
//     $.ajax({
//         type: "POST",
//         url: ajaxUrl,
//         // data: form.serialize(),
//         data: form.constructor(),
//         success: function () {
//             // $('#editRow').modal('hide');
//             updateTable();
//             successNoty('Saved');
//         }
//     });
// }


