$(document).ready(async function () {
    const urlParts = window.location.pathname.split('/');
    const productId = urlParts[urlParts.length - 1];
    let firstAttribute = "";
    let endAttribute = "";

    const productData = await $.ajax({
        url: `/api/v1/products/${productId}`,
        method: "GET",
        dataType: "json"
    });
    const shop = await getDataShop(productData.shopId);
    console.log(productData);
    console.log(shop);

    // Đặt giá trị ban đầu
    let selectedColor = null;
    let selectedSize = null;
    // Cập nhật tiêu đề và nhà cung cấp
    $('#product-name').text(productData.productName);
    $(".byAUthor").text(productData.shopName).attr("href");
    // Cập nhật giá
    $("#min-price").text(`${formatCurrency(productData.minPrice)}đ`);
    if (productData.minPrice !== productData.maxPrice) {
        $("#max-price").text(` từ ${formatCurrency(productData.maxPrice)}đ`);
    }
    // Cập nhật số lượng đánh giá
    $(".rating span").text(productData.averageRating);
    $("#soldQuantity-info").text(productData.soldQuantity)
    firstAttribute = productData.variants[0].attributes[0].name;
    $("#attributes-first-name").text(firstAttribute + ':');
    const uniqueColors = new Set();
    const colorOptions = $('#color-options')
    productData.variants.forEach(variant => {
        const color = variant.attributes.find(attr => attr.name === firstAttribute);
        if (color && variant.image && !uniqueColors.has(color.value)) {
            uniqueColors.add(color.value);
            const li = $('<li class="color-option d-flex flex-column justify-content-between"></li>');
            li.html(`<img src="/api/v1/files/product/${variant.image || ''}" alt="${color.value}" title="${color.value}"><span>${color.value}</span>`);
            li.data("color", color.value);
            colorOptions.append(li);
        }
    });
    productData.variants.forEach(variant => {
        const color = variant.attributes.find(attr => attr.name === firstAttribute);
        if (color && !variant.image && !uniqueColors.has(color.value)) {
            uniqueColors.add(color.value);
            const li = $('<li class="color-option d-flex align-items-end"></li>');
            li.html(`<span>${color.value}</span>`);
            li.data("color", color.value);
            colorOptions.append(li);
        }
    });

    if (productData.variants[0].attributes[1]?.name) {
        endAttribute = productData.variants[0].attributes[1].name;
        $("#attributes-end-name").text(endAttribute + ':');
        const sizeOptions = $("#size-options")
        const uniqueSizes = new Set();
        productData.variants.forEach(variant => {
            const size = variant.attributes.find(attr => attr.name === endAttribute);
            if (size && !uniqueSizes.has(size.value)) {
                uniqueSizes.add(size.value);
                const li = $('<li class="size-option"></li>');
                li.html(`<span title=${size.value}>${size.value}</span>`);
                li.data("size", size.value);
                sizeOptions.append(li);
            }
        });
    }
    $("#category-info").text(productData.categoryName)
    $("#brand-info").text(productData.brand)
    $("#origin-info").text(productData.origin)
    productData.expiryDate ? $("#expiryDate-info").text(productData.origin) : $("#expiryDate-info").text("Không có")
    $("#text-description").append(`<p>${productData.description}</p>`)

    //Thông tin shop
    if (shop && shop.logo) {
        $(".vendor-logo img").attr("src", `/api/v1/files/logo/${shop.logo}`);
    } else {
        $(".vendor-logo img").attr("src", `/img/page/product/futur.png`);
    }
    $(".vendor-name h6").html(`<a href="#">${shop.name}</a>`);
    $("#contact-address").text(shop.address)
    $("#contact-phone").text(shop.user.phone)
    $("#tab-vendor").append(`<p>${shop.description}</p>`)

    // Xử lý sự kiện chọn màu sắc
    $('#color-options').on('click', '.color-option', function () {
        $('#color-options .color-option').removeClass('active');
        $(this).addClass('active');
        selectedColor = $(this).data("color");
        checkSelections();
    });

    // Xử lý sự kiện chọn kích thước
    $('#size-options').on('click', '.size-option', function () {
        $('#size-options .size-option').removeClass('active');
        $(this).addClass('active');
        selectedSize = $(this).data("size");
        checkSelections();
    });

    // Kiểm tra lựa chọn và cập nhật thông tin
    function checkSelections() {
        // Nếu chỉ có 1 thuộc tính (màu sắc)
        if (selectedColor && !endAttribute) {
            unlockQuantityInput();
            const selectedVariant = productData.variants.find(variant =>
                variant.attributes.some(attr => attr.name === firstAttribute && attr.value === selectedColor)
            );
            if (selectedVariant) {
                $("#min-price").text(`${formatCurrency(selectedVariant.price)}đ`);
                $("#stock-quantity").text(`Số lượng: ${selectedVariant.stock_quantity} sản phẩm có sẵn`)
                $("#max-price").text('');
            }
        }

        // Nếu có 2 thuộc tính (màu sắc và kích thước)
        if (selectedColor && selectedSize) {
            const selectedVariant = productData.variants.find(variant =>
                variant.attributes.some(attr => attr.name === firstAttribute && attr.value === selectedColor) &&
                variant.attributes.some(attr => attr.name === endAttribute && attr.value === selectedSize)
            );
            unlockQuantityInput();
            if (selectedVariant) {
                $("#min-price").text(`${formatCurrency(selectedVariant.price)}đ`);
                $("#stock-quantity").text(`Số lượng: ${selectedVariant.stock_quantity} sản phẩm có sẵn`)
                $("#max-price").text('');
            }
        }
    }

    // Hàm mở khóa khi người dùng chọn đầy đủ biến thể
    function unlockQuantityInput() {
        $(".input-quantity input").prop("disabled", false);
        $(".minus-cart, .plus-cart").removeClass("disabled");
    }

    async function getDataShop(id) {
        try {
            return await $.ajax({
                url: `/api/v1/shops/${id}`,
                method: "GET",
                dataType: "json"
            });
        } catch (error) {
            console.error("Error fetching shop data:", error);
            return {};
        }
    }

    $(".minus-cart").on("click", function () {
        var _parent = $(this).parents(".input-quantity");
        var _currentInput = _parent.find("input");
        var _number = parseInt(_currentInput.val());
        if (_number > 1) {
            _number = _number - 1;
        }
        _currentInput.val(_number);
    });
    $(".plus-cart").on("click", function () {
        var _parent = $(this).parents(".input-quantity");
        var _currentInput = _parent.find("input");
        var _number = parseInt(_currentInput.val());
        // Lấy số lượng tồn kho (stock_quantity) từ UI
        var stockQuantity = parseInt($("#stock-quantity").text().replace(/[^0-9]/g, ""));
        if (_number >= stockQuantity) {
            alert(`Không thể thêm số lượng vượt quá ${stockQuantity}.`);
            return;
        }

        _number = _number + 1;
        _currentInput.val(_number);
    });

    // Thêm vào giỏ hàng
    $(".button-buy .btn-cart").click(function () {
        if (!selectedColor && !selectedSize) {
            alert("Vui lòng chọn các thuộc tính, trước khi thêm vào giỏ hàng.");
            return;
        }
        // Kiểm tra nếu chỉ có màu sắc hoặc kích thước được chọn
        if (!selectedColor && endAttribute) {
            alert("Vui lòng chọn màu sắc trước khi thêm vào giỏ hàng.");
            return;
        }

        if (!selectedSize && endAttribute) {
            alert("Vui lòng chọn kích thước trước khi thêm vào giỏ hàng.");
            return;
        }
        // Lấy giá của biến thể đã chọn
        let selectedVariant = null;
        if (selectedColor && !selectedSize) {
            selectedVariant = productData.variants.find(variant =>
                variant.attributes.some(attr => attr.name === firstAttribute && attr.value === selectedColor)
            );
        }

        if (selectedColor && selectedSize) {
            selectedVariant = productData.variants.find(variant =>
                variant.attributes.some(attr => attr.name === firstAttribute && attr.value === selectedColor) &&
                variant.attributes.some(attr => attr.name === endAttribute && attr.value === selectedSize)
            );
        }

        if (!selectedVariant) {
            alert("Biến thể không hợp lệ.");
            return;
        }

        const quantity = $(".input-quantity input").val();
        const unitPrice = selectedVariant.price;
        const totalPrice = unitPrice * quantity;

        const cartItem = {
            productId: productData.id,
            quantity: parseInt(quantity),
            unitPrice: unitPrice,
            totalPrice: totalPrice,
            attributes: []
        };
        // Thêm thuộc tính vào giỏ hàng
        if (selectedColor) {
            cartItem.attributes.push({name: firstAttribute, value: selectedColor});
        }
        if (selectedSize) {
            cartItem.attributes.push({name: endAttribute, value: selectedSize});
        }
        console.log("Thêm vào giỏ hàng:", cartItem);
    });

    // Mua ngay
    $(".button-buy .btn-buy").click(function () {
        if (!selectedColor || !selectedSize) {
            alert("Vui lòng chọn các thuộc tính, trước khi mua.");
            return;
        }
        const selectedVariant = {
            [firstAttribute]: selectedColor,
            [endAttribute]: selectedSize,
            quantity: $("#quantity").val()
        };
        console.log("Mua ngay:", selectedVariant);
    });

})