package lox;

import java.util.List;
import java.util.ArrayList;
import static lox.TokenType.*;
import java.util.Arrays;

public class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    // private helper function to help us parse statements out of the tokens
    private Stmt statement() {
        if (match(FOR))
            return forStatement();
        if (match(IF))
            return ifStatement();
        if (match(PRINT))
            return printStatement();
        if (match(WHILE))
            return whileStatement();
        if (match(LEFT_BRACE))
            return new Stmt.Block(block());
        
        return expressionStatement();
    }

    // this is where desugaring of the for statement occurs, converting it into a while statment
    private Stmt forStatement() {
        // consume the opening parenth
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        
        // capture the initializer
        Stmt initializer;
        if (match(SEMICOLON)) {
            // if the next char is a ';' there is no initializer
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            // match to a non-variable declaration which can be an expression statement
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, " Expect ';' after loop condition.");

        Expr increment = null;
        if(!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Stmt body = statement();

        // at this point, we have captured the initializer, condition, and increment statement/expressions

        // If there is an increment, add it to the end of the body
        if(increment != null) {
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        }
        
        // If there is no condition, set the condition simply to True (infinite loop)
        if(condition == null) condition = new Expr.Literal(true);
        // With this, create a new While Loop tree using the existing body and condition
        body = new Stmt.While(condition, body);
        
        // if there is an initializer, simply declare it in a block with the now while loop tree so that 
        // the initialized variable is scoped to the loop.
        if(initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    // private helper function to help use assemble the ifStatement tree node
    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        // "thenBranch" can be another if statement, expression statement, print, or a block
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        // if we find an "else" after, assemble another statement
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    // a print statement is simply the PRINT keyword followed by some expression
    private Stmt printStatement() {
        Expr value = expression();

        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    // a variable declaration is a the VAR keyword followed by an identifier name
    // and optional initializer.
    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ; after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' to after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    // an expression statement consumes the expression and wraps it in a statement
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after value.");

        return new Stmt.Expression(expr);
    }

    private Stmt.Function function(String kind) {
        // we pass "kind" because we can reuse this method for making class methods later on
        // consumes the identifier name
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        // consumes the parameters for the function
        if(!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    // block returns a list of statements to create a block statement with.
    // it consumes all declarations in a block and feeds them to a list.
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    // immediately matches to assignment
    private Expr expression() {
        return assignment();
    }

    // we know we are parsing an assignment because the left hand side of the
    // assignment is an l-value/storage location
    private Expr assignment() {
        Expr expr = or();

        // If we have the assignment operator '='
        if (match(EQUAL)) {
            Token equals = previous();
            // Call assignment() to parse the right hand side. This also makes it so that
            // assignment is right associative.
            Expr value = assignment();

            // If the left hand side evaluated to a Variable Expr
            // This is why we need the Variable Expr in order to match assignment
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            // Throw a syntax error if assignment on an invalid left hand expression (e.g. a
            // + b = 3)
            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    // private helper to create the "or" logical condition
    // it cascades to and() then matches the remaining OR expressions before proceeding
    private Expr or() {
        Expr expr = and();

        while(match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }
    
    // private helper function to match the AND expression
    // because or() calls and(), AND is of higher precedence than or
    private Expr and() {
        Expr expr = equality();

        while(match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    // this is the declaration production that will either produce a statement,
    // variable declaration, or function declaration
    // it is also where we hook up error recovery since it is a high level
    // production
    private Stmt declaration() {
        try {
            if (match(FUN)) return function("function");
            if (match(VAR))
                return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Expr equality() {
        // captures the first nonterminal as a "comparison"
        Expr expr = comparison();

        // the "*" part of the rule that matches every comparison until the end of the
        // expression
        // for example: a == b == c == d creates a new binary expression each iteration
        // with the
        // old expression as the left expression
        // if the parser does not hit an equality operator, then it effectively calls
        // and returns comparison()
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return call();
    }

    private Expr call() {
        Expr expr = primary();
        
        // do while that assembles the call chain on the expression
        while(true) {
            if(match(LEFT_PAREN)) {
                // while we see '(' keep function calling on the left operand
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        // do while to capture all arguments
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            // consumes the ','
            } while(match(COMMA));
        }
        
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        // wrap up the callee, closing parenth, and arguments into an AST node
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary() {
        if (match(FALSE))
            return new Expr.Literal(false);
        if (match(TRUE))
            return new Expr.Literal(true);
        if (match(NIL))
            return new Expr.Literal(null);
        if (match(NUMBER, STRING))
            return new Expr.Literal(previous().literal);
        if (match(IDENTIFIER))
            return new Expr.Variable(previous());
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        // If the token does not match to any such cases
        throw error(peek(), "Expect expression.");
    }

    // This checks to see if the current token has any of the given types
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType token, String message) {
        if (check(token))
            return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            this.current++;
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

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    // To be called after catching a ParseError
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON)
                return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}
