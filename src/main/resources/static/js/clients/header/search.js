$(document).ready(function () {
    let totalPage;
    let totalRecord;
    let pageInfo;
    let pageSize = 10;
    let pageIndex = 0;
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
        }
    });

    function getProductData(data) {
        $.ajax({
            url: '/api/v1/products',
            type: 'GET',
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                ...data
            },
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                console.log(response)
            }
        });
    }


    // Xử lý sự kiện khi nhấn nút tìm kiếm
    $('#search-button').on('click', function () {
        const formValues = $(".form-search").serializeArray();
        const data = {};
        formValues.forEach(input => {
            data[input.name] = input.value;
        });
        console.log(data)
        if (window.location.pathname === '/search') {
            getProductData(data);
        } else {
            // Nếu không, chuyển hướng sang trang /search
            sessionStorage.setItem('searchData', JSON.stringify(data));
            window.location.href = '/search';
        }
    });

    if (window.location.pathname === '/search') {
        const savedData = sessionStorage.getItem('searchData');
        const searchData = savedData ? JSON.parse(savedData) : {};
        getProductData(searchData);
    }

});
