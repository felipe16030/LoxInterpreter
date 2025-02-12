package lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    // No need to map tokens to values because tokens store a place in the code
    // All we need is the variable name to associate it with the value
    private final Map<String, Object> values = new HashMap<>();
    final Environment enclosing;

    // For the global scopes environment
    Environment() {
        enclosing = null;
    }

    // For all other environments
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value) {
        // We do not check that it exists before hand. This allows the user to use
        // declarations in order to redefine variables.
        values.put(name, value);
    }

    Object get(Token name) {
        // We pass the token to the get function in case we need to throw a RunTime Error.
        // It is not a static/syntax error because making recursive functions would be too difficult:
        // we need to allow variables to be referenced before they are defined so long as that reference is not evaluated
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        // If a variable isn't found in the current environment, simply check the enclosing environment
        if (enclosing != null) return this.enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    // Assignment is not allowed to create a new variable so we define that rule here.
    void assign (Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            this.enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
