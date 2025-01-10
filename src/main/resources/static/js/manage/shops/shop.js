$(document).ready(function () {
    let shopId;

    $("#form-info").validate({
        rules: {
            name: {
                required: true,
                maxlength: 150,
            },
            address: {
                required: true,
                maxlength: 250,
            },
            logo: {
                imageFile: true
            },

        },
        messages: {
            name: {
                required: "Tên bắt buộc nhập",
                maxlength: "Tên tối đa 150 ký tự",
            },
            address: {
                required: "Địa chỉ bắt buộc nhập",
                maxlength: "Địa chỉ tối đa 250 ký tự",
            }
        },
    });

    $("#form-info .form-control").on("focus", function () {
        $(this).siblings(".error").text("");
        $(this).removeClass("error");
    });
    const user = JSON.parse(localStorage.getItem("user"));
    let chosenFile = null;
    if (user && user.id) {
        $.ajax({
            url: `/api/v1/shops/user/${user.id}`,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                shopId = data.id;
                $("#form-info input[name='name']").val(data.name);
                $("#form-info input[name='address']").val(data.address);
                $("#form-info textarea[name='description']").val(data.description);
                if (data.logo) {
                    $(".img-avatar").attr("src", "/api/v1/files/logo/" + data.logo);
                }
            },
        })
    } else {
        window.location.href = "/logins";
    }

    $("#change-logo-btn").click((event) => {
        event.preventDefault();
        $("#logo-input").click();
        $("#logo-error").text("");
    });

    $("#logo-input").change(event => {
        const file = event.target.files[0];
        if (!file) {
            return;
        }
        const maxSize = 10 * 1024 * 1024; // 10MB
        if (file.size > maxSize) {
            alert("Kích thước file không được vượt quá 10MB.");
            return;
        }
        chosenFile = file;
        const imageBlob = new Blob([chosenFile], {type: chosenFile.type});
        const imageUrl = URL.createObjectURL(imageBlob);
        $(".img-avatar").attr("src", imageUrl);
    });


    $("#btn-shop-save").click(() => {
        const isValidForm = $("#form-info").valid();
        if (!isValidForm) {
            return;
        }
        const formData = new FormData();
        formData.append('request', JSON.stringify(getDataForm()));
        if (chosenFile) {
            formData.append('logo', chosenFile, chosenFile.name);
        }
        $.ajax({
            url: `/api/v1/shops/${shopId}`,
            type: 'PUT',
            //enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                showToast("Lưu thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    })

    function getDataForm() {
        const formValues = $("#form-info").serializeArray();
        const shop = {};
        formValues.forEach(input => {
            shop[input.name] = input.value;
        });
        return shop;
    }
})