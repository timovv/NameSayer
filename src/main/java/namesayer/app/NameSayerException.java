package namesayer.app;

/**
 * An exception class which represents unexpected behaviour in the NameSayer application.
 */
public class NameSayerException extends RuntimeException {

    private static final long serialVersionUID = 20181009L;

    public NameSayerException(String message) {
        super(message);
    }

    public NameSayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
