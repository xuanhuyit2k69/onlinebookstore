package vn.edu.xyz.olms.exception;

public class UnpaidFineException extends RuntimeException {
    public UnpaidFineException() {
        super("Bạn có khoản phạt chưa thanh toán. Vui lòng thanh toán trước khi mượn sách.");
    }
}
