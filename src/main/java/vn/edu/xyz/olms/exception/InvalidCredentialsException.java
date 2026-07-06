package vn.edu.xyz.olms.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Sai tài khoản hoặc mật khẩu");
    }
}
