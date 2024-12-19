$(document).ready(function () {
    let newProductId, newVariantId, shopId;
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
            const isProductActive = product.variants && product.variants.some(variant => variant.status === 'ACTIVE');
            const productStatus = isProductActive ? 'ACTIVE' : 'INACTIVE';
            const images = parseImages(product.productImages);
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

    // Xử lý ảnh sản phẩm
    function parseImages(productImages) {
        if (!productImages) return [];
        const fixedImages = productImages.replace(/[\[\]]/g, '').split(',')
            .map(image => image.trim().replace(/(^"|"$)/g, ''));
        return fixedImages;
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

    $("#btn-save-create").click(async function () {
        // 1. Gọi API tạo product
        await createProduct();

        // 2. Gọi API tạo product-variants
        await addVariant();

        // 3. Gọi API tạo product-attributes
        await addAttributes();

        setTimeout(() => {
            window.location.reload();
        }, 2000);
    })

    async function createProduct() {
        const formProduct = new FormData();
        const formValues = getProductForm();

        if (shopId) {
            formValues.shopId = shopId;
        }
        formProduct.append('request', JSON.stringify(formValues));
        const files = $("#avatar-input")[0].files;
        if (files.length > 0) {
            Array.from(files).forEach(file => {
                formProduct.append('images', file, file.name);
            });
        }

        const productResponse = await $.ajax({
            url: `/api/v1/products`,
            type: 'POST',
            processData: false,
            contentType: false,
            data: formProduct
        });
        showToast("Tạo sản phẩm thành công", "success");
        newProductId = productResponse?.id;
        console.log("newProductId", newProductId)
    }

    async function addVariant() {
        const formVariant = new FormData();
        const formValues = getVariantForm();
        if (newProductId) {
            formValues.productId = newProductId;
        }
        formVariant.append('request', JSON.stringify(formValues));
        const variantFiles = $("#variant-images")[0].files;
        if (variantFiles.length > 0) {
            Array.from(variantFiles).forEach(file => {
                formVariant.append('images', file, file.name);
            });
        }
        const variantResponse = await $.ajax({
            url: `/api/v1/product-variants`,
            type: 'POST',
            processData: false,
            contentType: false,
            data: formVariant
        });
        showToast("Tạo biến thể thành công", "success");
        newVariantId = variantResponse?.id;
        console.log("newVariantId", newVariantId)
    }

    async function addAttributes() {
        const attribute = getAttributeForm()
        if (newVariantId) {
            attribute.variantId = newVariantId;
        }
        const attributeResponse = await $.ajax({
            url: `/api/v1/product-attributes`,
            type: 'POST',
            contentType: "application/json",
            data: JSON.stringify(attribute)
        });
        showToast("Tạo thuộc tính thành công", "success");
        console.log("attributeResponse", attributeResponse)
    }

    function getProductForm() {
        const formValues = $("#form-create-product").serializeArray();
        const product = {};
        formValues.forEach(input => {
            product[input.name] = input.value;
        });
        return product;
    }

    // Lấy thông tin shop
    function fetchShopData() {
        const user = JSON.parse(localStorage.getItem("user"));
        $.ajax({
            url: `/api/v1/shops/user/${user?.id}`,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                shopId = response?.id;
            },
            error: function () {
                showToast("Không thể lấy thông tin shop", "error");
            }
        });
    }

    fetchShopData();

    // Hàm để cập nhật bảng biến thể
    function updateVariants(attributes) {
        const tableBody = $("#variants-table tbody");
        tableBody.empty();

        // Tạo tất cả các kết hợp biến thể từ các thuộc tính đã nhập
        const combinations = getCombinations(attributes);

        combinations.forEach(combination => {
            const row = `<tr>
                <td>${combination.join(' - ')}</td>
                <td><input type="number" id="stockQuantity" name="stockQuantity" class="form-control" placeholder="Số lượng"></td>
                <td><input type="number" id="price" name="price" class="form-control" placeholder="Giá bán"></td>
                <td><input type="file" id="variant-images" class="form-control" accept="image/jpeg, image/png, image/gif, image/webp" multiple></td>
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
            const name = $(this).find("input[name='name']").val();
            const values = $(this).find("input[name='value']").val().split(',').map(value => value.trim());
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
                    <label for="name" class="form-label">Tên thuộc tính</label>
                    <input class="form-control" id="name" name="name" type="text" placeholder="Ví dụ: Màu sắc" />
                </div>
                <div class="col">
                    <label for="value" class="form-label">Giá trị thuộc tính</label>
                    <input class="form-control" id="value" name="value" type="text" placeholder="Ví dụ: Đỏ, Xanh, Vàng" />
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

    function getVariantForm() {
        const formValues = $("#from-create-variant").serializeArray();
        const variant = {};
        formValues.forEach(input => {
            variant[input.name] = input.value;
        });
        return variant;
    }

    function getAttributeForm() {
        const formValues = $("#from-create-attribute").serializeArray();
        const attribute = {};
        formValues.forEach(input => {
            attribute[input.name] = input.value;
        });
        return attribute;
    }

})