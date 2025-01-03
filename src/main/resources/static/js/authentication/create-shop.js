$(document).ready(function () {
    const user = JSON.parse(localStorage.getItem("user"));

    $("#shop-form").validate({
        onfocusout: false,
        onkeyup: false,
        onclick: false,
        rules: {
            "name": {
                required: true,
                maxlength: 150
            },
            "address": {
                required: true,
                maxlength: 250
            },

        },
        messages: {
            "name": {
                required: "Tên cửa hàng bắt buộc nhập",
                maxlength: "Tên cửa hàng tối đa 150 ký tự",
            },
            "address": {
                required: "Địa chỉ bắt buộc nhập",
                maxlength: "Địa chỉ tối đa 250 ký tự",
            }

        },
    });

    $("#btn-create-shop").click(async function () {
        const isValidForm = $("#shop-form").valid();
        if (!isValidForm) {
            return;
        }
        const name = $("#name").val().trim();
        const address = $("#address").val().trim();
        const description = $("#description").val().trim();
        const requestData = {
            name: name,
            address: address,
            description: description
        };
        await $.ajax({
            url: `/api/v1/shops/${user?.id}`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestData),
            success: async function (response) {
                const user = await getUserDetail(response?.id);
                if (user) {
                    localStorage.setItem("user", JSON.stringify(user));
                }
                showToast("Tạo thành công", "success");
                setTimeout(() => {
                    window.location.href = "/";
                }, 1000);
            }
        });
    })
})