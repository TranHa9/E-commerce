$(document).ready(function () {
    let userAddressId;
    let user = JSON.parse(localStorage.getItem("user"));

    function getUserAddressData() {
        $.ajax({
            url: `/api/v1/user-address`,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                renderUserAddressTable(response);
            }
        });
    }

    function renderUserAddressTable(data) {
        console.log(data)
        const tableBody = $('#address-list tbody');
        tableBody.empty();
        if (!data || data?.length === 0) {
            tableBody.append(
                `<tr><td colspan="6">Chưa có dữ liệu</td></tr>`
            );
            return;
        }
        data.forEach(function (address) {
            const row = `
                        <tr>
                            <td>${address.id}</td>
                            <td>${address.name}</td>
                            <td>${address.phone}</td>
                            <td>${address.address}</td>
                            <td><span class="badge rounded-pill alert-${address.defaultAddress === true ? 'success' : 'danger'}">
                                ${address.defaultAddress === true ? 'Mặc định' : 'Địa chỉ phụ'}</span></td>
                            <td class="text-end">
                                <button class="btn btn-sm font-sm rounded btn-brand mr-5 edit-address-btn" 
                                    data-bs-toggle="modal"
                                    data-bs-target="#address-modal"
                                    data-id="${address.id}"
                                    >
                                        <i class="material-icons md-edit"></i>
                                    Sửa</button>
                                <div class="btn-group">
                                    <button
                                    class="btn btn-sm font-sm btn-light rounded" type="button" data-bs-toggle="dropdown" aria-expanded="false" ><i
                                                class="material-icons md-more_horiz"></i></button>
                                        <ul class="dropdown-menu">
                                            <li><p class="dropdown-item address-default" style="cursor: pointer" data-id="${address.id}" >Đặt Mặc định</p></li>
                                            <li><p class="dropdown-item address-remove" style="cursor: pointer" data-id="${address.id}" >Xóa</p></li>
                                        </ul>
                                </div>
                            </td>
                        </tr>`;

            tableBody.append(row);
        });
    }

    getUserAddressData();

    $("#btn-save-address").click(function () {
        const isValid = $("#address-form").valid()
        if (!isValid) {
            return;
        }
        if (!userAddressId) {
            createAddress()
        } else {
            updateAddress(userAddressId)
        }
    })

    $(document).on('click', '.edit-address-btn', function () {
        userAddressId = $(this).data('id');
        $.ajax({
            url: `/api/v1/user-address/${userAddressId}`,
            type: 'GET',
            success: function (userAddress) {
                $("#name").val(userAddress.name);
                $("#address").val(userAddress.address);
                $("#phone").val(userAddress.phone);
                $("#modal-title").text('Sửa địa chỉ');
            }
        });
    });


    function createAddress() {
        const address = {
            name: $("#name").val(),
            address: $("#address").val(),
            phone: $("#phone").val(),
            userId: user.id
        }
        $.ajax({
            url: '/api/v1/user-address',
            type: 'POST',
            data: JSON.stringify(address),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Tạo mới thành công", "success");
                $("#name").val('');
                $("#address").val('');
                $("#phone").val('');
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        })
    }

    function updateAddress(id) {
        const address = {
            name: $("#name").val(),
            phone: $("#phone").val(),
            address: $("#address").val()
        };
        $.ajax({
            url: `/api/v1/user-address/${id}`,
            type: 'PUT',
            data: JSON.stringify(address),
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Cập nhật thành công", "success");
                $("#name").val('');
                $("#phone").val('');
                $("#address").val('');
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        });
    }

    $('#address-modal').on('hidden.bs.modal', function () {
        $('#address-form')[0].reset();
        userAddressId = null;
    });

    $(document).on("click", ".address-default", function () {
        const userAddressId = $(this).data("id");
        $.ajax({
            url: `/api/v1/user-address/${userAddressId}/set-default`,
            type: "PATCH",
            contentType: "application/json; charset=utf-8",
            success: function () {
                showToast("Thành công", "success");
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
        })
    });

    $(document).on("click", ".address-remove", function () {
        const userAddressId = $(this).data("id");
        if (confirm("Bạn có chắc chắn muốn xóa địa chỉ này không?")) {
            $.ajax({
                url: `/api/v1/user-address/${userAddressId}`,
                type: "DELETE",
                contentType: "application/json; charset=utf-8",
                success: function () {
                    showToast("Xóa thành công", "success");
                    setTimeout(function () {
                        location.reload();
                    }, 1000);
                }
            });
        }
    });
    $.validator.addMethod(
        "phonePattern",
        function (value, element) {
            return this.optional(element) || /^0\d{9}$/.test(value);
        },
        "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số"
    );
    $("#address-form").validate({
        rules: {
            name: {
                required: true,
                minlength: 3,
                maxlength: 150,
            },
            address: {
                required: true,
                minlength: 6,
                maxlength: 250,
            },
            phone: {
                required: true,
                phonePattern: true
            },
        },
        messages: {
            name: {
                required: "Vui lòng nhập tên",
                minlength: "Tên phải có ít nhất 6 ký tự",
                maxlength: "Tên không quá 150 ký tự"
            },
            address: {
                required: "Vui lòng nhập tên danh mục",
                minlength: "Địa chỉ phải có ít nhất 6 ký tự",
                maxlength: "Địa chỉ không quá 150 ký tự"
            },
            phone: {
                required: "Số điện thoại bắt buộc nhập",
            }
        }
    });
})