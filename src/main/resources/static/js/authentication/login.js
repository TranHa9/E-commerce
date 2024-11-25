$(document).ready(function () {
    $("#btn-login").click(function () {
        const data = getDataForm()

        $.ajax({
            url: '/api/v1/authentications/login',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                localStorage.setItem("accessToken", data?.jwt);
                localStorage.setItem("refreshToken", data?.refreshToken);
                const user = {
                    id: data?.id,
                    email: data?.email,
                    name: data?.name,
                    role: data?.roles?.[0]
                };
                localStorage.setItem("user", JSON.stringify(user));
                showToast("Đăng nhập thành công", "success");
                setTimeout(() => {
                    window.location.href = "/";
                }, 1000);
            },
            error: function (data) {
                showToast("Thất bại", "error");
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
})