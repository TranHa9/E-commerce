$(document).ready(function (){
    const userId = $("#userId").val()
    $.ajax({
        url: `/api/v1/accounts/${userId}/activations`,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        success: function () {
            showToast("Kích hoạt thành công", "success");
            setTimeout(() => {
                window.location.href = "/logins";
            }, 2000);
        },
        error: function () {
            showToast("Kích hoạt thất bại", "error");
        }
    });
})