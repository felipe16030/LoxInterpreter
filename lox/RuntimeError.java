package lox;

// This object is thrown within our tree-walker interpreter when coming accross a runtime error
public class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
