$(document).ready(function () {
    const accessToken = localStorage.getItem("accessToken");

    if (!accessToken || isTokenExpired(accessToken)) {
        localStorage.removeItem("user");
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
    }
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.name) {
        $("#user-info").text(` ${user.name}`);
        let dropdownMenu = `
            <ul>
        `;
        if (user.role === "ADMIN" || user.role === "SHOP") {
            dropdownMenu += `<li><a href="#">Quản lý</a></li>`;
        }
        dropdownMenu += `
            <li><a href="/users">Tài khoản</a></li>
            <li><a href="#">Đơn hàng</a></li>
            <li><a href="#" id="logout">Đăng xuất</a></li>
        </ul>
        `;
        $(".dropdown-account").html(dropdownMenu);
    } else {
        $("#user-info").text("Xin chào!");
        $(".dropdown-account").html(`
            <ul>
                <li><a href="/logins">Đăng nhập</a></li>
                <li><a href="/registers">Đăng ký</a></li>
            </ul>
        `);
    }

    $("#user-info").click(function () {
        $(".dropdown-account").toggleClass("dropdown-open");
    });

    $("#logout").click(function () {
        $.ajax({
            url: "/api/v1/authentications/logout",
            method: "POST",
            success: function () {
                localStorage.removeItem("user");
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/logins";
            },
            error: function () {
                localStorage.removeItem("user");
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/logins";
            }
        });
    });

    function refreshToken() {
        const accessToken = localStorage.getItem('accessToken');
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken || !accessToken) return;

        $.ajax({
            url: "/api/v1/authentications/refresh_token",
            method: "POST",
            data: JSON.stringify({refreshToken: refreshToken}),
            contentType: "application/json",
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
                setRefreshTimer();
            }
        });
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

    setRefreshTimer();
})
