package repositories.generator;

public class DataGeneratorException extends RuntimeException {
    DataGeneratorException(final String message, final Throwable innerException) {
        super(message, innerException);
    }
}
