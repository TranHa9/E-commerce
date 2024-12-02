$(document).ready(function () {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.id) {
        $.ajax({
            url: `/api/v1/users/${user.id}`,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log(data)
                $("#form-info input[name='name']").val(data.name);
                $("#form-info input[name='gender']").val(data.gender);
                $("#form-info input[name='email']").val(data.email);
                $("#form-info input[name='phone']").val(data.phone);
                $("#form-info input[name='dob']").val(data.dob);
                if(data.avatar){
                    $(".img-avatar").attr("src", "data:image/png;base64," + data.avatar);
                }

            },
        })
    } else {
        window.location.href = "/logins";
    }

    $(".change-avatar-btn").click((event) => {
        event.preventDefault();
        $("#avatar-input").click();
    });

    $("#avatar-input").change(event => {
        const file = event.target.files[0];
        if (!file) {
            return;
        }
        chosenFile = file;
        const imageBlob = new Blob([chosenFile], {type: chosenFile.type});
        const imageUrl = URL.createObjectURL(imageBlob);
        $(".img-avatar").attr("src", imageUrl);
    });


    $("#btn-user-save").click(() => {
        const formData = new FormData($("#form-info")[0]);
        $.ajax({
            url: `/api/v1/users/${user.id}`,
            type: 'PUT',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                showToast("Lưu thành công", "success");
            }
        });
    })

})