$(document).ready(function () {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken || isTokenExpired(accessToken)) {
        localStorage.removeItem("user");
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
    }
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.avatar) {
        $(".avatar-info").attr("src", "/api/v1/files/user/" + user.avatar);
    }
    setRefreshTimer();
})
