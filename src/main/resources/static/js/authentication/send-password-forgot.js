$(document).ready(function () {
    $("#btn-cancel").click(function () {
        window.location.href = "/logins";
    });

    $("#forgot-form").validate({
        onfocusout: false,
        onkeyup: false,
        onclick: false,
        rules: {
            "email": {
                required: true,
                maxlength: 100,
                email: true
            }
        },
        messages: {
            "email": {
                required: "Email bắt buộc nhập",
                maxlength: "Email tối đa 100 ký tự",
                email: "Vui lòng nhập đúng định dạng email"
            }

        },
    });
    $("#forgot-form .form-control").on("focus", function () {
        $(this).siblings(".error").text(""); // Xóa lỗi của trường input này
        $(this).removeClass("error");
    });

    $("#btn-password-forgot").click(function () {
        const isValidForm = $("#forgot-form").valid();
        if (!isValidForm) {
            return;
        }
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