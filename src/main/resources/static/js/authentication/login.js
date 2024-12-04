$(document).ready(function () {
    $.validator.addMethod(
        "passwordPattern",
        function (value, element) {
            return this.optional(element) || /^(?=.*[a-zA-Z])(?=.*\d)/.test(value);
        },
        "Mật khẩu phải chứa cả chữ và số"
    );

    $("#login-form").validate({
        onfocusout: false,
        onkeyup: false,
        onclick: false,
        rules: {
            "email": {
                required: true,
                maxlength: 100,
                email: true
            },
            "password": {
                required: true,
                minlength: 6,
                maxlength: 16,
                passwordPattern: true
            },

        },
        messages: {
            "email": {
                required: "Email bắt buộc nhập",
                maxlength: "Email tối đa 100 ký tự",
                email: "Vui lòng nhập đúng định dạng email"
            },
            "password": {
                required: "Mật khẩu bắt buộc nhập",
                minlength: "Mật khẩu phải có ít nhất 6 ký tự",
                maxlength: "Mật khẩu tối đa 16 ký tự",
            }

        },
    });

    $("#login-form .form-control").on("focus", function () {
        $(this).siblings(".error").text(""); // Xóa lỗi của trường input này
        $(this).removeClass("error");
    });

    $("#btn-login").click(async function () {
        const isValidForm = $("#login-form").valid();
        if (!isValidForm) {
            return;
        }
        const data = getDataForm()
        const loginResponse = await $.ajax({
            url: '/api/v1/authentications/login',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8"
        });

        localStorage.setItem("accessToken", loginResponse?.jwt);
        localStorage.setItem("refreshToken", loginResponse?.refreshToken);

        const user = await getUserDetail(loginResponse?.id);

        if (user) {
            localStorage.setItem("user", JSON.stringify(user));
        }
        showToast("Đăng nhập thành công", "success");
        setTimeout(() => {
            window.location.href = "/";
        }, 1000);
    })

    function getDataForm() {
        const formValues = $("#login-form").serializeArray();
        const user = {};
        formValues.forEach(input => {
            user[input.name] = input.value;
        });
        return user;
    }

    async function getUserDetail(id) {
        const accessToken = localStorage.getItem("accessToken");
        let user = null;
        await $.ajax({
            url: `/api/v1/users/${id}`,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
            success: function (response) {
                user = response;
            },
        });
        return user;
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
})