package lox;

import java.util.List;
import static lox.TokenType.*;

public abstract class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // immediately matches to equality
    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        // captures the first nonterminal as a "comparison"
        Expr expr = comparison();
        
        // the "*" part of the rule that matches every comparison until the end of the expression
        // for example: a == b == c == d creates a new binary expression each iteration with the 
        // old expression as the left expression
        while(match(BANG_EQUAL, EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // This checks to see if the current token has any of the given types
    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) this.current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return this.tokens.get(this.current);
    }

    private Token previous() {
        return this.tokens.get(this.current - 1);
    }
}
