$(document).ready(function () {
    const userId = $("#userId").val()
    $.ajax({
        url: `/api/v1/accounts/${userId}/activation_emails`,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        success: function () {
            showToast("Thành công! Vui lòng kiếm tra email", "success");
            setTimeout(() => {
                window.location.href = "/registers";
            }, 1000);
        },
        error: function () {
            showToast("Thất bại", "error");
        }
    });
})