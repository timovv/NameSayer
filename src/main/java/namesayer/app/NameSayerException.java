package namesayer.app;

public class NameSayerException extends RuntimeException {

    public NameSayerException(String message) {
        super(message);
    }

    public NameSayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
