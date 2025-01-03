package vn.techmaster.tranha.ecommerce.exception.handler;

import vn.techmaster.tranha.ecommerce.exception.*;
import vn.techmaster.tranha.ecommerce.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ExistedUserException.class,
            InvalidRefreshTokenException.class,
            ObjectNotFoundException.class,
            ExpiredEmailActivationUrlException.class,
            ExpiredPasswordForgottenUrlException.class,
            AuthenticationException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(getErrorCode(ex))
                .build();

        HttpStatus status = getHttpStatus(ex);
        errorResponse.setCode(String.valueOf(status.value()));

        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus getHttpStatus(Exception ex) {
        if (ex instanceof ExistedUserException || ex instanceof PasswordNotMatchedException) {
            return HttpStatus.BAD_REQUEST;  // 400
        } else if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof InvalidRefreshTokenException || ex instanceof ObjectNotFoundException) {
            return HttpStatus.NOT_FOUND;  // 404
        } else if (ex instanceof ExpiredEmailActivationUrlException || ex instanceof ExpiredPasswordForgottenUrlException) {
            return HttpStatus.UNPROCESSABLE_ENTITY;  // 422
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;  // 500
    }

    private int getErrorCode(Exception ex) {
        if (ex instanceof ExistedUserException) {
            return 1001; // Mã lỗi số cho tài khoản đã tồn tại
        } else if (ex instanceof PasswordNotMatchedException) {
            return 1002; // Mã lỗi số cho mật khẩu không khớp
        } else if (ex instanceof InvalidRefreshTokenException) {
            return 1003; // Mã lỗi số cho token làm mới không hợp lệ
        } else if (ex instanceof ObjectNotFoundException) {
            return 1004; // Mã lỗi số cho không tìm thấy đối tượng
        } else if (ex instanceof ExpiredEmailActivationUrlException) {
            return 1005; // Mã lỗi số cho URL kích hoạt email đã hết hạn
        } else if (ex instanceof ExpiredPasswordForgottenUrlException) {
            return 1006; // Mã lỗi số cho URL quên mật khẩu đã hết hạn
        } else if (ex instanceof AuthenticationException) {
            return 1007; // Mã lỗi chưa đăng nhập hoặc sai tài khoản
        }
        return 9999; // Mã lỗi số mặc định cho lỗi không xác định
    }
}
