$(document).ready(function () {

    $.validator.addMethod(
        "phonePattern",
        function (value, element) {
            return this.optional(element) || /^0\d{9}$/.test(value);
        },
        "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số"
    );

    $.validator.addMethod(
        "passwordPattern",
        function (value, element) {
            return this.optional(element) || /^(?=.*[a-zA-Z])(?=.*\d)/.test(value);
        },
        "Mật khẩu phải chứa cả chữ và số"
    );

    $("#register-form").validate({
        onfocusout: false,
        onkeyup: false,
        onclick: false,
        rules: {
            "name": {
                required: true,
                maxlength: 150,
            },
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
            "phone": {
                required: true,
                phonePattern: true
            },

        },
        messages: {
            "name": {
                required: "Tên bắt buộc nhập",
                maxlength: "Tên tối đa 150 ký tự",
            },
            "email": {
                required: "Email bắt buộc nhập",
                maxlength: "Email tối đa 100 ký tự",
                email: "Vui lòng nhập đúng định dạng email"
            },
            "password": {
                required: "Mật khẩu bắt buộc nhập",
                minlength: "Mật khẩu phải có ít nhất 6 ký tự",
                maxlength: "Mật khẩu tối đa 16 ký tự",
            },
            "phone": {
                required: "Số điện thoại bắt buộc nhập",
            }

        },
    });
    $("#register-form .form-control").on("focus", function () {
        $(this).siblings(".error").text(""); // Xóa lỗi của trường input này
        $(this).removeClass("error");
    });

    $("#btn-create").click(function () {
        const isValidForm = $("#register-form").valid();
        if (!isValidForm) {
            return;
        }

        const data = getDataForm()

        $.ajax({
            url: '/api/v1/authentications/registration',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                $("#success").remove();
                $("#error").remove();
                showToast("Đăng ký thành công", "success");
                const successHtml = `
                    <div id="success" class="form-group text-success">
                        <p>Đăng ký thành công! Kiểm tra email để kích hoạt tài khoản. Nếu không thấy email hãy 
                        <a href="http://localhost:8080/api/v1/accounts/${data.id}/activation_emails">bấm vào đây</a>.
                        </p>
                    </div>`;
                $(".form-register").append(successHtml);
            },
            error: function (data) {
                $("#success").remove();
                $("#error").remove();
                showToast(data.responseJSON.message, "error");
                const errorHtml = `
                    <div id="error" class="form-group text-danger">
                        <p>Đăng ký thất bại! Kiểm tra lại email và mật khẩu.</p>
                    </div>`;
                $(".form-register").append(errorHtml);
            }
        });
    })

    function getDataForm() {
        const formValues = $("#register-form").serializeArray();
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