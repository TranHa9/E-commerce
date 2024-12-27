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
<!--                                <button-->
<!--                                    class="btn btn-sm font-sm btn-light rounded" ><i-->
<!--                                    class="material-icons md-delete_forever"></i>Xóa</button>-->
                </td>
            </tr>`;

            tableBody.append(row);
        });
        renderPagination(data.totalPage, data.pageInfo.pageNumber);
    }

    function renderPagination(totalPage, currentPage) {
        const pagination = $(".pagination-area .pagination");

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

})