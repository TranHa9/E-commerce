$(document).ready(function () {
    let checkoutItems = JSON.parse(localStorage.getItem("checkoutItems")) || [];

    if (checkoutItems.length === 0) {
        alert("Không có sản phẩm nào để thanh toán!");
        window.location.href = "/cart"; // Quay lại giỏ hàng nếu không có sản phẩm
        return;
    }
    $('#cart-items').empty();
    // Kiểm tra nếu có sản phẩm
    if (checkoutItems && checkoutItems?.length > 0) {
        const groupedByShop = checkoutItems.reduce((acc, item) => {
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
                shopHTML += `
                    <div class="item-wishlist ">
                                <div class="wishlist-product">
                                    <div class="product-wishlist">
                                        <div class="product-image">
                                            <a href="/detail-product/${cartItem.productId}"><img src="${cartItem.imageUrl}" alt="${cartItem.productName}"></a>
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
                                            <p class="font-xl color-brand-3" type="text" >${cartItem.quantity}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="wishlist-action">
                                    <h6 class="color-brand-3">${formatCurrency(cartItem.totalPrice)}đ</h6>
                                </div>                         
                    </div>
                            `;
            });
            // Thêm input mã giảm giá vào shop
            shopHTML += `
                 <div class="mb-25">
                    <div class="shop-discount">
                       <h6 class="font-md-bold">Voucher của shop: ${shopName}
                           <span class="voucher-info">
                                ${items[0].voucher ? `${items[0].voucher.details}` : "Không có mã"}
                            </span>
                       </h6>
                    </div>
                 </div>
                 `;
            $('#cart-items').append(shopHTML);
        }
    }

    function calculateTotal() {
        let checkoutItems = JSON.parse(localStorage.getItem("checkoutItems")) || [];

        let subtotal = 0;
        let totalDiscount = 0;
        let groupedByShop = {};

        // Nhóm sản phẩm theo shop
        checkoutItems.forEach(item => {
            if (!groupedByShop[item.shopId]) {
                groupedByShop[item.shopId] = {items: [], totalPrice: 0, discount: 0};
            }
            groupedByShop[item.shopId].items.push(item);
            groupedByShop[item.shopId].totalPrice += item.totalPrice; // Cộng tổng tiền của shop
            groupedByShop[item.shopId].discount = item.discount; // Chỉ lấy một lần
        });

        // Tính tổng cộng
        Object.values(groupedByShop).forEach(shop => {
            subtotal += shop.totalPrice; // Tổng tiền hàng
            totalDiscount += shop.discount; // Chỉ trừ discount một lần
        });

        let total = subtotal - totalDiscount;

        // Hiển thị trên giao diện
        $("#subtotalValue").text(formatCurrency(subtotal) + "đ");
        $("#voucherValue").text("-" + formatCurrency(totalDiscount) + "đ");
        $("#totalValue").text(formatCurrency(total) + "đ");
    }

    calculateTotal();

    function getUserAddressData() {
        $.ajax({
            url: `/api/v1/user-address`,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                console.log(response)
                const defaultAddress = response.find(address => address.defaultAddress === true);
                if (defaultAddress) {
                    $("#address-default").text(`${defaultAddress.name} | ${defaultAddress.phone} - ${defaultAddress.address}`);
                } else {
                    $("#address-default").text("Chưa có địa chỉ mặc định");
                }
            }
        });
    }

    getUserAddressData()

})