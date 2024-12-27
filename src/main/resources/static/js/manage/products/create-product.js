$(document).ready(function () {
    let shop;
    (async function () {
        shop = await fetchShopData();
    })();
    // Custom validator để kiểm tra ngày trong tương lai
    $.validator.addMethod("futureDate", function (value, element) {
        if (!value) return true;
        let currentDate = new Date();
        let inputDate = new Date(value);
        return inputDate > currentDate;
    }, "Hạn sử dụng phải là ngày trong tương lai");

    $('#form-create-product').validate({
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

    $('#form-create-info').validate({
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

    $("#change-avatar-btn").click((event) => {
        event.preventDefault();
        $("#avatar-input").click();
        $("#image-error").text("");
    });
    $("#avatar-input").change(event => {
        const files = event.target.files;
        if (!files || files.length === 0) {
            $('#image-error').text('Ảnh không được để trống!');
            return;
        } else {
            $('#image-error').text('')
        }
        const maxSize = 10 * 1024 * 1024; // 10MB
        const imagePlaceholder = $(".image-preview-container .image-placeholder");
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
                    <div class="image-preview create-image-product">
                        <img class="img-product" src="/api/v1/files${response}" alt="Image preview">
                        <div class="image-actions">
                            <button class="delete-btn">🗑️</button>
                        </div>
                    </div>`;
                    imagePlaceholder.append(imgElement);
                    $(".fas.fa-image").hide()
                    $(".delete-btn").last().click(function () {
                        $(this).closest('.image-preview.create-image-product').remove();
                        if ($('.image-preview.create-image-product').length === 0) {
                            $(".fas.fa-image").show();
                        }
                    });
                },
            });
        });
    });

    function validateImageCount() {
        let isValid = true;
        const imageCount = $('.image-preview.create-image-product').length;
        if (imageCount === 0) {
            $('#image-error').text('Ảnh không được để trống!');
            isValid = false;
        } else {
            $('#image-error').text('');
        }
        if (imageCount > 9) {
            $('#image-error').text('Bạn chỉ có thể tải tối đa 9 ảnh');
            isValid = false;
        }
        return isValid;
    }

    $("#btn-save-create").click(async function () {
        const isValid = validateVariants()
        if (!isValid) {
            return;
        }
        let request = {
            name: $('#name').val(),
            categoryId: parseInt($('#categoryId').val()),
            description: $('#description').val(),
            origin: $('#origin').val(),
            brand: $('#brand').val(),
            expiryDate: $('#expiryDate').val(),
            prices: []
        };
        if (shop) {
            request.shopId = shop.id;
        }

        request.prices = getPriceVariants();
        request.variants = getVariantData()
        const imgPathArray = [];
        $(".image-preview.create-image-product img").each(function () {
            const imgPath = $(this).attr("src").replace('/api/v1/files/product/', '');
            imgPathArray.push(imgPath);
        });
        request.imageUrls = imgPathArray;
        $.ajax({
            url: `/api/v1/products`,
            type: 'POST',
            data: JSON.stringify(request),
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                showToast("Tạo mới thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 2000)
            }
        });
    })

    function getPriceVariants() {
        let variants = [];
        $('#variant-body tr').each(function () {
            let variant = [];
            $(this).find('td').slice(0, -3).each(function (index) {
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
        $('#variant-body tr').each(function () {
            let attribute = [];
            $(this).find('td').slice(0, -3).each(function (index) {
                attribute.push({
                    name: $("#variant-header th").eq(index).text().trim(),
                    value: $(this).text().trim()
                });
            });
            let price = parseFloat($(this).find('[name="price"]').val());
            let stockQuantity = parseInt($(this).find('[name="stockQuantity"]').val());
            let imageUrl = $(this).find('.image-preview').length > 0
                ? $(this).find('.image-preview').attr('src').replace('/api/v1/files/product/', '') : null;
            variants.push({
                attributes: attribute,
                stockQuantity: stockQuantity,
                price: price,
                imageUrl: imageUrl
            });

        });
        return variants;
    }

    function fetchCategoriesData() {
        $.ajax({
            url: '/api/v1/categories',
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                if (!data && data?.data.length === 0) {
                    $('#categoryId').append($('<option></option>').text("Chưa có dữ liệu"));
                }
                $('#categoryId').empty();
                data?.data.forEach(category => {
                    const option = $('<option></option>')
                        .val(category.id)
                        .text(category.name);
                    $('#categoryId').append(option);
                });
            },
        });
    }

    fetchCategoriesData()
    // Thêm thuộc tính mới
    $('#add-attribute').click(function () {
        let attributeHtml = `
            <div class="row gx-3 attribute-group">
                <div class="col-lg-5 mb-3">
                    <label for="attributeName" class="form-label">Thuộc tính 2</label>
                    <input type="text" id="attributeName" name="attributeName"
                        class="form-control me-2"
                            placeholder="VD: Kích thước,...">
                    <p class="error attributeName"></p>
                </div>
                <div class="col-lg-5 mb-3">
                    <label for="attributeValue" class="form-label">Giá trị</label>
                        <input type="text" id="attributeValue" name="attributeValue"
                        class="form-control me-2"
                            placeholder="VD: M, L, XL (phân cách bởi dấu phẩy)">
                    <p class="error attributeValue"></p>
                </div>
                <div class="col mb-3">
                    <button class="btn-delete">Xóa</button>
                </div>
            </div>`;
        $('#attribute-section').append(attributeHtml);
        updateAddButtonState();
    });

    function validateAttributes() {
        let isValid = true;

        $('.attribute-group').each(function () {
            let attributeName = $(this).find('input[name="attributeName"]').val();
            let attributeValue = $(this).find('input[name="attributeValue"]').val();
            let attributeNameError = $(this).find('.error.attributeName');
            let attributeValueError = $(this).find('.error.attributeValue');
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

    $('#attribute-section').on('focus', 'input', function () {
        $(this).closest('.attribute-group').find('p.error').text('');
        updateAddButtonState();
        resetVariantList();
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
        updateAddButtonState();
        resetVariantList();
    });

    $('#save-attributes').click(function () {
        const isValid = validateAttributes()
        if (!isValid) {
            return;
        }
        $('#btn-save-create').removeClass('btn-disabled').prop('disabled', false);
        let headers = [];
        let attributes = [];
        let variants = [];

        $('.attribute-group').each(function () {
            let attributeName = $(this).find('input').eq(0).val();
            let attributeValues = $(this).find('input').eq(1).val().split(',').map(val => val.trim());
            attributes.push({name: attributeName, values: attributeValues});
        });

        // Tạo header
        attributes.forEach(attribute => {
            headers.push(`<th>${attribute.name}</th>`);
        });

        headers.push('<th>Số lượng</th>', '<th>Giá bán</th>', '<th>ảnh</th>');
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
                    <td>
                        <input type="number" name="stockQuantity" class="form-control" placeholder="Số lượng">
                        <p class="error stockQuantity"></p>
                    </td>;
                    <td>
                        <input type="number" name="price" class="form-control" placeholder="Giá bán">
                        <p class="error price"></p>
                    </td>;
                    <td>
                        <input type="file" name="imageUrl" class="form-control create-image-variant" placeholder="image-variant">
                        <div class="d-flex gap-2 align-items-center justify-content-between">
                        <img class="image-preview img-sm mt-2" src="" alt="Uploaded Image" style="display: none;">
                        <button type="button" class="crate-clear-button" style="display: none">Xóa ảnh</button>
                        </div>
                    </td>`;
            rows += `<tr>${row}</tr>`;
        });

        $('#variant-body').html(rows);

    });

    function validateVariants() {
        let isValid = true;

        $('#variant-body tr').each(function () {
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

    $('#variant-body').on('focus', 'input', function () {
        $(this).closest('tr').find('p.error').text('');
    });

    //Step
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
    $('#next-btn').click(function (e) {
        const isValidForm = $("#form-create-product").valid();
        if (!isValidForm) {
            return;
        }
        let isValidImages = validateImageCount();
        if (!isValidImages) {
            return;
        }
        const isValidFromInfo = $("#form-create-info").valid()
        if (!isValidFromInfo) {
            return;
        }
        if (currentStep < $('.step-content').length - 1) {
            currentStep++;
            updateStep();
        }
    });
    updateStep();

    //Gọi API lấy link ảnh
    $(document).on('change', '.create-image-variant', function (event) {
        let fileInput = $(event.target);
        const file = event.target.files[0];
        let previewImage = fileInput.closest('td').find('.image-preview');
        let clearButton = fileInput.closest('td').find('.create-clear-button');
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
    // Xóa ảnh và làm trống input khi nhấn nút xóa
    $(document).on('click', '.create-clear-button', function (event) {
        let button = $(event.target);
        let fileInput = button.closest('td').find('input[type="file"]');
        let previewImage = button.closest('td').find('.image-preview');
        fileInput.val('');
        previewImage.hide();
        button.hide();
    });
})