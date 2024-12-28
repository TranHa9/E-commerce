$(document).ready(function () {
    let shop;
    let totalPage;
    let totalRecord;
    let pageInfo;
    let pageSize = 10;
    let pageIndex = 0;

    async function getProductData(data) {
        try {
            const response = await $.ajax({
                url: `/api/v1/products/shop/${shop.id}`,
                type: 'GET',
                data: {
                    pageIndex: pageIndex,
                    pageSize: pageSize,
                    ...data
                },
                contentType: "application/json; charset=utf-8",
            });
            renderProductTable(response);
        } catch (error) {
            showToast("Thất bại", "error");
        }
    }

    (async function () {
        try {
            shop = await fetchShopData();
            await getProductData({});
        } catch (error) {
            showToast("Có lỗi", "error");
        }
    })();

    function renderProductTable(data) {
        const tableBody = $('#product-list tbody');
        const pagination = $(".pagination-area .pagination");
        tableBody.empty();
        pagination.empty();
        if (!data || data.data?.length === 0) {
            tableBody.append(
                `<tr><td colspan="6">Chưa có dữ liệu</td></tr>`
            );
            return;
        }
        const products = data.data;
        totalPage = data.totalPage;
        totalRecord = data.totalRecord;
        pageInfo = data.pageInfo;

        products.forEach(function (product) {
            const firstImage = product.productImages.length > 0 ? product.productImages[0] : null;

            const row = `<tr>
                <td width="40%"><a class="itemside" href="#">
                    <div class="left"><img class="img-sm img-avatar" 
                    src="${firstImage ? `/api/v1/files/product/${firstImage}` : '/img/people/avatar-default.jpg'}"
                    alt="avatar"></div>
                    <div class="info pl-3">
                        <h6 class="mb-0 title">${product.productName}</h6>
                        <small class="text-muted">Sản phẩm ID: #${product.id}</small>
                    </div>
                </a></td>
                <td>${product.minPrice}</td>
                <td>${product.maxPrice}</td>
                <td>${product.categoryName}</td>
                <td>${product.shopName}</td>
                <td>${product.productStockQuantity}</td>
                <td><span class="badge rounded-pill alert-${product.status === 'ACTIVE' ? 'success' : product.status === 'OUT_OF_STOCK' ? 'warning' : 'danger'}">
                    ${product.status === 'ACTIVE' ? 'Có sẵn' : product.status === 'OUT_OF_STOCK' ? 'Hết hàng' : 'Ngừng bán'}</span></td>
                <td class="text-end">
                        <button class="btn btn-sm font-sm rounded btn-brand mr-5 edit-product-btn" 
                                    data-bs-toggle="modal"
                                    data-bs-target="#edit-product-modal"
                                    data-id="${product.id}"
                                    >
                                        <i class="material-icons md-edit"></i>
                                    Sửa</button>
                        <div class="btn-group">
                             <button
                                class="btn btn-sm font-sm btn-light rounded" type="button" data-bs-toggle="dropdown"><i
                                                class="material-icons md-more_horiz"></i></button>
                             <ul class="dropdown-menu">
                                <li><p class="dropdown-item inactive" data-id="${product.id}" >Ngừng bán</p></li>
                                 <li><p class="dropdown-item outOfStock" data-id="${product.id}" >Hết hàng</p></li>
                             </ul>
                         </div>
                </td>
            </tr>`;

            tableBody.append(row);
        });
        renderPagination(data.totalPage, data.pageInfo.pageNumber);
    }

    //Phân trang
    function renderPagination(totalPage, currentPage) {
        const pagination = $(".pagination-area .pagination");
        pagination.empty();
        pagination.append(`
            <li class="page-item previous-page"><a class="page-link" href="#"><i class="material-icons md-chevron_left"></i></a></li>
        `);

        for (let i = 1; i <= totalPage; i++) {
            const isActive = i === currentPage + 1 ? "active" : "";
            pagination.append(`<li class="page-item ${isActive}" page="${i - 1}"><a class="page-link" href="#">${i}</a></li>`);
        }

        pagination.append(`
            <li class="page-item next-page"><a class="page-link" href="#"><i class="material-icons md-chevron_right"></i></a></li>
        `);

        // Xử lý sự kiện chuyển trang
        pagination.find(".page-item").click(function () {
            const page = $(this).attr("page");
            if (page) {
                pageIndex = parseInt(page);
                getProductData({});
            }
        });

        pagination.find(".next-page").click(function () {
            if (currentPage < totalPage - 1) {
                pageIndex = currentPage + 1;
                getProductData({});
            }
        });

        pagination.find(".previous-page").click(function () {
            if (currentPage > 0) {
                pageIndex = currentPage - 1;
                getProductData({});
            }
        });
    }

    $("#btn-search").click(function () {
        const formValues = $("#form-search").serializeArray();
        const data = {};
        formValues.forEach(input => {
            data[input.name] = input.value;
        });

        getProductData(data)
    })

    $("#btn-reset").click(function () {
        $("#form-search")[0].reset();
        pageIndex = 0;
        getProductData({});
    });


    $(document).on("click", ".inactive", function () {
        const productId = $(this).data("id");
        $.ajax({
            url: `/api/v1/products/${productId}/status`,
            type: "PATCH",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify("INACTIVE"),
            success: function () {
                showToast("Thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
        })
    });

    $(document).on("click", ".outOfStock", function () {
        const productId = $(this).data("id");
        $.ajax({
            url: `/api/v1/products/${productId}/status`,
            type: "PATCH",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify("OUT_OF_STOCK"),
            success: function () {
                showToast("Thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
        })
    });

})