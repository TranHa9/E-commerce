$(document).ready(function () {

    const tableBody = $('table tbody');
    const pagination = $(".pagination-area .pagination")
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
                renderProductTable(response)
            }
        });
    }

    function renderProductTable(data) {
        pagination.empty();
        tableBody.empty();
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
            console.log(product)
            const isProductActive = product.variants && product.variants.some(variant => variant.status === 'ACTIVE');
            const productStatus = isProductActive ? 'ACTIVE' : 'INACTIVE';
            const row = `<tr>
                <td width="40%"><a class="itemside" href="#">
                    <div class="left"><img class="img-sm img-avatar" 
                    src="${product.avatar ? `/api/v1/files/product/${product.productImage}` : '/img/people/avatar-default.jpg'}"
                    alt="avatar"></div>
                    <div class="info pl-3">
                        <h6 class="mb-0 title">${product.productName}</h6><small class="text-muted">User ID: #${product.id}</small>
                    </div>
                </a></td>
                <td>${product.productPrice}</td>
                <td>${product.categoryName}</td>
                <td>${product.shopName}</td>
                <td>${product.productStockQuantity}</td>
                <td><span class="badge rounded-pill alert-${productStatus === 'ACTIVE' ? 'success' : 'danger'}">${productStatus}</span></td>
                <td class="text-end"><a
                                class="btn btn-sm font-sm rounded btn-brand mr-5" href="#"><i
                                class="material-icons md-edit"></i> Edit</a><a
                                class="btn btn-sm font-sm btn-light rounded" href="#"><i
                                class="material-icons md-delete_forever"></i> Delete</a></td>
            </tr>`;

            tableBody.append(row);
        });
        pagination.append(`
            <li class="page-item previous-page"><a class="page-link" href="#"><i class="material-icons md-chevron_left"></i></a></li>
        `)
        for (let i = 1; i <= totalPage; i++) {
            const page = `<li class="page-item ${i === (pageInfo.pageNumber + 1) ? "active" : ""}" page="${i - 1}"><a class="page-link" href="#">${i}</a></li>`;
            pagination.append(page);
        }
        pagination.append(`
            <li class="page-item next-page"><a class="page-link" href="#"><i class="material-icons md-chevron_right"></i></a></li>
        `)

        $(".page-item").click(function (event) {
            pageIndex = $(event.currentTarget).attr("page");
            if (!pageIndex) {
                return;
            }
            getProductData({});
        })

        $(".next-page").click(function () {
            if (pageInfo.pageNumber === totalPage - 1) {
                return;
            }
            pageIndex = pageInfo.pageNumber + 1;
            getProductData({});
        })
        $(".previous-page").click(function () {
            if (pageInfo.pageNumber === 0) {
                return;
            }
            pageIndex = pageInfo.pageNumber - 1;
            getProductData({});
        })
    }

    getProductData({});
})