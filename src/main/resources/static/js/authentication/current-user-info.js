$(document).ready(function (){
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.name) {
        $("#user-info").text(` ${user.name}`);
    } else {
        $("#user-info").text("Xin chào!");
    }

})
