package vn.techmaster.tranha.ecommerce.statics;

public enum OrderStatus {
    ORDER_CREATED, //Đặt hàng thành công
    ORDER_CONFIRMED, //Xác nhận đơn hàng
    DELIVERING, //Đang giao
    ORDER_SUCCESSED, //Giao thành công
    DELIVER_FAILED, //Giao thất bại
    CANCELLED //Hủy
}
