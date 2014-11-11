package rgrl;

public class RgException extends Exception {
    public RgException() {
    }

    public RgException(String message) {
        super(message);
    }

    public RgException(Throwable cause) {
        super(cause);
    }

    public RgException(String message, Throwable cause) {
        super(message, cause);
    }
}
