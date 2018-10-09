package namesayer.app;

public class NameSayerException extends RuntimeException {

    private static final long serialVersionUID = 20181009L;

    public NameSayerException(String message) {
        super(message);
    }

    public NameSayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
