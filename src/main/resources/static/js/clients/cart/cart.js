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
                        const groupedByShop = response.reduce((acc, item) => {
                            const shopName = item.shopName || "Shop không xác định";
                            if (!acc[shopName]) acc[shopName] = [];
                            acc[shopName].push(item);
                            return acc;
                        }, {});
                        // Render từng shop
                        for (const [shopName, items] of Object.entries(groupedByShop)) {
                            let shopHTML = `
                                <div class="shop-group">
                                    <h5 class="shop-title" data-shopId="${items[0].shopId}">${shopName}</h5>
                                <div class="shop-products">
                                `;
                            console.log("items", items)
                            items.forEach(cartItem => {
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
                                        `;
                                }
                                shopHTML += `
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
                                    <h6 class="color-brand-3">${formatCurrency(cartItem.unitPrice)}đ</h6>
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
                                    <h6 class="color-brand-3">${formatCurrency(cartItem.totalPrice)}đ</h6>
                                </div>
                                <div class="wishlist-remove">
                                    <a class="btn btn-delete" href="#" data-id="${cartItem.id}"></a>
                                </div>                              
                            </div>
                            ${updatedMessage}
                            ${inactiveMessage}
                            `;
                            });
                            // Thêm input mã giảm giá vào shop
                            shopHTML += `
                                </div>
                                <div class="shop-discount">
                                    <h6 class="font-md-bold">Mã giảm giá cho ${shopName}
                                        <span class="voucher-info"></span>
                                    </h6>
                                    <div class="form-group d-flex">
                                        <input class="form-control mr-15" placeholder="Nhập mã giảm giá">
                                        <button class="btn btn-buy w-auto">Áp dụng</button>
                                    </div>
                                </div>
                            </div>
                            `;

                            $('#cart-items').append(shopHTML);
                        }
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
        totalDiscount = 0; // Reset tổng giảm giá
        if (isChecked) {
            $(".cb-select").each(function () {
                const itemTotalPrice = parseFloat(
                    $(this).closest(".item-wishlist")
                        .find(".wishlist-action h6")
                        .text()
                        .replace(/\./g, "")
                        .replace("đ", "")
                );
                totalPrice += itemTotalPrice;
            });
        }
        // Reset mã giảm giá và thông tin hiển thị cho tất cả các shop
        $(".shop-discount input").val(""); // Xóa tất cả input mã giảm giá
        $(".shop-discount .voucher-info").text(""); // Xóa tất cả thông tin mã giảm giá
        // Cập nhật lại tổng giá trị
        $(".summary-cart #subtotalValue").text(`${formatCurrency(totalPrice)}đ`);
        $(".summary-cart #voucherValue").text(`-${formatCurrency(totalDiscount)}đ`);
        // Cập nhật lại tổng giá trị sau khi có giảm giá
        let updatedTotal = totalPrice - totalDiscount;
        $(".summary-cart #totalValue").text(`${formatCurrency(updatedTotal)}đ`);
    });

    $("#cart-items").on("change", ".cb-select", function () {
        const _parent = $(this).closest(".item-wishlist");
        const shopElement = $(this).closest(".shop-group");
        const shopId = shopElement.find(".shop-title").data("shopid");
        const voucherInput = shopElement.find(".shop-discount input");
        const voucherInfo = shopElement.find(".shop-discount .voucher-info");
        const itemTotalPrice = parseFloat(
            _parent.find(".wishlist-action h6").text().replace(/\./g, "").replace("đ", "")
        );
        if ($(this).is(":checked")) {
            totalPrice += itemTotalPrice;
        } else {
            totalPrice -= itemTotalPrice;
        }
        // Loại bỏ mã giảm giá của shop ngay lập tức
        if (shopDiscounts[shopId]) {
            totalDiscount -= shopDiscounts[shopId];
            delete shopDiscounts[shopId];
        }

        // Reset input mã giảm giá và thông tin hiển thị
        voucherInput.val(""); // Xóa giá trị input mã giảm giá
        voucherInfo.text(""); // Xóa thông tin mã giảm giá

        // Cập nhật lại tổng giá trị
        $(".summary-cart #voucherValue").text(`-${formatCurrency(totalDiscount)}đ`);
        let subtotalValue = parseInt(
            $(".summary-cart #subtotalValue")
                .text()
                .replace(/\./g, "")
                .replace("đ", "")
        );
        let updatedTotal = subtotalValue - totalDiscount;
        $(".summary-cart #totalValue").text(`${formatCurrency(updatedTotal)}đ`);

        // Cập nhật trạng thái checkbox tất cả
        const allChecked = $(".cb-select").length === $(".cb-select:checked").length;
        $(".cb-all").prop("checked", allChecked);

        $(".summary-cart #subtotalValue").text(`${formatCurrency(totalPrice)}đ`);
    });

    $("#cart-items").on("click", ".minus-cart", function () {
        var _parent = $(this).parents(".item-wishlist");
        var _currentInput = _parent.find(".input-quantity input");
        var _unitPrice = parseFloat(_parent.find(".wishlist-price h6").text().replace(/\./g, "").replace("đ", ""));
        var _totalPriceElement = _parent.find(".wishlist-action h6");
        const cartItemId = _parent.find(".btn-delete").data("id");

        var _number = parseInt(_currentInput.val());
        if (_number > 1) {
            _number = _number - 1;
            _currentInput.val(_number);
            handleQuantityChange(cartItemId, _number);
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
        var _unitPrice = parseFloat(_parent.find(".wishlist-price h6").text().replace(/\./g, "").replace("đ", ""));
        var _totalPriceElement = _parent.find(".wishlist-action h6");
        const cartItemId = _parent.find(".btn-delete").data("id");

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
        handleQuantityChange(cartItemId, _number);
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
        var _unitPrice = parseFloat(_parent.find(".wishlist-price h6").text().replace(/\./g, "").replace("đ", ""));
        var _totalPriceElement = _parent.find(".wishlist-action h6");
        const cartItemId = _parent.find(".btn-delete").data("id");

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

        handleQuantityChange(cartItemId, _number);
        // Cập nhật tổng giá sản phẩm
        var _totalPrice = _unitPrice * _number;
        _totalPriceElement.text(`${formatCurrency(_totalPrice)}đ`);

        // Nếu checkbox được chọn, cập nhật tổng giá trị
        if (_parent.find(".cb-select").is(":checked")) {
            var _prevTotalPrice = parseFloat(_parent.find(".wishlist-action h6").text().replace(/\./g, "").replace("đ", ""));
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
                    updateCartItemCount(user.id)
                }
            });
        }
    });

    let debounceTimeout;

    // Hàm gọi API cập nhật số lượng
    function updateCartItemQuantity(cartItemId, quantity) {
        $.ajax({
            url: `/api/v1/cartItems/${cartItemId}`,
            type: "PATCH",
            contentType: "application/json",
            data: JSON.stringify(quantity),
            success: function () {
                // showToast("Cập nhật số lượng thành công", "success")
            }
        })
    }

    // Thêm debounce khi xử lý số lượng
    function handleQuantityChange(cartItemId, quantity) {
        clearTimeout(debounceTimeout); // Xóa timeout cũ nếu có
        debounceTimeout = setTimeout(() => {
            updateCartItemQuantity(cartItemId, quantity); // Gọi API sau 2 giây
        }, 2000);
    }

    let totalDiscount = 0;
    let shopDiscounts = {};
    $("#cart-items").on("click", ".shop-discount button", function () {
        const shopElement = $(this).closest(".shop-group");
        const shopId = shopElement.find(".shop-title").data("shopid");
        const voucherCode = shopElement.find(".shop-discount input").val().trim();
        const titleVoucher = shopElement.find(".shop-discount .voucher-info");
        const orderValue = calculateOrderValue(shopElement);

        // Kiểm tra nếu không có sản phẩm nào được chọn trong shop
        const checkedItems = shopElement.find(".cb-select:checked");
        if (checkedItems.length === 0) {
            alert("Vui lòng chọn ít nhất một sản phẩm để áp dụng mã giảm giá.");
            return;
        }

        if (!voucherCode) {
            alert("Vui lòng nhập mã giảm giá.");
            return;
        }

        $.ajax({
            url: `/api/v1/vouchers/validate`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                shopId: shopId,
                code: voucherCode,
                orderValue: orderValue
            }),
            success: function (response) {
                console.log(response)
                if (response.valid) {
                    const discountValue = response.discountValue;
                    // Nếu shop đã có giảm giá, trừ giảm giá cũ trước
                    if (shopDiscounts[shopId]) {
                        totalDiscount -= shopDiscounts[shopId];
                    }
                    // Cập nhật giảm giá mới cho shop
                    shopDiscounts[shopId] = discountValue;
                    totalDiscount += discountValue;

                    titleVoucher.text(`
                        - Mã: ${response.voucher.code} giảm: ${response.voucher.voucherValue <= 100
                        ? response.voucher.voucherValue + "%" : formatCurrency(response.voucher.voucherValue) + "đ"}
                    `)
                    $(".summary-cart #voucherValue").text(`-${formatCurrency(totalDiscount)}đ`);

                    let subtotalValue = parseInt(
                        $(".summary-cart #subtotalValue")
                            .text()
                            .replace(/\./g, "")
                            .replace("đ", "")
                    );
                    let updatedTotal = subtotalValue - totalDiscount;
                    $(".summary-cart #totalValue").text(`${formatCurrency(updatedTotal)}đ`);
                } else {
                    alert(response.message);
                }
            }
        });
    });

    function calculateOrderValue(shopElement) {
        let totalValue = 0;
        // Lấy tất cả các sản phẩm trong shop
        const checkedItems = shopElement.find(".cb-select:checked");
        // Duyệt qua từng sản phẩm được chọn
        checkedItems.each(function () {
            const itemElement = $(this).closest(".item-wishlist");
            const itemTotalPrice = parseFloat(
                itemElement.find(".wishlist-action h6").text().replace(/\./g, "").replace("đ", "")
            );
            totalValue += itemTotalPrice; // Cộng giá trị của từng sản phẩm
        });
        return totalValue; // Trả về tổng giá trị
    }
})