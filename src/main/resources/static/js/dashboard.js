$(document).ready(function () {
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