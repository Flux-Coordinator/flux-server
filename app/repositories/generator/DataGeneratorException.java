package repositories.generator;

class DataGeneratorException extends RuntimeException {
    DataGeneratorException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
