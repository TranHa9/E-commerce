$(document).ready(function () {
    // Hàm gọi API và render nội dung
    function loadProducts(tabId, apiUrl) {
        $.ajax({
            url: apiUrl,
            method: "GET",
            dataType: "json",
            success: function (response) {
                console.log(response)
                const productsContainer = $(`#${tabId} .list-products-5`);
                productsContainer.empty(); // Xóa nội dung cũ trước khi thêm mới

                response.data.forEach(product => {
                    const productHtml = `
                        <div class="card-grid-style-3">
                            <div class="card-grid-inner">
                                <div class="tools">
                                    <a class="btn btn-trend btn-tooltip mb-10" href="#" aria-label="Trend" data-bs-placement="left"></a>
                                    <a class="btn btn-wishlist btn-tooltip mb-10" href="shop-wishlist.html" aria-label="Add To Wishlist"></a>
                                    <a class="btn btn-compare btn-tooltip mb-10" href="shop-compare.html" aria-label="Compare"></a>
                                    <a class="btn btn-quickview btn-tooltip" aria-label="Quick view" href="#ModalQuickview" data-bs-toggle="modal"></a>
                                </div>
                                <div class="image-box">
                                    <span class="label bg-brand-2">${product.discount ? `-${product.discount}%` : ''}</span>
                                    <a href="#"><img src="api/v1/files/product/${product.productImages[0]}" alt="${product.productImages[0]}"></a>
                                </div>
                                <div class="info-right">
                                    <a class="font-xs color-gray-500" href="#">${product.shopName}</a><br>
                                    <a class="color-brand-3 font-sm-bold" href="#">${product.productName}</a>
                                    <div class="rating">
                                        ${Array(product.averageRating).fill('<img src="/img/template/icons/star.svg" alt="Ecom">').join('')}
                                        <span class="font-xs color-gray-500">(${product.averageRating})</span>
                                    </div>
                                    <div class="price-info">
                                        <strong class="font-lg-bold color-brand-3 price-main">${product.minPrice}vnđ</strong>
    
                                    </div>
                                    <div class="mt-20 box-btn-cart">
                                        <a class="btn btn-cart" href="#">Add To Cart</a>
                                    </div>
                                </div>
                            </div>
                        </div>`;
                    productsContainer.append(productHtml);
                });
            },
            error: function (xhr, status, error) {
                console.error("Failed to load products:", error);
            }
        });
    }

    // Gọi API cho tab "Tất cả" khi trang tải
    loadProducts("tab-all", "/api/v1/products");

    // Lắng nghe sự kiện chuyển tab để gọi API tương ứng
    $('a[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const targetTab = $(e.target).attr('href').replace("#", "");
        if (targetTab === "tab-bestseller") {
            loadProducts("tab-bestseller", "/api/v1/products/bestseller");
        }
    });
});
