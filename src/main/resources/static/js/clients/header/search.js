$(document).ready(function () {
    let data = {};
    let totalPage;
    let totalRecord;
    let pageInfo;
    let pageSize = 10;
    let pageIndex = 0;

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
                renderProductLists(response)
            }
        });
    }

    function renderProductLists(data) {
        const productContainer = $("#product-lists");
        productContainer.empty();
        if (!data || data.data?.length === 0) {
            productContainer.append(
                `<h4 class="mt-25 mb-35">Không tìm thấy sản phẩm nào phù hợp</h4>`
            );
            return;
        }
        const products = data.data;
        totalPage = data.totalPage;
        totalRecord = data.totalRecord;
        pageInfo = data.pageInfo;
        products.forEach(product => {
            const productHTML = `
            <div class="col-xl-3 col-lg-4 col-md-6 col-sm-6 col-12">
                <div class="card-grid-style-3">
                    <div class="card-grid-inner">
                         <div class="image-box">
                             <span class="label bg-brand-2">${product.discount ? `-${product.discount}%` : ''}</span>
                                 <a href="/detail-product/${product.id}"><img src="api/v1/files/product/${product.productImages[0]}" alt="${product.productImages[0]}"></a>
                                </div>
                                <div class="info-right">
                                    <a class="font-xs color-gray-500" href="#">${product.shopName}</a><br>
                                    <a class="color-brand-3 font-sm-bold product-name" href="/detail-product/${product.id}">${product.productName}</a>
                                    <div class="rating">
                                        ${Array(product.averageRating).fill('<img src="/img/template/icons/star.svg" alt="Ecom">').join('')}
                                        <span class="font-xs color-gray-500">(${product.averageRating})</span>
                                    </div>
                                    <div class="price-info">
                                        <strong class="font-lg-bold color-brand-3 price-main">${formatCurrency(product.minPrice)}đ</strong>
                                    </div>
<!--                                    <ul class="contact-infor mb-50">-->
<!--                                        <li><img src="/img/page/product/icon-location.svg"-->
<!--                                            alt=""><strong>Đia chỉ: </strong><span id="info-address"></span>-->
<!--                                        </li>-->
<!--                                    </ul>-->
                          </div>
                    </div>
                </div>
            </div>
        `;
            productContainer.append(productHTML);
        });
        renderPagination(data.totalPage, data.pageInfo.pageNumber);
    }

    //Phân trang
    function renderPagination(totalPage, currentPage) {
        const pagination = $(".pagination");
        pagination.empty();
        pagination.append(`
            <li class="page-item"><a class="page-link page-prev"></a></li>
        `);

        for (let i = 1; i <= totalPage; i++) {
            const isActive = i === currentPage + 1 ? "active" : "";
            pagination.append(`<li class="page-item" page="${i - 1}"><a class="page-link ${isActive}">${i}</a></li>`);
        }

        pagination.append(`
            <li class="page-item"><a class="page-link page-next"></a></li>
        `);

        // Xử lý sự kiện chuyển trang
        pagination.find(".page-item").on('click', function () {
            const page = $(this).attr("page");
            if (page) {
                pageIndex = parseInt(page);
                getProductData({});
            }
        });

        pagination.find(".page-next").on('click', function () {
            if (currentPage < totalPage - 1) {
                pageIndex = currentPage + 1;
                getProductData({});
            }
        });

        pagination.find(".page-prev").on('click', function () {
            if (currentPage > 0) {
                pageIndex = currentPage - 1;
                getProductData({});
            }
        });
    }

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
            // Gán giá trị nếu đang ở trang /search
            if (window.location.pathname === '/search' && searchData?.categoryId) {
                categorySelect.val(searchData.categoryId).trigger('change');
            }
        }
    });

    // Gọi API lấy các shop
    $.ajax({
        url: '/api/v1/shops',
        type: 'GET',
        success: function (response) {
            const shopFillters = $(".sidebar-border .sidebar-content .list-nav-arrow")
            shopFillters.empty();
            const mainShops = response.data.slice(0, 5);
            const extraShops = response.data.slice(5);
            mainShops.forEach(function (shop) {
                shopFillters.append(`<li class="shop-item" value="${shop.id}">${shop.name}</li>`);
            });
            const moreMenu = $("#moreMenu .list-nav-arrow");
            moreMenu.empty();
            extraShops.forEach(function (shop) {
                moreMenu.append(`
                    <li class="shop-item" value="${shop.id}">${shop.name}</li>`);
            });
            $(".shop-item").on("click", function () {
                const shopId = $(this).attr("value");
                data.shopId = shopId;
                $(".shop-item").removeClass("active"); // Gỡ bỏ lớp "active" khỏi tất cả các mục
                $(this).addClass("active");
                getProductData(data);
            });
        }
    });

    // Xử lý sự kiện khi nhấn nút tìm kiếm
    $('#search-button').on('click', function () {
        const formValues = $(".form-search").serializeArray();
        formValues.forEach(input => {
            data[input.name] = input.value;
        });
        if (window.location.pathname === '/search') {
            clearFilters();
            sessionStorage.setItem('searchData', JSON.stringify(data));
            $("#show-text-product").empty()
            $("#show-text-product").append(`<p>Kết quả tìm kiếm "${data.productName}"</p>`)
            getProductData(data);
        } else {
            // Nếu không, chuyển hướng sang trang /search
            sessionStorage.setItem('searchData', JSON.stringify(data));
            window.location.href = '/search';
        }
    });

    // Lấy searchData từ sessionStorage nếu đang ở trang /search
    let searchData = {};
    // Nếu đang ở trang /search, gán các giá trị khác từ searchData
    if (window.location.pathname === '/search') {
        const savedData = sessionStorage.getItem('searchData');
        searchData = savedData ? JSON.parse(savedData) : {};
        data.productName = searchData.productName
        data.categoryId = searchData.categoryId
        if (searchData?.productName) {
            $(".box-keysearch input").val(searchData.productName);
            $("#show-text-product").empty()
            $("#show-text-product").append(`<p>Kết quả tìm kiếm "${searchData.productName}"</p>`)
        }
        getProductData(searchData);
    }

    $("#btn-search-price").on('click', function () {
        const priceFrom = $("#price-from").val().trim();
        const priceTo = $("#price-to").val().trim();
        $("#price-error").hide();
        if (!priceFrom || !priceTo) {
            $("#price-error").show();
        } else {
            data.minPrice = priceFrom;
            data.maxPrice = priceTo;
            getProductData(data);
        }
    })
    $("#btn-search-brand").on('click', function () {
        const brand = $("#brand-search").val().trim();
        $("#brand-error").hide();
        if (!brand) {
            $("#brand-error").show();
        } else {
            data.brand = brand;
            getProductData(data);
        }
    })

    $(".list-checkbox li").on('click', function () {
        data.averageRating = $(this).attr("value");
        // Xóa lớp "active" khỏi tất cả các mục
        $(".list-checkbox li").removeClass("active");
        // Thêm lớp "active" cho mục được chọn
        $(this).addClass("active");
        getProductData(data);
    });

    $("#btn-clear-search").on('click', function () {
        clearFilters();
    })

    function clearFilters() {
        const categoryId = data.categoryId;
        const name = data.productName;
        data = {
            categoryId: categoryId,
            productName: name
        };
        $("#price-error").hide();
        $("#brand-error").hide();
        $(".list-checkbox li").removeClass("active");
        $(".shop-item").removeClass("active");
        $("#price-from").val('');
        $("#price-to").val('');
        $("#brand-search").val('').trigger('change');
        getProductData(data);
    }

    // Xử lý sự kiện cho dropdown sắp xếp
    $("#dropdownSortBy li").on("click", function () {
        // Lấy giá trị của mục được chọn
        const sortValue = $(this).attr("value");
        data.sortBy = sortValue;
        // Xóa lớp "active" khỏi tất cả các mục
        $("#dropdownSortBy li").removeClass("active");
        // Thêm lớp "active" cho mục được chọn
        $(this).addClass("active");
        // Thay đổi nội dung nút dropdown để hiển thị mục được chọn
        const selectedText = $(this).text();
        $("#dropdownSort").text(selectedText);
        // Gọi lại API để cập nhật danh sách sản phẩm
        getProductData(data);
    });

    // Xử lý sự kiện khi chọn "Hiển thị" sản phẩm
    $("#dropdownSortItem li").on("click", function () {
        pageSize = $(this).attr("value");
        data.pageSize = pageSize;

        const selectedText = $(this).text();
        $("#dropdownSort2 span").text(selectedText);
        // Xóa lớp "active" khỏi tất cả các mục
        $("#dropdownSortItem li").removeClass("active");
        // Thêm lớp "active" cho mục được chọn
        $(this).addClass("active");
        // Gọi lại API để lấy sản phẩm theo pageSize mới
        getProductData(data);
    });

});
