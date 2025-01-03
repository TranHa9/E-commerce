$(document).ready(function () {
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
                maxlength: 16
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
        await $.ajax({
            url: '/api/v1/authentications/login',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: async function (response) {
                localStorage.setItem("accessToken", response?.jwt);
                localStorage.setItem("refreshToken", response?.refreshToken);
                const user = await getUserDetail(response?.id);
                if (user) {
                    localStorage.setItem("user", JSON.stringify(user));
                }
                showToast("Đăng nhập thành công", "success");
                setTimeout(() => {
                    window.location.href = "/";
                }, 1000);
            },
            error: function (data) {
                if (data.responseJSON.errorCode === 1007) {
                    showToast("Tài khoản mật khẩu không chính xác", "error");
                } else {
                    showToast(data.responseJSON.messages, "error");
                }
            }
        });
    })

    function getDataForm() {
        const formValues = $("#login-form").serializeArray();
        const user = {};
        formValues.forEach(input => {
            user[input.name] = input.value;
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