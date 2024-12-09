$(document).ready(function () {
    const tableBody = $('table tbody');
    const pagination = $(".pagination-area .pagination")
    let totalPage;
    let totalRecord;
    let pageInfo;
    let pageSize = 10;
    let pageIndex = 0;

    function getUserData(data) {
        $.ajax({
            url: '/api/v1/users',
            type: 'GET',
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                ...data
            },
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                renderUserTable(response)
                setupPagination(response.totalPage);
            }
        });
    }

    function renderUserTable(data) {
        pagination.empty();
        tableBody.empty();
        if (!data || data.data?.length === 0) {
            tableBody.append(
                `<tr><td colspan="6">Chưa có dữ liệu</td></tr>`
            );
            return;
        }
        const users = data.data;
        totalPage = data.totalPage;
        totalRecord = data.totalRecord;
        pageInfo = data.pageInfo;

        users.forEach(function (user) {
            const row = `<tr>
                <td width="40%"><a class="itemside" href="#">
                    <div class="left"><img class="img-sm img-avatar" 
                    src="${user.avatar ? `/api/v1/files/user/${user.avatar}` : '/img/people/avatar-default.jpg'}"
                    alt="avatar"></div>
                    <div class="info pl-3">
                        <h6 class="mb-0 title">${user.name}</h6><small class="text-muted">User ID: #${user.id}</small>
                    </div>
                </a></td>
                <td>${user.email}</td>
                <td>${user.phone}</td>
                <td>${user.role}</td>
                <td><span class="badge rounded-pill alert-${user.status === 'ACTIVATED' ? 'success' : 'danger'}">${user.status}</span></td>
                <td class="text-end"><a class="btn btn-sm btn-brand rounded font-sm mt-15" href="#">View details</a></td>
            </tr>`;

            tableBody.append(row);
        });

        pagination.empty();
        pagination.append(`
            <li class="page-item previous-page"><a class="page-link" href="#"><i class="material-icons md-chevron_left"></i></a></li>
        `)
        for (let i = 1; i <= totalPage; i++) {
            const page = `<li class="page-item ${i === (pageInfo.pageNumber + 1) ? "active" : ""}" page="${i - 1}"><a class="page-link" href="#">${i}</a></li>`;
            pagination.append(page);
        }
        pagination.append(`
            <li class="page-item next-page"><a class="page-link" href="#"><i class="material-icons md-chevron_right"></i></a></li>
        `)

        $(".page-item").click(function (event) {
            pageIndex = $(event.currentTarget).attr("page");
            if (!pageIndex) {
                return;
            }
            getUserData({});
        })

        $(".next-page").click(function () {
            if (pageInfo.pageNumber === totalPage - 1) {
                return;
            }
            pageIndex = pageInfo.pageNumber + 1;
            getUserData({});
        })
        $(".previous-page").click(function () {
            if (pageInfo.pageNumber === 0) {
                return;
            }
            pageIndex = pageInfo.pageNumber - 1;
            getUserData({});
        })
    }


    getUserData({});

    $("#btn-search").click(function () {
        const formValues = $("#form-search").serializeArray();
        const data = {};
        formValues.forEach(input => {
            data[input.name] = input.value;
        });

        getUserData(data)
    })

    $("#btn-reset").click(function () {
        $("#form-search")[0].reset();
        pageIndex = 0;
        getUserData({});
    });

})