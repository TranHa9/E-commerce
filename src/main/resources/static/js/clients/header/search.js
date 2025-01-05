$(document).ready(function () {
    let totalPage;
    let totalRecord;
    let pageInfo;
    let pageSize = 10;
    let pageIndex = 0;

    // Lấy searchData từ sessionStorage nếu đang ở trang /search
    let searchData = {};
    if (window.location.pathname === '/search') {
        const savedData = sessionStorage.getItem('searchData');
        searchData = savedData ? JSON.parse(savedData) : {};
    }

    // Gọi API lấy danh mục
    $.ajax({
        url: '/api/v1/categories/active',
        type: 'GET',
        success: function (response) {
            const categorySelect = $('#category-select');
            const categoryFillters = $(".sidebar-border .sidebar-content .list-nav-arrow")
            categorySelect.empty();
            categorySelect.append('<option value="">Tất cả</option>');
            response.forEach(function (category) {
                categorySelect.append(`<option value="${category.id}">${category.name}</option>`);
            });
            // Cập nhật lại select2 (nếu bạn đang dùng select2)
            categorySelect.select2();
            categoryFillters.empty();
            const mainCategories = response.slice(0, 5);
            const extraCategories = response.slice(5);
            categoryFillters.append(`
                <li><a href="#">Tất cả<span class="number">${response.length}</span></a>
                                </li>
            `)
            mainCategories.forEach(function (category) {
                categoryFillters.append(`<li><a href="#">${category.name}<span class="number">09</span></a>
                                        </li>
                                 `);
            });
            const moreMenu = $("#moreMenu .list-nav-arrow");
            moreMenu.empty();
            extraCategories.forEach(function (category) {
                moreMenu.append(`
                <li><a href="#">${category.name}<span class="number"></span></a></li>
            `);
            });
            // Gán giá trị nếu đang ở trang /search
            if (window.location.pathname === '/search' && searchData?.categoryId) {
                categorySelect.val(searchData.categoryId).trigger('change');
            }
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
            sessionStorage.removeItem('searchData');
            getProductData(data);
        } else {
            // Nếu không, chuyển hướng sang trang /search
            sessionStorage.setItem('searchData', JSON.stringify(data));
            window.location.href = '/search';
        }
    });

    // Nếu đang ở trang /search, gán các giá trị khác từ searchData
    if (window.location.pathname === '/search') {
        if (searchData?.productName) {
            $(".box-keysearch input").val(searchData.productName);
        }
        getProductData(searchData);
    }

});
