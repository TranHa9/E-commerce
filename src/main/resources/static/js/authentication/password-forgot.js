$(document).ready(function () {
    $("#btn-cancel").click(function () {
        window.location.href = "/logins";
    });
    $.validator.addMethod(
        "passwordPattern",
        function (value, element) {
            return this.optional(element) || /^(?=.*[a-zA-Z])(?=.*\d)/.test(value);
        },
        "Mật khẩu phải chứa cả chữ và số"
    );

    $.validator.addMethod(
        "passwordMatch",
        function (value, element) {
            return this.optional(element) || value === $("#password").val();
        },
        "Mật khẩu mới và xác nhận mật khẩu không khớp"
    );

    $("#passwordNew-form").validate({
        onfocusout: false,
        onkeyup: false,
        onclick: false,
        rules: {
            "password": {
                required: true,
                minlength: 6,
                maxlength: 16,
                passwordPattern: true
            },
            "confirmedPassword": {
                required: true,
                minlength: 6,
                maxlength: 16,
                passwordPattern: true,
                passwordMatch: true
            }

        },
        messages: {
            "password": {
                required: "Mật khẩu bắt buộc nhập",
                minlength: "Mật khẩu phải có ít nhất 6 ký tự",
                maxlength: "Mật khẩu tối đa 16 ký tự",
            },
            "confirmedPassword": {
                required: "Mật khẩu bắt buộc nhập",
                minlength: "Mật khẩu phải có ít nhất 6 ký tự",
                maxlength: "Mật khẩu tối đa 16 ký tự",
            }

        },
    });
    $("#passwordNew-form .form-control").on("focus", function () {
        $(this).siblings(".error").text(""); // Xóa lỗi của trường input này
        $(this).removeClass("error");
    });


    const urlParts = window.location.pathname.split('/');
    const userId = urlParts[urlParts.length - 2];

    $("#btn-confirm").click(function () {
        const isValidForm = $("#passwordNew-form").valid();
        if (!isValidForm) {
            return;
        }
        const data = getDataForm()
        $.ajax({
            url: `/api/v1/accounts/${userId}/password`,
            type: 'PATCH',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Thành công!", "success");
                setTimeout(() => {
                    window.location.href = "/logins";
                }, 2000);
            },
            error: function (xhr) {
                if (xhr.status === 404) {
                    showToast("Email bạn nhập không tồi tại", "error");
                    $("#failed-email").text("Email bạn nhập không tồi tại, Vui lòng kiểm tra lại");
                }
            }
        });
    })

    function getDataForm() {
        const formValues = $("#passwordNew-form").serializeArray();
        const password = {};
        formValues.forEach(input => {
            password[input.name] = input.value;
        });
        return password;
    }

    $('#toggle-password').click(function () {
        const passwordField = $('#password');
        const type = passwordField.attr('type') === 'password' ? 'text' : 'password';
        passwordField.attr('type', type);
        const icon = $(this);
        if (type === 'password') {
            icon.removeClass('fa-eye-slash').addClass('fa-eye');
        } else {
            icon.removeClass('fa-eye').addClass('fa-eye-slash');
        }
    });
    $('#toggle-confirmed-password').click(function () {
        const passwordField = $('#confirmedPassword');
        const type = passwordField.attr('type') === 'password' ? 'text' : 'password';
        passwordField.attr('type', type);
        const icon = $(this);
        if (type === 'password') {
            icon.removeClass('fa-eye-slash').addClass('fa-eye');
        } else {
            icon.removeClass('fa-eye').addClass('fa-eye-slash');
        }
    });
})