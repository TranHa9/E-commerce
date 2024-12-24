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
            const firstImage = product.productImages.length > 0 ? product.productImages[0] : null;

            const row = `<tr>
                <td width="40%"><a class="itemside" href="#">
                    <div class="left"><img class="img-sm img-avatar" 
                    src="${firstImage ? `/api/v1/files/product/${firstImage}` : '/img/people/avatar-default.jpg'}"
                    alt="avatar"></div>
                    <div class="info pl-3">
                        <h6 class="mb-0 title">${product.productName}</h6><small class="text-muted">User ID: #${product.id}</small>
                    </div>
                </a></td>
                <td>${product.minPrice}</td>
                <td>${product.maxPrice}</td>
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
        let request = {
            name: $('#name').val(),
            categoryId: parseInt($('#categoryId').val()),
            description: $('#description').val(),
            origin: $('#origin').val(),
            brand: $('#brand').val(),
            expiryDate: $('#expiryDate').val(),
            prices: []
        };
        const formProduct = new FormData();
        if (shopId) {
            request.shopId = shopId;
        }

        request.prices = collectVariants();

        // Thêm toàn bộ thông tin của request vào FormData
        formProduct.append('request', JSON.stringify(request));

        const files = $("#avatar-input")[0].files;
        if (files.length > 0) {
            Array.from(files).forEach(file => {
                formProduct.append('images', file, file.name);
            });
        }
        console.log("attributes", getVariantData())
        console.log("Payload:", request);
        $.ajax({
            url: `/api/v1/products`,
            type: 'POST',
            //enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formProduct,
            success: function (response) {
                showToast("Lưu thành công", "success");
            }
        });
    })

    function collectVariants() {
        let variants = [];
        $('#variant-body tr').each(function () {
            let variant = [];
            $(this).find('td').slice(0, -2).each(function (index) {
                variant.push({
                    name: $("#variant-header th").eq(index).text().trim(),
                    value: $(this).text().trim()
                });
            });

            let price = parseFloat($(this).find('[name="price"]').val());
            let stockQuantity = parseInt($(this).find('[name="stockQuantity"]').val());

            variants.push({
                variant: variant,
                price: price,
                stockQuantity: stockQuantity
            });
        });
        return variants;
    }

    function getVariantData() {
        let variants = [];
        $('#variant-body tr').each(function () {
            let attribute = [];
            $(this).find('td').slice(0, -2).each(function (index) {
                attribute.push({
                    name: $("#variant-header th").eq(index).text().trim(),
                    value: $(this).text().trim()
                });
            });
            let stockQuantity = parseInt($(this).find('[name="stockQuantity"]').val());

            variants.push({
                attributes: attribute,
                stockQuantity: stockQuantity
            });

        });
        return variants;
    }


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


    // Thêm thuộc tính mới
    $('#add-attribute').click(function () {
        let attributeHtml = `
            <div class="row gx-3 attribute-group">
                <div class="col-lg-5 mb-3">
                    <label for="attributeName" class="form-label">Thuộc tính 2</label>
                    <input type="text" id="attributeName" name="attributeName"
                        class="form-control me-2"
                            placeholder="VD: Kích thước,...">
                </div>
                <div class="col-lg-5 mb-3">
                    <label for="attributeValue" class="form-label">Giá trị</label>
                        <input type="text" id="attributeValue" name="attributeValue"
                        class="form-control me-2"
                            placeholder="VD: M, L, XL (phân cách bởi dấu phẩy)">
                </div>
                <div class="col mb-3">
                <input type="radio" name="attribute-radio" value="2">
                    <button class="btn-delete">Xóa</button>
                </div>
            </div>`;
        $('#attribute-section').append(attributeHtml);
        updateAddButtonState();
    });

    function updateAddButtonState() {
        if ($('#attribute-section .attribute-group').length >= 2) {
            $('#add-attribute').addClass('btn-disabled').prop('disabled', true);
        } else {
            $('#add-attribute').removeClass('btn-disabled').prop('disabled', false);
        }
    }

    function resetVariantList() {
        $('#variant-header').html('<th>Thuộc tính</th><th>Số lượng</th><th>Giá bán</th>');
        $('#variant-body').html('');
    }

    $(document).on('click', '.btn-delete', function () {
        $(this).closest('.attribute-group').remove();
        $('#image-update-section #image-body ').empty()
        $('#image-update-section').hide();
        updateAddButtonState();
        resetVariantList();
    });

    $('#save-attributes').click(function () {
        let headers = [];
        let attributes = [];
        let variants = [];
        let radioValue = "1";

        $('.attribute-group').each(function () {
            let attributeName = $(this).find('input').eq(0).val();
            let attributeValues = $(this).find('input').eq(1).val().split(',').map(val => val.trim());
            attributes.push({name: attributeName, values: attributeValues});
        });

        // Tạo header
        attributes.forEach(attribute => {
            headers.push(`<th>${attribute.name}</th>`);
        });

        headers.push('<th>Số lượng</th>', '<th>Giá bán</th>');
        $('#variant-header').html(headers.join(''));

        // Hàm tạo tổ hợp
        function generateCombinations(index, current) {
            if (index === attributes.length) {
                variants.push([...current]);
                return;
            }
            attributes[index].values.forEach(value => {
                current.push(value);
                generateCombinations(index + 1, current);
                current.pop();
            });
        }

        generateCombinations(0, []);

        let rows = '';
        variants.forEach(variant => {
            let row = '';
            variant.forEach((value) => {
                row += `<td>${value}</td>`;
            });
            row += `
                    <td><input type="number" name="stockQuantity" class="form-control" placeholder="Số lượng"></td>
                    <td><input type="number" name="price" class="form-control" placeholder="Giá bán"></td>`;
            rows += `<tr>${row}</tr>`;
        });

        $('#variant-body').html(rows);

        radioValue = $("input[name='attribute-radio']:checked").val();
        if (radioValue === "1") {
            $('#image-update-section').show();

            let rowImages = '';
            attributes[0].values.forEach(value => {
                rowImages += `
                    <tr>
                        <td>${value}</td>
                        <td>
                            <input type="file" class="form-control image-upload" accept="image/*" />
                            <div class="image-preview" style="display: none;"></div>
                        </td>
                        <td><button class="btn-delete-image">Xóa</button></td>
                    </tr>
                `;
            });

            $('#image-body').html(rowImages);

            // Xử lý sự kiện tải ảnh
            $('.image-upload').change(function () {
                let file = this.files[0];
                let previewContainer = $(this).closest('td').find('.image-preview');

                if (file) {
                    let reader = new FileReader();
                    reader.onload = function (e) {
                        previewContainer.html('<img src="' + e.target.result + '" alt="Image Preview" style="width: 100px; height: auto;"/>');
                        previewContainer.show();
                    };
                    reader.readAsDataURL(file);
                }
            });

            // Xử lý sự kiện xóa ảnh
            $('.btn-delete-image').click(function () {
                let row = $(this).closest('tr');
                row.find('.image-upload').val('');  // Xóa ảnh đã tải lên
                row.find('.image-preview').hide(); // Ẩn ảnh đã tải lên
            });
        } else if (radioValue === "2") {
            $('#image-update-section').show();
            let rowImages = '';
            attributes[1].values.forEach(value => {
                rowImages += `
                    <tr>
                        <td>${value}</td>
                        <td>
                            <input type="file" class="form-control image-upload" accept="image/*" />
                            <div class="image-preview" style="display: none;"></div>
                        </td>
                        <td><button class="btn-delete-image">Xóa</button></td>
                    </tr>
                `;
            });

            $('#image-body').html(rowImages);

            // Xử lý sự kiện tải ảnh
            $('.image-upload').change(function () {
                let file = this.files[0];
                let previewContainer = $(this).closest('td').find('.image-preview');

                if (file) {
                    let reader = new FileReader();
                    reader.onload = function (e) {
                        previewContainer.html('<img src="' + e.target.result + '" alt="Image Preview" style="width: 100px; height: auto;"/>');
                        previewContainer.show();
                    };
                    reader.readAsDataURL(file);
                }
            });

            // Xử lý sự kiện xóa ảnh
            $('.btn-delete-image').click(function () {
                let row = $(this).closest('tr');
                row.find('.image-upload').val('');  // Xóa ảnh đã tải lên
                row.find('.image-preview').hide(); // Ẩn ảnh đã tải lên
            });
        }

    });

    let currentStep = 0;

    function updateStep() {
        $('.step-content.active').fadeOut(300, function () {
            $(this).removeClass('active');
            $(`#content-${currentStep + 1}`).fadeIn(300).addClass('active');
        });
        $('.step').removeClass('active');
        $('.step-line').removeClass('active');

        $(`#step-${currentStep + 1}`).addClass('active');

        for (let i = 1; i <= currentStep; i++) {
            $(`#step-${i}`).addClass('active');
            $(`#line-${i}`).addClass('active');
        }

        $('#back-btn').prop('disabled', currentStep === 0);
        $('#next-btn').prop('disabled', currentStep === $('.step-content').length - 1);
    }

    $('#back-btn').click(function () {
        if (currentStep > 0) {
            currentStep--;
            updateStep();
        }
    });
    $('#next-btn').click(function () {
        if (currentStep < $('.step-content').length - 1) {
            currentStep++;
            updateStep();
        }
    });
    updateStep();
})