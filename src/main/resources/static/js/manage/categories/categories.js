$(document).ready(function () {
    let categoryId;
    let pageInfo;
    let pageSize = 10;
    let pageIndex = 0;

    function getCategoryData(data) {
        $.ajax({
            url: `/api/v1/categories`,
            type: 'GET',
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                ...data
            },
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                renderCategoryTable(response);
            }
        });
    }

    function renderCategoryTable(data) {
        const tableBody = $('#category-list tbody');
        const pagination = $(".pagination-area .pagination");
        tableBody.empty();
        pagination.empty();
        if (!data || data.data?.length === 0) {
            tableBody.append(
                `<tr><td colspan="6">Chưa có dữ liệu</td></tr>`
            );
            return;
        }
        const categories = data.data;
        totalPage = data.totalPage;
        totalRecord = data.totalRecord;
        pageInfo = data.pageInfo;

        categories.forEach(function (category) {
            const row = `
                        <tr>
                            <td>${category.id}</td>
                            <td>${category.name}</td>
                            <td>${category.description}</td>
                            <td><span class="badge rounded-pill alert-${category.status === 'ACTIVE' ? 'success' : 'danger'}">
                                ${category.status === 'ACTIVE' ? 'Hoạt động' : 'Ngừng dùng'}</span></td>
                            <td class="text-end">
                                <button class="btn btn-sm font-sm rounded btn-brand mr-5 edit-category-btn" 
                                    data-bs-toggle="modal"
                                    data-bs-target="#category-modal"
                                    data-id="${category.id}"
                                    >
                                        <i class="material-icons md-edit"></i>
                                    Sửa</button>
                                <div class="btn-group">
                                    <button
                                    class="btn btn-sm font-sm btn-light rounded" type="button" data-bs-toggle="dropdown" aria-expanded="false" ><i
                                                class="material-icons md-more_horiz"></i></button>
                                        <ul class="dropdown-menu">
                                            <li><p class="dropdown-item active-status" data-id="${category.id}" >Hoạt động</p></li>
                                            <li><p class="dropdown-item inactive-status" data-id="${category.id}" >Ngừng dùng</p></li>
                                        </ul>
                                </div>
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
            <li class="page-item previous-page"><a class="page-link" href="#"><i class="material-icons md-chevron_left"></i></a></li>
        `);

        for (let i = 1; i <= totalPage; i++) {
            const isActive = i === currentPage + 1 ? "active" : "";
            pagination.append(`<li class="page-item ${isActive}" page="${i - 1}"><a class="page-link" href="#">${i}</a></li>`);
        }

        pagination.append(`
            <li class="page-item next-page"><a class="page-link" href="#"><i class="material-icons md-chevron_right"></i></a></li>
        `);

        // Xử lý sự kiện chuyển trang
        pagination.find(".page-item").click(function () {
            const page = $(this).attr("page");
            if (page) {
                pageIndex = parseInt(page);
                getCategoryData({});
            }
        });

        pagination.find(".next-page").click(function () {
            if (currentPage < totalPage - 1) {
                pageIndex = currentPage + 1;
                getCategoryData({});
            }
        });

        pagination.find(".previous-page").click(function () {
            if (currentPage > 0) {
                pageIndex = currentPage - 1;
                getCategoryData({});
            }
        });
    }

    getCategoryData({})

    $("#btn-save-category").click(function () {
        const isValid = $("#category-form").valid()
        if (!isValid) {
            return;
        }
        if (!categoryId) {
            createCategory()
        } else {
            updateCategory(categoryId)
        }
    })
    $(document).on('click', '.edit-category-btn', function () {
        categoryId = $(this).data('id');
        $.ajax({
            url: `/api/v1/categories/${categoryId}`,
            type: 'GET',
            success: function (category) {
                $("#name").val(category.name);
                $("#description").val(category.description);
                $("#modal-title").text('Sửa danh mục');
            }
        });
    });

    function createCategory() {
        const category = {
            name: $("#name").val(),
            description: $("#description").val()
        }
        $.ajax({
            url: '/api/v1/categories',
            type: 'POST',
            data: JSON.stringify(category),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Tạo mới thành công", "success");
                $("#name").val('');
                $("#description").val('');
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        })
    }

    function updateCategory(id) {
        const category = {
            name: $("#name").val(),
            description: $("#description").val()
        };
        $.ajax({
            url: `/api/v1/categories/${id}`,
            type: 'PUT',
            data: JSON.stringify(category),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Cập nhật thành công", "success");
                $("#name").val('');
                $("#description").val('');
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    }

    $('#category-modal').on('hidden.bs.modal', function () {
        // Reset giá trị các trường trong form khi modal đóng
        $('#category-form')[0].reset();
    });

    $(document).on("click", ".active-status", function () {
        const productId = $(this).data("id");
        $.ajax({
            url: `/api/v1/categories/${productId}/status`,
            type: "PATCH",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify("ACTIVE"),
            success: function () {
                showToast("Thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
        })
    });

    $(document).on("click", ".inactive-status", function () {
        const productId = $(this).data("id");
        $.ajax({
            url: `/api/v1/categories/${productId}/status`,
            type: "PATCH",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify("INACTIVE"),
            success: function () {
                showToast("Thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
        })
    });

    $("#category-form").validate({
        rules: {
            name: {
                required: true,
                minlength: 3,
                maxlength: 150,
            }
        },
        messages: {
            name: {
                required: "Vui lòng nhập tên danh mục",
                minlength: "Tên danh mục phải có ít nhất 3 ký tự",
                maxlength: "Tên danh mục không quá 150 ký tự"
            }
        }
    });

    $("#btn-search").click(function () {
        const formValues = $("#form-search").serializeArray();
        const data = {};
        formValues.forEach(input => {
            data[input.name] = input.value;
        });

        getCategoryData(data)
    })

    $("#btn-reset").click(function () {
        $("#form-search")[0].reset();
        pageIndex = 0;
        getCategoryData({});
    });
})