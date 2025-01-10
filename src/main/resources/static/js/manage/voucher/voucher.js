$(document).ready(function () {
    let shop;
    let voucherId;
    let pageInfo;
    let pageSize = 10;
    let pageIndex = 0;

    async function getVoucherData(data) {
        try {
            const response = await $.ajax({
                url: `/api/v1/vouchers/shop/${shop.id}`,
                type: 'GET',
                data: {
                    pageIndex: pageIndex,
                    pageSize: pageSize,
                    ...data
                },
                contentType: "application/json; charset=utf-8",
            });
            renderVoucherTable(response);
            console.log(response)
        } catch (error) {
            showToast("Thất bại", "error");
        }
    }

    (async function () {
        try {
            shop = await fetchShopData();
            await getVoucherData({});
        } catch (error) {
            showToast("Có lỗi", "error");
        }
    })();

    function renderVoucherTable(data) {
        const tableBody = $('#voucher-list tbody');
        const pagination = $(".pagination-area .pagination");
        tableBody.empty();
        pagination.empty();
        if (!data || data.data?.length === 0) {
            tableBody.append(
                `<tr><td colspan="6">Chưa có dữ liệu</td></tr>`
            );
            return;
        }
        const vouchers = data.data;
        totalPage = data.totalPage;
        totalRecord = data.totalRecord;
        pageInfo = data.pageInfo;

        const now = new Date(); // Lấy ngày hiện tại

        vouchers.forEach(function (voucher) {
            const startDate = new Date(voucher.startDate);
            const endDate = new Date(voucher.endDate);
            let status = "";

            if (startDate > now) {
                status = "Sắp bắt đầu";
            } else if (now >= startDate && now <= endDate) {
                status = "Hoạt động";
            } else if (now > endDate) {
                status = "Kết thúc";
            }

            const row = `
                        <tr>
                            <td>${voucher.id}</td>
                            <td>${voucher.code}</td>
                            <td>${voucher.voucherValue}</td>
                            <td>${voucher.discountType === "PERCENTAGE" ? 'Phần trăm' : 'Giá cố định'}</td>
                            <td>${formatCurrency(voucher.minOrderValue)} đ</td>
                            <td>
                                <span class="badge rounded-pill ${status === 'Hoạt động' ? 'alert-success' : status === 'Sắp bắt đầu' ? 'alert-warning' : 'alert-danger'}">
                                    ${status}
                                </span></td>
                                
                            <td class="text-end">
                                <button class="btn btn-sm font-sm rounded btn-brand mr-5 edit-voucher-btn" 
                                    data-bs-toggle="modal"
                                    data-bs-target="#voucher-modal"
                                    data-id="${voucher.id}"
                                    >
                                        <i class="material-icons md-edit"></i>
                                    Sửa</button>
                            </td>
                        </tr>`;

            tableBody.append(row);
        });
        renderPagination(data.totalPage, data.pageInfo.pageNumber);
    }

    //Phân trang
    function renderPagination(totalPage, currentPage) {
        const pagination = $(".pagination-area .pagination");
        pagination.empty();
        pagination.append(`
            <li class="page-item previous-page"><a class="page-link"><i class="material-icons md-chevron_left"></i></a></li>
        `);

        for (let i = 1; i <= totalPage; i++) {
            const isActive = i === currentPage + 1 ? "active" : "";
            pagination.append(`<li class="page-item ${isActive}" page="${i - 1}"><a class="page-link">${i}</a></li>`);
        }

        pagination.append(`
            <li class="page-item next-page"><a class="page-link"><i class="material-icons md-chevron_right"></i></a></li>
        `);

        // Xử lý sự kiện chuyển trang
        pagination.find(".page-item").click(function () {
            const page = $(this).attr("page");
            if (page) {
                pageIndex = parseInt(page);
                getVoucherData({});
            }
        });

        pagination.find(".next-page").click(function () {
            if (currentPage < totalPage - 1) {
                pageIndex = currentPage + 1;
                getVoucherData({});
            }
        });

        pagination.find(".previous-page").click(function () {
            if (currentPage > 0) {
                pageIndex = currentPage - 1;
                getVoucherData({});
            }
        });
    }

    $('#discountType').on('change', function () {
        const selectedType = $(this).val(); // Lấy giá trị được chọn
        const voucherValueInput = $('#voucherValue');

        if (selectedType === 'PERCENTAGE') {
            voucherValueInput.attr('type', 'text'); // Đặt kiểu nhập là text
            voucherValueInput.attr('placeholder', 'Nhập 1 - 100'); // Đặt placeholder
            voucherValueInput.val(''); // Xóa giá trị hiện tại
        } else if (selectedType === 'AMOUNT') {
            voucherValueInput.attr('type', 'number'); // Đặt kiểu nhập là số
            voucherValueInput.attr('placeholder', 'Nhập giá cố định'); // Đặt placeholder
            voucherValueInput.val(''); // Xóa giá trị hiện tại
        }
    });

    $("#btn-save-voucher").click(function () {
        const isValid = $("#voucher-form").valid()
        if (!isValid) {
            return;
        }
        if (!voucherId) {
            createVoucher()
        } else {
            updateVoucher(voucherId)
        }
    })

    function createVoucher() {
        const formValues = $("#voucher-form").serializeArray();
        const data = {};
        formValues.forEach(input => {
            data[input.name] = input.value;
        });
        data.shopId = shop.id;
        $.ajax({
            url: '/api/v1/vouchers',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Tạo mới thành công", "success");
                resetForm();
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        })
    }

    function updateVoucher(id) {
        const formValues = $("#voucher-form").serializeArray();
        const data = {};
        formValues.forEach(input => {
            data[input.name] = input.value;
        });
        data.shopId = shop.id;
        $.ajax({
            url: `/api/v1/vouchers/${id}`,
            type: 'PUT',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Cập nhật thành công", "success");
                resetForm();
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    }

    $(document).on('click', '.edit-voucher-btn', function () {
        voucherId = $(this).data('id');
        $.ajax({
            url: `/api/v1/vouchers/${voucherId}`,
            type: 'GET',
            success: function (voucher) {
                $("#code").val(voucher.code);
                $("#discountType").val(voucher.discountType);
                $("#voucherValue").val(voucher.voucherValue);
                $("#minOrderValue").val(voucher.minOrderValue);
                $("#startDate").val(voucher.startDate);
                $("#endDate").val(voucher.endDate);
                $("#usageLimit").val(voucher.usageLimit);
                $("#modal-title").text('Sửa mã giảm giá');
            }
        });
    });

    function resetForm() {
        $("#code").val('');
        $("#discountType").val('');
        $("#voucherValue").val('');
        $("#minOrderValue").val('');
        $("#startDate").val('');
        $("#endDate").val('');
        $("#usageLimit").val('');
    }

    $('#voucher-modal').on('hidden.bs.modal', function () {
        resetForm()
        voucherId = null;
    });
    $.validator.addMethod("pattern", function (value, element, param) {
        return this.optional(element) || param.test(value);
    }, "Giá trị không hợp lệ.");

    $.validator.addMethod("hasLetterNumber", function (value, element) {
        return /[A-Za-z]/.test(value) && /[0-9]/.test(value); // Kiểm tra có chữ cái và số
    });

    $.validator.addMethod("greaterThanStartDate", function (value, element) {
        const startDate = new Date($("#startDate").val());
        const endDate = new Date(value);
        return this.optional(element) || endDate > startDate; // endDate phải lớn hơn startDate
    });

    $("#voucher-form").validate({
        rules: {
            code: {
                required: true,
                minlength: 4,
                maxlength: 50,
                pattern: /^[A-Za-z0-9]+$/,
                hasLetterNumber: true // Custom method kiểm tra chứa ít nhất 1 chữ cái và 1 số
            },
            discountType: {
                required: true,
            },
            voucherValue: {
                required: true,
                number: true,
                range: function () {
                    if ($("#discountType").val() === "PERCENTAGE") {
                        return [1, 100]; // Nếu là PERCENTAGE, chỉ cho phép từ 1% đến 100%
                    } else if ($("#discountType").val() === "AMOUNT") {
                        return [1000, Infinity]; // Nếu là AMOUNT, giá trị tối thiểu là 1000
                    }
                    return undefined; // Không giới hạn
                }
            },
            minOrderValue: {
                required: true,
                number: true,
            },
            startDate: {
                required: true,
                date: true
            },
            endDate: {
                required: true,
                date: true,
                greaterThanStartDate: true // Custom method kiểm tra lớn hơn startDate
            },
            usageLimit: {
                required: true,
                number: true
            }
        },
        messages: {
            code: {
                required: "Vui lòng nhập mã giảm giá.",
                minlength: "Mã giảm giá phải có ít nhất 4 ký tự.",
                maxlength: "Mã giảm giá không quá 50 ký tự.",
                pattern: "Mã giảm giá chỉ chứa chữ cái và số, không có ký tự đặc biệt.",
                hasLetterNumber: "Mã giảm giá phải chứa ít nhất 1 chữ cái và 1 số."
            },
            discountType: {
                required: "Vui lòng chọn loại mã",
            },
            voucherValue: {
                required: "Vui lòng nhập giá trị giảm giá.",
                number: "Giá trị giảm giá phải là số.",
                range: function () {
                    if ($("#discountType").val() === "PERCENTAGE") {
                        return "Giá trị giảm giá phải từ 1% đến 100%.";
                    } else if ($("#discountType").val() === "AMOUNT") {
                        return "Giá trị giảm giá phải lớn hơn hoặc bằng 1000.";
                    }
                    return "Giá trị không hợp lệ.";
                }
            },
            minOrderValue: {
                required: "Vui lòng nhập giá trị đơn hàng tối thiểu.",
                number: "Giá trị phải là số.",
            },
            startDate: {
                required: "Vui lòng chọn ngày bắt đầu.",
                date: "Ngày không hợp lệ."
            },
            endDate: {
                required: "Vui lòng chọn ngày kết thúc.",
                date: "Ngày không hợp lệ.",
                greaterThanStartDate: "Ngày kết thúc phải lớn hơn ngày bắt đầu."
            },
            usageLimit: {
                required: "Vui lòng nhập số lượng mã",
                number: "Gía trị phải là số"
            }
        }
    });

    $("#btn-search").click(function () {
        const formValues = $("#form-search").serializeArray();
        const data = {};
        formValues.forEach(input => {
            data[input.name] = input.value;
        });

        getVoucherData(data)
    })

    $("#btn-reset").click(function () {
        $("#form-search")[0].reset();
        pageIndex = 0;
        getVoucherData({});
    });
})