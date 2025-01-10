$(document).ready(function () {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user?.role) {
        // Ẩn tất cả menu-item
        $(".menu-item").hide();
        // Hiển thị các menu mà userRole có trong danh sách data-role
        $(".menu-item").each(function () {
            const roles = $(this).data('role').split(',');
            if (roles.includes(user?.role)) {
                $(this).show(); // Hiển thị nếu role của user nằm trong danh sách roles
            }
        });
    } else {
        showToast("Phiên làm việc hết hạn, vui lòng đăng nhập lại.", "error");
        setTimeout(() => {
            window.location.href = "/logins";
        }, 1000);
    }
    // Lấy URL hiện tại
    const currentPath = window.location.pathname;
    // Lặp qua tất cả các menu link
    $(".menu-link").each(function () {
        const $this = $(this);
        const linkPath = $this.attr("href");
        // Kiểm tra nếu linkPath trùng với currentPath
        if (currentPath.includes(linkPath)) {
            // Xóa active khỏi các menu-item khác
            $(".menu-item").removeClass("active");
            // Gắn class active vào menu-item hiện tại
            $this.closest(".menu-item").addClass("active");
        }
    });
});