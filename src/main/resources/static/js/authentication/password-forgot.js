$(document).ready(function () {
    $("#btn-cancel").click(function () {
        window.location.href = "/logins";
    });

    const urlParts = window.location.pathname.split('/');
    const userId = urlParts[urlParts.length - 2];

    $("#btn-confirm").click(function () {
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
                }, 1000);
            },
            error: function () {
                showToast("Thất bại", "error");
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
})