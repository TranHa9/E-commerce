$(document).ready(function () {
    const userId = $("#userId").val()
    $.ajax({
        url: `/api/v1/accounts/${userId}/activation_emails`,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        success: function () {
            showToast("Thành công! Vui lòng kiếm tra email", "success");
            $("#send-email").text("Đã gửi thông tin kích hoạt thành công! Vui lòng kiểm tra lại email")
            setTimeout(() => {
                window.location.href = "/registers";
            }, 3000);
        },
        error: function (xhr) {
            if (xhr.status === 404) {
                showToast("Tài khoản của bạn không tồn tại", "error");
                $("#send-email").text("Không tìm thấy tài khoản email này, vui lòng kiểm tra lại");
            } else if (xhr.status === 400) {
                showToast("Đã được kích hoạt rồi", "warning");
                $("#send-email").text("Tài khoản của bạn đã được kích hoạt rồi");
            } else {
                showToast("Thất bại", "error");
                $("#send-email").html(
                    'Gửi thông tin kích hoạt thất bại. ' +
                    '<a href="http://localhost:8080/api/v1/accounts/' + userId + '/activation_emails">' +
                    'Bấm vào đây để gửi lại email kích hoạt</a>'
                );
            }
        }
    });
})