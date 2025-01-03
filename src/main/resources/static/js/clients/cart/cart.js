$(document).ready(function () {
    const user = JSON.parse(localStorage.getItem("user"));
    let fileUrl = "/api/v1/files/product/";
    let totalPrice = 0;

    function fetchCartItems() {
        if (!user || !user.id) {
            window.location.href = '/logins';
        } else {
            $.ajax({
                url: `/api/v1/cartItems/${user.id}`,
                type: 'GET',
                dataType: 'json',
                success: function (response) {
                    console.log(response)
                    // Xóa nội dung cũ
                    $('#cart-items').empty();
                    // Kiểm tra nếu có sản phẩm
                    if (response && response?.length > 0) {
                        response.forEach(cartItem => {
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
                                            <a href="/detail-product/${cartItem.productId}"><img src="${fileUrl}${cartItem.imageUrls[0]}" alt="${cartItem.productName}"></a>
                                        </div>
                                        <div class="product-info">
                                            <a href="/detail-product/${cartItem.productId}">
                                                <h6 class="color-brand-3">${cartItem.productName}</h6>
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
                                    <a class="btn btn-delete" href="#" data-id="${cartItem.id}"></a>
                                </div>                              
                            </div>
                            ${updatedMessage}
                            ${inactiveMessage}
                        `);
                        });
                    } else {
                        $('#cart-items').append(`
                             <div class="d-flex align-items-center" style="height: 100px">
                                    <p>Giỏ hàng của bạn đang trống!</p>
                             </div>
                            `);

                    }
                }
            });
        }
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
    $("#cart-items").on("input", ".input-quantity input", function () {
        var _parent = $(this).parents(".item-wishlist");
        var _currentInput = $(this);
        var _unitPrice = parseFloat(_parent.find(".wishlist-price h4").text().replace(/\./g, "").replace("đ", ""));
        var _totalPriceElement = _parent.find(".wishlist-action h4");

        // Lấy giá trị mới từ input
        var _number = parseInt(_currentInput.val()) || 1; // Nếu không hợp lệ, mặc định là 1
        var maxQuantity = parseInt(_parent.attr("data-maxQuantity"));

        // Kiểm tra số lượng không vượt quá maxQuantity
        if (_number > maxQuantity) {
            alert(`Số lượng sản phẩm không thể vượt quá ${maxQuantity}.`);
            _number = maxQuantity;
            _currentInput.val(_number);
        } else if (_number < 1) {
            _number = 1;
            _currentInput.val(_number);
        }

        // Cập nhật tổng giá sản phẩm
        var _totalPrice = _unitPrice * _number;
        _totalPriceElement.text(`${formatCurrency(_totalPrice)}đ`);

        // Nếu checkbox được chọn, cập nhật tổng giá trị
        if (_parent.find(".cb-select").is(":checked")) {
            var _prevTotalPrice = parseFloat(_parent.find(".wishlist-action h4").text().replace(/\./g, "").replace("đ", ""));
            totalPrice -= _prevTotalPrice; // Loại bỏ giá trị cũ
            totalPrice += _totalPrice;    // Thêm giá trị mới
            $(".summary-cart h4").text(`${formatCurrency(totalPrice)}đ`);
        }
    });

    $("#cart-items").on("click", ".btn-delete", function (e) {
        e.preventDefault();
        const cartItemId = $(this).data("id");

        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?")) {
            $.ajax({
                url: `/api/v1/cartItems/${cartItemId}`,
                type: "DELETE",
                success: function () {
                    showToast("Xóa sản phẩm khỏi giỏ hàng thành công!", "success");
                    fetchCartItems();
                }
            });
        }
    });
})