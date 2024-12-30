$(document).ready(function () {
    const urlParts = window.location.pathname.split('/');
    const productId = urlParts[urlParts.length - 1];
    $.ajax({
        url: `/api/v1/products/${productId}`,
        method: "GET",
        dataType: "json",
        success: function (data) {
            console.log(data)
            // Cập nhật tiêu đề và nhà cung cấp
            $('#product-name').text(data.productName);

            $(".byAUthor").text(data.shopName).attr("href");

            // Cập nhật giá
            $(".price-main").text(`$${data.minPrice}`);
            $(".price-line").text(`$${data.maxPrice}`);

            // Cập nhật số lượng đánh giá
            $(".rating span").text(`(${data.averageRating} reviews)`);

            const firstAttribute = data.variants[0].attributes[0].name;
            $("#attributes-first-name").text(firstAttribute + ':');
            const uniqueColors = new Set();
            const colorOptions = $('#color-options')
            data.variants.forEach(variant => {
                const color = variant.attributes.find(attr => attr.name === firstAttribute);
                if (color && variant.image && !uniqueColors.has(color.value)) {
                    uniqueColors.add(color.value);
                    const li = $('<li class="d-flex flex-column justify-content-between"></li>');
                    li.html(`<img src="/api/v1/files/product/${variant.image || ''}" alt="${color.value}" title="${color.value}"><span>${color.value}</span>`);
                    colorOptions.append(li);
                }
            });
            data.variants.forEach(variant => {
                const color = variant.attributes.find(attr => attr.name === firstAttribute);
                if (color && !variant.image && !uniqueColors.has(color.value)) {
                    uniqueColors.add(color.value);
                    const li = $('<li class="d-flex align-items-end"></li>');
                    li.html(`<span>${color.value}</span>`);
                    colorOptions.append(li);
                }
            });

            const endAttribute = data.variants[0].attributes[1].name;
            console.log(endAttribute)
            $("#attributes-end-name").text(endAttribute + ':');
            const sizeOptions = $("#size-options")
            const uniqueSizes = new Set();
            data.variants.forEach(variant => {
                const color = variant.attributes.find(attr => attr.name === endAttribute);
                if (color && !uniqueSizes.has(color.value)) {
                    uniqueSizes.add(color.value);
                    const li = $('<li></li>');
                    li.html(`<span title=${color.value}>${color.value}</span>`);
                    sizeOptions.append(li);
                }
            });
        }
    });
})