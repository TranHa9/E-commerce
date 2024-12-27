$(document).ready(function () {
    // Custom validator để kiểm tra ngày trong tương lai
    $.validator.addMethod("futureDate", function (value, element) {
        if (!value) return true;
        let currentDate = new Date();
        let inputDate = new Date(value);
        return inputDate > currentDate;
    }, "Hạn sử dụng phải là ngày trong tương lai");
    $('#form-edit-product').validate({
        rules: {
            name: {
                required: true,
                maxlength: 250,
            },
            categoryId: {
                required: true
            },
        },
        messages: {
            name: {
                required: "Vui lòng nhập tên sản phẩm",
                maxlength: "Tên sản phẩm không vượt quá 250 ký tự"
            },
            categoryId: {
                required: "Vui lòng chọn danh mục"
            },
        }
    });

    $('#form-eidt-info').validate({
        rules: {
            origin: {
                required: true
            },
            brand: {
                required: true
            },
            expiryDate: {
                date: true,
                futureDate: true
            }
        },
        messages: {
            origin: {
                required: "Vui lòng nhập xuất xứ"
            },
            brand: {
                required: "Vui lòng nhập thương hiệu"
            },
            expiryDate: {
                date: "Vui lòng nhập ngày hợp lệ",
                futureDate: "Hạn sử dụng phải là ngày trong tương lai"
            }
        }
    });

    $(document).on('click', '.edit-product-btn', async function () {
        const productId = $(this).data('id');
        const product = await getProductDetail(productId)
        $('#update-name').val(product.productName);
        $('#update-categoryId').val(product.categoryId);
        $('#update-description').val(product.description);
        $('#update-origin').val(product.origin);
        $('#update-brand').val(product.brand);
        $('#update-expiryDate').val(product.expiryDate);
        const imagePlaceholder = $(".image-container .image-placeholder");
        if (product.productImages && product.productImages.length > 0) {
            product.productImages.forEach(image => {
                const imgElement = `
                <div class="image-preview image-product">
                    <img class="img-product" src="/api/v1/files/product/${image}" alt="Image preview">
                    <div class="image-actions">
                        <button class="delete-btn">🗑️</button>
                    </div>
                </div>`;
                imagePlaceholder.append(imgElement);
            });
            $("#fas-icon").hide();
        } else {
            $("#fas-icon").show();
        }
        // Thêm sự kiện xóa ảnh sau khi ảnh đã được hiển thị
        imagePlaceholder.on('click', '.delete-btn', function () {
            $(this).closest('.image-preview.image-product').remove();
            if ($('.image-preview.image-product').length === 0) {
                $("#fas-icon").show();
            }
        });

        // Hiển thị thông tin thuộc tính
        const attributeSection = $('#update-attribute-section');
        attributeSection.empty();
        if (product.prices && product.prices.length > 0) {
            const attributeMap = {};
            product.prices.forEach(price => {
                price.variants.forEach(variant => {
                    if (!attributeMap[variant.name]) {
                        attributeMap[variant.name] = new Set();
                    }
                    attributeMap[variant.name].add(variant.value);
                });
            });

            // Hiển thị thuộc tính và các giá trị
            let attributeIndex = 1;
            for (const [name, values] of Object.entries(attributeMap)) {
                const valuesArray = Array.from(values).join(", ");
                const attributeHtml = `
                <div class="row gx-3 update-attribute-group">
                <div class="col-lg-5 mb-3">
                    <label for="update-attributeName" class="form-label">Thuộc tính ${attributeIndex}</label>
                    <input type="text" id="update-attributeName" name="attributeName" value="${name}"
                        class="form-control me-2"
                            placeholder="VD: Kích thước,...">
                    <p class="error update-attributeName"></p>
                </div>
                <div class="col-lg-5 mb-3">
                    <label for="update-attributeValue" class="form-label">Giá trị</label>
                        <input type="text" id="update-attributeValue" name="attributeValue" value="${valuesArray}"
                        class="form-control me-2"
                            placeholder="VD: M, L, XL (phân cách bởi dấu phẩy)">
                    <p class="error update-attributeValue"></p>
                </div>
                <div class="col mb-3">
                    <button class="btn-delete">Xóa</button>
                </div>
            </div>`;
                attributeSection.append(attributeHtml);
                attributeIndex++;
                updateAddButtonState();
            }
            // Vô hiệu hóa nút "Xóa" đầu tiên
            const firstDeleteButton = attributeSection.find('.btn-delete').first();
            firstDeleteButton.addClass('btn-disabled').prop('disabled', true);
        }

        // Hiển thị danh sách biến thể
        const variantBody = $('#update-variant-body');
        const variantHeader = $('#update-variant-header');

        variantBody.empty();
        variantHeader.empty();

        if (product.variants && product.variants.length > 0) {
            // Xây dựng header dựa trên thuộc tính
            const attributeNames = product.variants[0].attributes.map(attr => attr.name);
            let headerHtml = '';
            attributeNames.forEach(name => {
                headerHtml += `<th>${name}</th>`;
            });
            headerHtml += `<th>Số lượng</th><th>Giá bán</th><th>Ảnh</th><th>Trạng thái</th>`;
            variantHeader.html(headerHtml);

            // Xây dựng các dòng dữ liệu
            product.variants.forEach(variant => {
                const attributeValues = variant.attributes.map(attr => `<td>${attr.value}</td>`).join('');
                const imageUrl = variant.image || "";
                // Xử lý giá trị status
                const statusOptions = ["ACTIVE", "INACTIVE", "OUT_OF_STOCK"];
                const statusOptionsHtml = statusOptions.map(status => {
                    const selected = variant.status === status ? "selected" : "";  // Chọn trạng thái hiện tại
                    return `<option value="${status}" ${selected}>${status === "ACTIVE" ? "Hoạt động" : status === "INACTIVE" ? "Ngừng bán" : "Hết hàng"}</option>`;
                }).join('');

                const row = `
        <tr>
            ${attributeValues}
            <td>
                <input type="number" name="stockQuantity" class="form-control" placeholder="Số lượng"
                    value="${variant.stock_quantity}">
                <p class="error stockQuantity"></p>
            </td>
            <td>
                <input type="number" name="price" class="form-control" placeholder="Giá bán"
                    value="${variant.price}">
                <p class="error price"></p>
            </td>
            <td>
                <input type="file" name="imageUrl" class="form-control update-image-variant" placeholder="image-variant">
                <div class="d-flex gap-2 align-items-center justify-content-between">
                    <img class="image-preview img-sm mt-2" src="/api/v1/files/product/${imageUrl}" alt="Uploaded Image" style="display: ${imageUrl ? 'block' : 'none'};">
                    <button type="button" class="clear-button btn btn-danger btn-sm" style="display: ${imageUrl ? 'block' : 'none'};">Xóa ảnh</button>
                </div>
            </td>
            <td>
                <select class="form-select" id="update-status" name="status">
                    ${statusOptionsHtml}
                </select>
            </td>
        </tr>`;
                variantBody.append(row);
            });
        }

        //Cập nhật
        $("#btn-save-edit").click(function () {
            const isValidForm = $("#form-edit-product").valid();
            if (!isValidForm) {
                return;
            }
            let isValidImages = validateImageCount();
            if (!isValidImages) {
                return;
            }
            const isValidFromInfo = $("#form-edit-info").valid()
            if (!isValidFromInfo) {
                return;
            }
            const isValidVariant = validateAttributes()
            if (!isValidVariant) {
                return;
            }
            const isValid = validateVariants()
            if (!isValid) {
                return;
            }
            updateProduct(productId);

        })
    });

    function updateProduct(id) {
        let request = {
            name: $('#update-name').val(),
            categoryId: parseInt($('#update-categoryId').val()),
            description: $('#update-description').val(),
            origin: $('#update-origin').val(),
            brand: $('#update-brand').val(),
            expiryDate: $('#update-expiryDate').val(),
            prices: []
        };

        request.prices = getPriceVariants();
        request.variants = getVariantData()
        const imgPathArray = [];
        $(".image-preview.image-product img").each(function () {
            const imgPath = $(this).attr("src").replace('/api/v1/files/product/', '');
            imgPathArray.push(imgPath);
        });
        request.imageUrls = imgPathArray;
        $.ajax({
            url: `/api/v1/products/${id}`,
            type: 'PUT',
            data: JSON.stringify(request),
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                showToast("Cập nhât thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 2000)
            }
        });
    }

    function getPriceVariants() {
        let variants = [];
        $('#update-variant-body tr').each(function () {
            let variant = [];
            $(this).find('td').slice(0, -4).each(function (index) {
                variant.push({
                    name: $("#variant-header th").eq(index).text().trim(),
                    value: $(this).text().trim()
                });
            });

            let price = parseFloat($(this).find('[name="price"]').val());
            let stockQuantity = parseInt($(this).find('[name="stockQuantity"]').val());

            variants.push({
                variants: variant,
                price: price,
                stockQuantity: stockQuantity
            });
        });
        return variants;
    }

    function getVariantData() {
        let variants = [];
        $('#update-variant-body tr').each(function () {
            let attribute = [];
            $(this).find('td').slice(0, -4).each(function (index) {
                attribute.push({
                    name: $("#variant-header th").eq(index).text().trim(),
                    value: $(this).text().trim()
                });
            });
            let price = parseFloat($(this).find('[name="price"]').val());
            let stockQuantity = parseInt($(this).find('[name="stockQuantity"]').val());
            let status = ($(this).find('[name="status"]').val());
            let imageUrl = $(this).find('.image-preview').length > 0
                ? $(this).find('.image-preview').attr('src').replace('/api/v1/files/product/', '') : null;
            variants.push({
                attributes: attribute,
                stockQuantity: stockQuantity,
                price: price,
                imageUrl: imageUrl,
                status: status
            });

        });
        return variants;
    }

    async function getProductDetail(id) {
        try {
            return await $.ajax({
                url: `/api/v1/products/${id}`,
                type: 'GET',
                contentType: "application/json; charset=utf-8",
            });
        } catch (error) {
            throw new Error('Không thể lấy dữ liệu sản phẩm');
        }
    }

    $("#change-image-btn").click((event) => {
        event.preventDefault();
        $("#image-input").click();
        $("#avtar-error").text("");
    });

    $("#image-input").change(event => {
        const files = event.target.files;
        if (!files || files.length === 0) {
            $('#update-image-error').text('Ảnh không được để trống!');
            return;
        } else {
            $('#update-image-error').text('')
        }
        const maxSize = 10 * 1024 * 1024; // 10MB
        const imagePlaceholder = $(".image-container .image-placeholder");
        Array.from(files).forEach(file => {
            if (file.size > maxSize) {
                alert("Kích thước file không được vượt quá 10MB.");
                return;
            }
            const formData = new FormData();
            formData.append("files", file);

            $.ajax({
                url: '/api/v1/files',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    const imgElement = `
                    <div class="image-preview image-product">
                        <img class="img-product" src="/api/v1/files${response}" alt="Image preview">
                        <div class="image-actions">
                            <button class="delete-btn">🗑️</button>
                        </div>
                    </div>`;
                    imagePlaceholder.append(imgElement);
                    $("#fas-icon").hide()
                    $(".delete-btn").last().click(function () {
                        $(this).closest('.image-preview.image-product').remove();
                        if ($('.image-preview.image-product').length === 0) {
                            $("#fas-icon").show();
                        }
                    });
                },
            });
        });
    });

    function validateImageCount() {
        let isValid = true;
        const imageCount = $('.image-preview.image-product').length;
        if (imageCount === 0) {
            $('#update-image-error').text('Ảnh không được để trống!');
            isValid = false;
        } else {
            $('#update-image-error').text('');
        }
        if (imageCount > 9) {
            $('#update-image-error').text('Bạn chỉ có thể tải tối đa 9 ảnh');
            isValid = false;
        }
        return isValid;
    }

    function fetchCategoriesData() {
        $.ajax({
            url: '/api/v1/categories',
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                if (!data && data?.data.length === 0) {
                    $('#update-categoryId').append($('<option></option>').text("Chưa có dữ liệu"));
                }
                $('#update-categoryId').empty();
                data?.data.forEach(category => {
                    const option = $('<option></option>')
                        .val(category.id)
                        .text(category.name);
                    $('#update-categoryId').append(option);
                });
            },
        });
    }

    fetchCategoriesData()

    // Thêm thuộc tính mới
    $(document).on('click', '#update-add-attribute', function () {
        let attributeHtml = `
            <div class="row gx-3 update-attribute-group">
                <div class="col-lg-5 mb-3">
                    <label for="update-attributeName" class="form-label">Thuộc tính 2</label>
                    <input type="text" id="update-attributeName" name="attributeName"
                        class="form-control me-2"
                            placeholder="VD: Kích thước,...">
                    <p class="error" for="update-attributeName"></p>
                </div>
                <div class="col-lg-5 mb-3">
                    <label for="update-attributeValue" class="form-label">Giá trị</label>
                        <input type="text" id="update-attributeValue" name="attributeValue"
                        class="form-control me-2"
                            placeholder="VD: M, L, XL (phân cách bởi dấu phẩy)">
                    <p class="error" for="update-attributeValue"></p>
                </div>
                <div class="col mb-3">
                    <button class="btn-delete">Xóa</button>
                </div>
            </div>`;
        $('#update-attribute-section').append(attributeHtml);
        updateAddButtonState();
    });

    function validateAttributes() {
        let isValid = true;

        $('.update-attribute-group').each(function () {
            let attributeName = $(this).find('input[name="attributeName"]').val();
            let attributeValue = $(this).find('input[name="attributeValue"]').val();
            let attributeNameError = $(this).find('.error.update-attributeName');
            let attributeValueError = $(this).find('.error.update-attributeValue');
            if (!attributeName) {
                attributeNameError.text("Vui lòng nhâp tên thuộc tính");
                isValid = false;
            } else {
                attributeNameError.text("");
            }
            if (!attributeValue) {
                attributeValueError.text("Vui lòng nhập giá trị thuộc tính");
                isValid = false;
            } else {
                let validValue = /^[a-zA-Z0-9, ,đĐêÊáàảãạăắẳẵặâấầẩẫậíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵ]+$/;
                if (!validValue.test(attributeValue)) {
                    attributeValueError.text("Giá trị không hợp lệ (chỉ chứa chữ, số và dấu phẩy)");
                    isValid = false;
                } else {
                    attributeValueError.text("");
                }
            }
        });
        return isValid;
    }

    $('#update-attribute-section').on('focus', 'input', function () {
        $(this).closest('.update-attribute-group').find('p.error').text('');
        updateAddButtonState();
        resetVariantList();
    });

    function updateAddButtonState() {
        if ($('#update-attribute-section .update-attribute-group').length >= 2) {
            $('#update-add-attribute').addClass('btn-disabled').prop('disabled', true);
        } else {
            $('#update-add-attribute').removeClass('btn-disabled').prop('disabled', false);
        }
    }

    function resetVariantList() {
        $('#update-variant-header').html('<th>Thuộc tính</th><th>Số lượng</th><th>Giá bán</th>');
        $('#update-variant-body').html('');
    }

    $(document).on('click', '.btn-delete', function () {
        $(this).closest('.update-attribute-group').remove();
        updateAddButtonState();
        resetVariantList();
    });

    $('#update-save-attributes').click(function () {
        const isValid = validateAttributes()
        if (!isValid) {
            return;
        }
        $('#btn-save-edit').removeClass('btn-disabled').prop('disabled', false);
        let headers = [];
        let attributes = [];
        let variants = [];

        $('.update-attribute-group').each(function () {
            let attributeName = $(this).find('input').eq(0).val();
            let attributeValues = $(this).find('input').eq(1).val().split(',').map(val => val.trim());
            attributes.push({name: attributeName, values: attributeValues});
        });

        // Tạo header
        attributes.forEach(attribute => {
            headers.push(`<th>${attribute.name}</th>`);
        });

        headers.push('<th>Số lượng</th>', '<th>Giá bán</th>', '<th>ảnh</th>');
        $('#update-variant-header').html(headers.join(''));

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
                    <td>
                        <input type="number" name="stockQuantity" class="form-control" placeholder="Số lượng">
                        <p class="error stockQuantity"></p>
                    </td>;
                    <td>
                        <input type="number" name="price" class="form-control" placeholder="Giá bán">
                        <p class="error price"></p>
                    </td>;
                    <td>
                        <input type="file" name="imageUrl" class="form-control update-image-variant" placeholder="image-variant">
                        <div class="d-flex gap-2 align-items-center justify-content-between">
                        <img class="image-preview img-sm mt-2" src="" alt="Uploaded Image" style="display: none;">
                        <button type="button" class="clear-button btn btn-danger btn-sm" style="display: none">Xóa ảnh</button>
                        </div>
                    </td>
                    <td>
                        <select class="form-select" id="update-status" name="status">
                            <option value="ACTIVE">Hoạt động</option>
                            <option value="INACTIVE">Ngừng bán</option>
                            <option value="OUT_OF_STOCK">Hết hàng</option>
                           </select>
                    </td>
                    `;
            rows += `<tr>${row}</tr>`;
        });

        $('#update-variant-body').html(rows);

    });

    function validateVariants() {
        let isValid = true;

        $('#update-variant-body tr').each(function () {
            let stockQuantity = $(this).find('input[name="stockQuantity"]').val();
            let price = $(this).find('input[name="price"]').val();
            let stockQuantityError = $(this).find('.error.stockQuantity');
            let priceError = $(this).find('.error.price');
            if (!stockQuantity) {
                stockQuantityError.text("Vui lòng nhâp số lượng");
                isValid = false;
            } else if (!/^[1-9][0-9]*$/.test(stockQuantity)) {
                stockQuantityError.text("Số lượng phải là số nguyên dương lớn hơn 0");
                isValid = false;
            } else {
                stockQuantityError.text("");
            }
            if (!price) {
                priceError.text("Vui lòng nhập giá");
                isValid = false;
            } else if (!/^\d+(\.\d+)?$/.test(price) || parseFloat(price) <= 1000) {
                priceError.text("Giá phải là số lớn hơn 1000");
                isValid = false;
            } else {
                priceError.text("");
            }
        });
        return isValid;
    }

    $('#update-variant-body').on('focus', 'input', function () {
        $(this).closest('tr').find('p.error').text('');
    });

    //Gọi API lấy link ảnh
    $(document).on('change', '.update-image-variant', function (event) {
        let fileInput = $(event.target);
        const file = event.target.files[0];
        let previewImage = fileInput.closest('td').find('.image-preview');
        let clearButton = fileInput.closest('td').find('.clear-button');
        if (!file) {
            previewImage.attr("src", "").hide();
            clearButton.hide();
            return;
        }
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            alert('Vui lòng chọn file ảnh (jpg, png, gif, webp).');
            return;
        }
        const maxSize = 10 * 1024 * 1024; // 10MB
        if (file.size > maxSize) {
            alert("Kích thước file không được vượt quá 10MB.");
            return;
        }
        const formData = new FormData();
        formData.append('files', file);

        $.ajax({
            url: '/api/v1/files',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                previewImage.attr('src', "/api/v1/files" + response).show();
                clearButton.show();
            },
        });
    });

    // Gắn sự kiện xóa ảnh
    $(document).on('click', '.clear-button', function () {
        const row = $(this).closest('tr');
        row.find('.image-preview').attr('src', '').hide();
        row.find('.clear-button').hide();
        row.find('input[name="imageUrl"]').val('');
    });
})