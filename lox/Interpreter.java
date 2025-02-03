package lox;

import static lox.TokenType.GREATER_EQUAL;
import static lox.TokenType.LESS;
import static lox.TokenType.LESS_EQUAL;

// This will be the evaluation code for each type of expression
// We return an object from each visitor function because Lox is dynamically typed
public class Interpreter implements Expr.Visitor<Object>{
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // to evaluate a literal, simply pull it back out of the sytax tree node/leaf
        return expr.value;
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
                return - (double)right;
        }

        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        // evaluate the children of the expression first
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case MINUS:
                return (double)left - (double)right;
            case PLUS:
                // PLUS is special because we must handle number addition and also concatenation
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                break;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL: return isEqual(left, right);
        }

        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
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
