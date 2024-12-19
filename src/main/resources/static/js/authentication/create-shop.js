$(document).ready(function () {
    const user = JSON.parse(localStorage.getItem("user"));
    $("#btn-create-shop").click(async function () {
        const name = $("#name").val().trim();
        const description = $("#description").val().trim();
        const requestData = {
            name: name,
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