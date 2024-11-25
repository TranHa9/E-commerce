$(document).ready(function () {
    $("#btn-cancel").click(function () {
        window.location.href = "/logins";
    });

    $("#btn-password-forgot").click(function () {
        const email = $("#email").val().trim();

        $.ajax({
            url: '/api/v1/accounts/password_forgotten_emails',
            type: 'POST',
            data: JSON.stringify({email: email}),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Hướng dẫn đặt lại mật khẩu đã được gửi đến email", "success");
            },
            error: function () {
                showToast("Thất bại! Vui lòng kiểm tra lại email", "error");
            }
        });
    })
})