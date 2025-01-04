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

async function getUserDetail(id) {
    const accessToken = localStorage.getItem("accessToken");
    let user = null;
    await $.ajax({
        url: `/api/v1/users/${id}`,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        success: function (response) {
            user = response;
        },
    });
    return user;
}

async function fetchShopData() {
    const user = JSON.parse(localStorage.getItem("user"));
    const accessToken = localStorage.getItem("accessToken");
    let shop = null;
    await $.ajax({
        url: `/api/v1/shops/user/${user?.id}`,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        success: function (response) {
            shop = response;
        }
    });
    return shop;
}

// Kiểm tra xem token có hết hạn không
function isTokenExpired(token) {
    if (!token) {
        return true;
    }
    const decodedToken = parseJwt(token);
    if (!decodedToken || !decodedToken.exp) return true;
    const expTime = decodedToken.exp * 1000;
    return expTime < Date.now();
}

// Hàm giải mã JWT (token) để lấy payload
function parseJwt(token) {
    if (!token || token.split('.').length !== 3) return null;
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

const accessToken = localStorage.getItem("accessToken");
const ajaxSetupObj = {
    error: function (data) {
        if (data.responseJSON.code === 401) {
            showToast("Phiên làm việc hết hạn, vui lòng đăng nhập lại.", "error");
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            localStorage.removeItem("user");
            window.location.href = "/logins";
        } else if (data.responseJSON.code === 404) {
            showToast("Trang không tồn tại.", "warning");
            window.location.href = "/not-founds";
        } else {
            showToast(`Đã có lỗi`, "error");
        }
    }
}
if (accessToken) {
    ajaxSetupObj["headers"] = {
        "Authorization": `Bearer ${accessToken}`
    }
}
$.ajaxSetup(ajaxSetupObj);

async function refreshToken() {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken || !accessToken) return;

    await $.ajax({
        url: "/api/v1/authentications/refresh_token",
        method: "POST",
        data: JSON.stringify({refreshToken: refreshToken}),
        contentType: "application/json",
        success: async function (data) {
            localStorage.setItem("accessToken", data?.jwt);
            localStorage.setItem("refreshToken", data?.refreshToken);
            const user = await getUserDetail(data?.id);
            if (user) {
                localStorage.setItem("user", JSON.stringify(user));
            }
            setRefreshTimer();
        }
    });
}


function setRefreshTimer() {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) return;

    const decodedToken = parseJwt(accessToken);
    if (!decodedToken || !decodedToken.exp) return;

    const expTime = decodedToken.exp * 1000;  // Lấy thời gian hết hạn (convert sang milliseconds)
    const timeUntilExpiry = expTime - Date.now();  // Tính thời gian còn lại
    const refreshTime = timeUntilExpiry - 60000;  // Refresh trước 1 phút (60,000ms)

    if (refreshTime > 0) {
        setTimeout(() => {
            refreshToken();
        }, refreshTime);
    }
}

//Định dạng giá Việt
function formatCurrency(value) {
    return value.toLocaleString('vi-VN');
}

//Lấy số lượng sản phẩm trong giỏ
function updateCartItemCount(userId) {
    $.ajax({
        url: `/api/v1/cartItems/${userId}`,
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            $("#cart-item-count").text(data.length);
        }
    });
}


