$(document).ready(function () {

    // Gọi API lấy danh mục
    $.ajax({
        url: '/api/v1/categories/active',
        type: 'GET',
        success: function (response) {
            const categorySelect = $('#category-select');
            categorySelect.empty();
            categorySelect.append('<option value="">Tất cả</option>');
            response.forEach(function (category) {
                categorySelect.append(`<option value="${category.id}">${category.name}</option>`);
            });
            // Cập nhật lại select2 (nếu bạn đang dùng select2)
            categorySelect.select2();
        },
        error: function (error) {
            console.error('Lỗi khi lấy danh mục:', error);
        }
    });
});
