package toby.exception;

public class SqlNotFoundException extends RuntimeException {
    public SqlNotFoundException(String message) {
        super(message);
    }

    SqlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
