$(document).ready(function (){

    $("#btn-create").click(function (){
        const data = getDataForm()

        $.ajax({
            url: '/api/v1/authentications/registration',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                showToast("Đăng ký thành công", "success");
            },
            error: function (data) {
                showToast(data.responseJSON.message, "error");
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