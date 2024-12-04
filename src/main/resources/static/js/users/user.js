$(document).ready(function () {
    $.validator.addMethod(
        "phonePattern",
        function (value, element) {
            if (!value) return true;
            return /^0\d{9}$/.test(value);
        },
        "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số"
    );

    $.validator.addMethod(
        "passwordPattern",
        function (value, element) {
            if (!value) return true;
            return this.optional(element) || /^(?=.*[a-zA-Z])(?=.*\d)/.test(value);
        },
        "Mật khẩu phải chứa cả chữ và số"
    );

    $.validator.addMethod(
        "dobPastDate",
        function (value, element) {
            if (!value) return true;
            const dob = new Date(value);
            const today = new Date();
            return dob < today;
        },
        "Ngày sinh phải là ngày trong quá khứ"
    );

    $("#form-info").validate({
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
                phonePattern: true
            },
            "dob": {
                dobPastDate: true
            },
            "avatar": {
                imageFile: true
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
                phonePattern: "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số"
            },
            "dob": {
                dobPastDate: "Ngày sinh phải là ngày trong quá khứ"
            }

        },
    });
    $("#form-info .form-control").on("focus", function () {
        $(this).siblings(".error").text(""); // Xóa lỗi của trường input này
        $(this).removeClass("error");
    });
    const user = JSON.parse(localStorage.getItem("user"));
    let chosenFile = null;
    if (user && user.id) {
        $.ajax({
            url: `/api/v1/users/${user.id}`,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                $("#form-info input[name='name']").val(data.name);
                $("#form-info input[name='gender']").val(data.gender);
                $("#form-info input[name='email']").val(data.email);
                $("#form-info input[name='phone']").val(data.phone);
                $("#form-info input[name='dob']").val(data.dob);
                if (data.avatar) {
                    $(".img-avatar").attr("src", "/api/v1/files/user/" + data.avatar);
                }
            },
        })
    } else {
        window.location.href = "/logins";
    }

    $(".change-avatar-btn").click((event) => {
        event.preventDefault();
        $("#avatar-input").click();
        $("#avtar-error").text("");
    });

    $("#avatar-input").change(event => {
        const file = event.target.files[0];
        if (!file) {
            return;
        }
        const maxSize = 10 * 1024; // 10KB
        if (file.size > maxSize) {
            $("#avtar-error").text("Kích thước file không được vượt quá 10KB.")
            return;
        }
        chosenFile = file;
        const imageBlob = new Blob([chosenFile], {type: chosenFile.type});
        const imageUrl = URL.createObjectURL(imageBlob);
        $(".img-avatar").attr("src", imageUrl);
    });


    $("#btn-user-save").click(() => {
        const isValidForm = $("#form-info").valid();
        if (!isValidForm) {
            return;
        }
        const formData = new FormData();
        formData.append('request', JSON.stringify(getDataForm()));
        if (chosenFile) {
            formData.append('avatar', chosenFile, chosenFile.name);
        }
        $.ajax({
            url: `/api/v1/users/${user.id}`,
            type: 'PUT',
            //enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                showToast("Lưu thành công", "success");
                localStorage.setItem("user", JSON.stringify(response));
            }
        });
    })

    function getDataForm() {
        const formValues = $("#form-info").serializeArray();
        const user = {};
        formValues.forEach(input => {
            user[input.name] = input.value;
        });
        return user;
    }

})