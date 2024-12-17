$(document).ready(function () {

    const pagination = $(".pagination-area .pagination")
    let shopId;
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
                renderProductTable(response)
            }
        });
    }

    function renderProductTable(data) {
        const tableBody = $('#product-list tbody');
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
            const isProductActive = product.variants && product.variants.some(variant => variant.status === 'ACTIVE');
            const productStatus = isProductActive ? 'ACTIVE' : 'INACTIVE';
            let images = [];
            if (product.productImages) {
                const fixedImages = product.productImages
                    .replace(/[\[\]]/g, '')
                    .split(',')
                    .map(image => image.trim().replace(/(^"|"$)/g, ''))
                    .map(image => `"${image}"`)
                    .join(',');
                images = JSON.parse(`[${fixedImages}]`);
            }
            const firstImage = images.length > 0 ? images[0] : null;

            const row = `<tr>
                <td width="40%"><a class="itemside" href="#">
                    <div class="left"><img class="img-sm img-avatar" 
                    src="${firstImage ? `/api/v1/files/product/${firstImage}` : '/img/people/avatar-default.jpg'}"
                    alt="avatar"></div>
                    <div class="info pl-3">
                        <h6 class="mb-0 title">${product.productName}</h6><small class="text-muted">User ID: #${product.id}</small>
                    </div>
                </a></td>
                <td>${product.basePrice}</td>
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

    $("#change-avatar-btn").click((event) => {
        event.preventDefault();
        $("#avatar-input").click();
        $("#avtar-error").text("");
    });
    $("#avatar-input").change(event => {
        const files = event.target.files;
        if (!files || files.length === 0) {
            return;
        }
        const maxSize = 10 * 1024 * 1024; // 10MB
        const imagePlaceholder = $(".image-preview-container .image-placeholder");
        imagePlaceholder.empty();
        Array.from(files).forEach(file => {
            if (file.size > maxSize) {
                alert("Kích thước file không được vượt quá 10MB.");
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                const imageUrl = e.target.result;
                const imgElement = `<div class="image-preview"><img class="img-sm" src="${imageUrl}" alt="Image preview"></div>`;
                imagePlaceholder.append(imgElement);
            };
            reader.readAsDataURL(file);
        });
    });

    $("#btn-save-create").click(function () {
        const formData = new FormData();
        const formValues = getDataForm();

        if (shopId) {
            formValues.shopId = shopId;
        }
        formData.append('request', JSON.stringify(formValues));
        const files = $("#avatar-input")[0].files;
        if (files.length > 0) {
            Array.from(files).forEach(file => {
                formData.append('images', file, file.name);
            });
        }
        $.ajax({
            url: `/api/v1/products`,
            type: 'POST',
            //enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                showToast("Lưu thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    })

    function getDataForm() {
        const formValues = $("#form-create-product").serializeArray();
        const product = {};
        formValues.forEach(input => {
            product[input.name] = input.value;
        });
        return product;
    }

    const user = JSON.parse(localStorage.getItem("user"));
    $.ajax({
        url: `/api/v1/shops/${user?.id}`,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            shopId = response?.id
        }
    });

    // Hàm để cập nhật bảng biến thể
    function updateVariants(attributes) {
        const tableBody = $("#variants-table tbody");
        tableBody.empty();

        // Tạo tất cả các kết hợp biến thể từ các thuộc tính đã nhập
        const combinations = getCombinations(attributes);

        combinations.forEach(combination => {
            const row = `<tr>
                <td>${combination.join(' - ')}</td>
                <td><input type="number" class="form-control" placeholder="Số lượng"></td>
                <td><input type="number" class="form-control" placeholder="Giá bán"></td>
                <td><input type="file" class="form-control"></td>
            </tr>`
            tableBody.append(row);
        });
    }

    // Hàm tạo kết hợp các giá trị thuộc tính
    function getCombinations(attributes) {
        let result = [[]];
        for (let attribute of attributes) {
            let temp = [];
            for (let val of attribute.values) {
                for (let combination of result) {
                    temp.push(combination.concat(val));
                }
            }
            result = temp;
        }
        return result;
    }

    // Khi nhấn "Lưu thuộc tính"
    $("#save-attribute").click(function () {
        $("#variants").removeClass("d-none")
        // Lấy tất cả các thuộc tính đã thêm
        const attributeElements = $("#attributes-container .attributes-input");
        let attributes = [];

        // Duyệt qua tất cả các thuộc tính và lấy dữ liệu
        attributeElements.each(function () {
            const name = $(this).find("input[name='attribute-name']").val();
            const values = $(this).find("input[name='attribute-values']").val().split(',').map(value => value.trim());
            // const values = $(this).find("input[name='attribute-values']").val();
            attributes.push({name, values});
        });

        console.log(attributes)
        updateVariants(attributes);
    });

    // Sự kiện khi nhấn nút "Thêm thuộc tính"
    $("#add-attribute").click(function () {
        // Tạo các trường nhập liệu mới cho thuộc tính
        const attributeHtml = `
            <div class="attributes-input d-flex gap-2 mb-3">
                <div class="col">
                    <label for="attribute-name" class="form-label">Tên thuộc tính</label>
                    <input class="form-control" name="attribute-name" type="text" placeholder="Ví dụ: Màu sắc" />
                </div>
                <div class="col">
                    <label for="attribute-values" class="form-label">Giá trị thuộc tính</label>
                    <input class="form-control" name="attribute-values" type="text" placeholder="Ví dụ: Đỏ, Xanh, Vàng" />
                </div>
                <button type="button" class="btn btn-danger btn-remove-attribute">Xóa</button>
            </div>
        `;

        // Thêm vào container thuộc tính
        $("#attributes-container").append(attributeHtml);
        $("#attributes-container").on("click", ".btn-remove-attribute", function () {
            $(this).closest('.attributes-input').remove();
        });

    });

})