package lox;

import java.util.List;

// any object in Lox that can be called will implement this interface
interface LoxCallable {
    // the interpreter is passed into the call method incase the class implementing call needs it.
    Object call(Interpreter interpreter, List<Object> arguments);

    int arity();
}
