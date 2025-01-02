$(document).ready(function () {
    const user = JSON.parse(localStorage.getItem("user"));
    let fileUrl = "/api/v1/files/product/";
    let totalPrice = 0;

    function fetchCartItems() {
        $.ajax({
            url: `/api/v1/carts/${user.id}`,
            type: 'GET',
            dataType: 'json',
            success: function (response) {
                console.log(response)
                // Xóa nội dung cũ
                $('#cart-items').empty();
                // Kiểm tra nếu có sản phẩm
                if (response.cartItems && response.cartItems.length > 0) {
                    response.cartItems.forEach(cartItem => {
                        let variantsHTML = '';
                        if (cartItem.variants && cartItem.variants.length > 0) {
                            cartItem.variants.forEach(variant => {
                                variantsHTML += `
                                        <div class="d-flex align-items-center gap-1 mb-5">
                                            <span>${variant.name}:</span>
                                            <span>${variant.value}</span>
                                        </div>
                                `;
                            });
                        }

                        // Tạo thông báo nếu sản phẩm bị sửa hoặc ngừng bán
                        let updatedMessage = cartItem.isUpdated ? `<p class="alert alert-warning">Sản phẩm đã bị sửa. Vui lòng chọn lại sản phẩm.</p>` : '';
                        let inactiveMessage = cartItem.isInactive ? `<p class="alert alert-danger">Sản phẩm đã ngừng bán. Vui lòng chọn sản phẩm khác.</p>` : '';
                        // Kiểm tra nếu sản phẩm có thông báo, áp dụng lớp disabled
                        let isDisabledClass = cartItem.isUpdated || cartItem.isInactive ? 'disabled' : '';
                        let checkboxHTML = '';
                        if (!cartItem.isUpdated && !cartItem.isInactive) {
                            checkboxHTML += `
                                <div class="wishlist-cb">
                                    <input class="cb-layout cb-select" type="checkbox">
                                </div>
                            `
                        }
                        $('#cart-items').append(`
                            <div class="item-wishlist ${isDisabledClass}" data-maxQuantity="${cartItem.maxQuantity}">
                                ${checkboxHTML}
                                <div class="wishlist-product">
                                    <div class="product-wishlist">
                                        <div class="product-image">
                                            <a href="#"><img src="${fileUrl}${cartItem.product.imageUrls[0]}" alt="${cartItem.product.name}"></a>
                                        </div>
                                        <div class="product-info">
                                            <a href="#">
                                                <h6 class="color-brand-3">${cartItem.product.name}</h6>
                                            </a>
                                             <div class="rating">
                                                ${variantsHTML}
                                             </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="wishlist-price">
                                    <h4 class="color-brand-3">${formatCurrency(cartItem.unitPrice)}đ</h4>
                                </div>
                                <div class="wishlist-status">
                                    <div class="box-quantity">
                                        <div class="input-quantity">
                                            <input class="font-xl color-brand-3" type="text" value="${cartItem.quantity}">
                                            <span class="minus-cart"></span>
                                            <span class="plus-cart"></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="wishlist-action">
                                    <h4 class="color-brand-3">${formatCurrency(cartItem.totalPrice)}đ</h4>
                                </div>
                                <div class="wishlist-remove">
                                    <a class="btn btn-delete" href="#" data-id="${cartItem.id}">Xóa</a>
                                </div>                              
                            </div>
                            ${updatedMessage}
                            ${inactiveMessage}
                        `);
                    });
                } else {
                    $('#cart-items').append('<p>Giỏ hàng của bạn đang trống.</p>');
                }
            },
            error: function (error) {
                console.error('Lỗi khi tải dữ liệu giỏ hàng:', error);
            }
        });
    }

    // Gọi hàm khi trang tải xong
    fetchCartItems();

    // Thêm sự kiện cho nút "Tải lại"
    $('.update-cart').on('click', function (e) {
        e.preventDefault();
        fetchCartItems();
    });

    $(".cb-all").on("change", function () {
        const isChecked = $(this).is(":checked");
        $(".cb-select").prop("checked", isChecked);

        totalPrice = 0; // Reset tổng tiền
        if (isChecked) {
            $(".cb-select").each(function () {
                const itemTotalPrice = parseFloat(
                    $(this).closest(".item-wishlist")
                        .find(".wishlist-action h4")
                        .text()
                        .replace(/\./g, "")
                        .replace("đ", "")
                );
                totalPrice += itemTotalPrice;
            });
        }

        $(".summary-cart h4").text(`${formatCurrency(totalPrice)}đ`);
    });

    $("#cart-items").on("change", ".cb-select", function () {
        const _parent = $(this).closest(".item-wishlist");
        const itemTotalPrice = parseFloat(
            _parent.find(".wishlist-action h4").text().replace(/\./g, "").replace("đ", "")
        );

        if ($(this).is(":checked")) {
            totalPrice += itemTotalPrice;
        } else {
            totalPrice -= itemTotalPrice;
        }

        // Cập nhật trạng thái checkbox tất cả
        const allChecked = $(".cb-select").length === $(".cb-select:checked").length;
        $(".cb-all").prop("checked", allChecked);

        $(".summary-cart h4").text(`${formatCurrency(totalPrice)}đ`);
    });

    $("#cart-items").on("click", ".minus-cart", function () {
        var _parent = $(this).parents(".item-wishlist");
        var _currentInput = _parent.find(".input-quantity input");
        var _unitPrice = parseFloat(_parent.find(".wishlist-price h4").text().replace(/\./g, "").replace("đ", ""));
        var _totalPriceElement = _parent.find(".wishlist-action h4");

        var _number = parseInt(_currentInput.val());
        if (_number > 1) {
            _number = _number - 1;
            _currentInput.val(_number);

            // Cập nhật tổng giá
            var _totalPrice = (_unitPrice * _number);
            _totalPriceElement.text(`${formatCurrency(_totalPrice)}đ`);
            // Nếu checkbox được chọn, cập nhật tổng giá trị
            if (_parent.find(".cb-select").is(":checked")) {
                totalPrice -= _unitPrice;
                $(".summary-cart h4").text(`${formatCurrency(totalPrice)}đ`);
            }
        }
    });

    $("#cart-items").on("click", ".plus-cart", function () {
        var _parent = $(this).parents(".item-wishlist");
        var _currentInput = _parent.find(".input-quantity input");
        var _unitPrice = parseFloat(_parent.find(".wishlist-price h4").text().replace(/\./g, "").replace("đ", ""));
        var _totalPriceElement = _parent.find(".wishlist-action h4");

        // Lấy số lượng hiện tại và maxQuantity
        var _number = parseInt(_currentInput.val());
        maxQuantity = parseInt(_parent.attr("data-maxQuantity"));
        console.log(maxQuantity)
        if (_number >= maxQuantity) {
            alert(`Số lượng sản phẩm không thể vượt quá ${maxQuantity}.`);
            return;
        }
        _number = _number + 1;
        _currentInput.val(_number);

        // Cập nhật tổng giá
        var _totalPrice = (_unitPrice * _number);
        _totalPriceElement.text(`${formatCurrency(_totalPrice)}đ`);
        // Nếu checkbox được chọn, cập nhật tổng giá trị
        if (_parent.find(".cb-select").is(":checked")) {
            totalPrice += _unitPrice;
            $(".summary-cart h4").text(`${formatCurrency(totalPrice)}đ`);
        }
    });
})