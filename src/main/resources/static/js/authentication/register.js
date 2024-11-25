$(document).ready(function (){

    $("#btn-create").click(function (){
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
})