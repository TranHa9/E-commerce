$(document).ready(function () {
    const userId = $("#userId").val()
    $.ajax({
        url: `/api/v1/accounts/${userId}/activations`,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        success: function () {
            showToast("Kích hoạt thành công", "success");
            $("#info-activation").text("Kích hoạt tài khoản thành công")
            setTimeout(() => {
                window.location.href = "/logins";
            }, 3000);
        },
        error: function () {
            showToast("Kích hoạt thất bại", "error");
            $("#info-activation").html(
                'Kích hoạt tài khoản thất bại, vui lòng kiểm tra lại email. ' +
                '<a href="http://localhost:8080/api/v1/accounts/' + userId + '/activation_emails">' +
                'Hoặc bấm vào đây để gửi lại email kích hoạt</a>'
            );
        }
    });
})