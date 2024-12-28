$(document).ready(function () {
    const accessToken = localStorage.getItem("accessToken");

    if (!accessToken || isTokenExpired(accessToken)) {
        localStorage.removeItem("user");
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
    }
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.avatar) {
        $("#avatar-info").attr("src", "/api/v1/files/user/" + user.avatar);
    }
    if (user && user.name) {
        $("#user-info").text(` ${user.name}`);
        let dropdownMenu = `
            <ul>
        `;
        if (user?.role && user?.role === "ADMIN") {
            dropdownMenu += `<li><a href="/admin/users">Quản lý tài khoản</a></li>`;
        }
        if (user?.role && user?.role === "SHOP") {
            dropdownMenu += `<li><a href="/shop/products">Quản lý sản phẩm</a></li>`;
        }
        if (user?.role && user?.role !== "SHOP") {
            dropdownMenu += `<li><a href="/shops">Mở shop</a></li>`;
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

    $("#user-info, .image-info").click(function () {
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


    setRefreshTimer();
})
