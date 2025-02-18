package lox;

import java.util.List;
import java.util.ArrayList;

// This will be the evaluation code for each type of expression
// We return an object from each visitor function because Lox is dynamically typed
public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    // this is our API for the interpreter which takes in a list of statements and executes them

    private Environment globals = new Environment();
    // this globals will hold a fixed reference to the outermost, global environment
    private Environment environment = globals;

    Interpreter() {
        // The Interpreter constructor defines a native function for timing.
        // It is named 'clock' and it takes no arguments
        // If we wanted to define more native functions (e.g. reading from file, IO) it would be defined here.
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() { return 0; }
            
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    // our private helper method for stringifying values
    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }

            return text;
        }

        return object.toString();
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // to evaluate a literal, simply pull it back out of the sytax tree node/leaf
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        // Below is the code that enables short circuit evaluation of logical expressions
        if (expr.operator.type == TokenType.OR) {
            // we return left because Lox is dynamically typed and will thus return an Object of equal truthiness
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        // to evaluate a grouping, simply evaluate the inner expression by recursively 
        // visiting the expression
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        // evaluates the operand of the expression. this is a post-order traversal because the children are evaluated first.
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                // casting the right operator to a double is what makes the language dynamically typed
                // we don't know what it is at runtime
                checkNumberOperand(expr.operator, right);
                return - (double)right;
        }

        return null;
    }

    @Override 
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        // evaluate the children of the expression first
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                // PLUS is special because we must handle number addition and also concatenation
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
        }

        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        // evaluate the callee first; could be another function call.
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            // here we evaluate the arguments themselves.
            arguments.add(evaluate(argument));
        }
        // checks to make sure that we are calling a callable object.
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable)callee;

        // checks to make sure the number of arguments is equal to the arity of the function
        if(arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            // This will update the current environment and execute the statements
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            // This will then restore the original environment and GC will destroy the old environment
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override 
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if(isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if(stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while(isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        // we return the value from the expression because it can be nested inside other expressions.
        return value;
    }

    private boolean isTruthy(Object object) {
        // this means that only "false" and "nil" are falsey whereas everything else is truthy
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        // 2 objects are equal if they are both null
        if (a == null && b == null) return true;
        if (a == null) return false;
        
        // Lox does not support automatic conversion during equality checks so java's built in method will work
        return a.equals(b);
    }
}
