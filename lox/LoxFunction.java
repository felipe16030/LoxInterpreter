package lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    LoxFunction(Stmt.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // creates a new environment whose enclosing is the global environment.
        // each function encapsulates its parameters and thus has its own environment.
        // recursion would break without each function having its own environment.
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
            // this is the code that binds together parameters to arguments.
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        // here we actually execute the function body using the new environment.
        interpreter.executeBlock(declaration.body, environment);
        return null;
    }

    // used by the visitCallExpr() to ensure match between params and arguments
    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
