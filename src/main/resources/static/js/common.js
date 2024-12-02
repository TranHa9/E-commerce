function showToast(message, type) {
    let color;
    switch (type) {
        case "success":
            color = "#198754";
            break;
        case "error":
            color = "#dc3545";
            break;
        case "warning":
            color = "#fd7e14";
            break;
    }
    Toastify({
        text: message,
        duration: 3000, // bao lâu thì toast tự động mất (milisecond)
        // destination: "https://github.com/apvarun/toastify-js",
        // newWindow: true,
        close: true,
        gravity: "top", // `top` or `bottom`
        position: "right", // `left`, `center` or `right`
        stopOnFocus: true, // Prevents dismissing of toast on hover
        style: {
            background: color,
        },
        // onClick: function(){} // Callback after click
    }).showToast();
}

const accessToken = localStorage.getItem("accessToken");
const ajaxSetupObj = {
    error: function (jqXHR) {
        if (jqXHR.status === 401) {
            showToast("Phiên làm việc hết hạn, vui lòng đăng nhập lại.", "error");
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            localStorage.removeItem("user");
            window.location.href = "/logins";
        } else if (jqXHR.status === 404) {
            showToast("Trang không tồn tại.", "warning");
            window.location.href = "/not-founds";
        } else {
            showToast(`Có lỗi`, "error");
        }
    }
}
if (accessToken) {
    ajaxSetupObj["headers"] = {
        "Authorization": `Bearer ${accessToken}`
    }
}
$.ajaxSetup(ajaxSetupObj);


