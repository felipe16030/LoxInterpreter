package lox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

public class Scanner {
    // source is the source code and tokens will hold the emitted tokens after lexically analyzing the source code
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // start and current are offsets that index into the string. start points to the first character in the lexeme and
    // current points to the current character being considered
    private int start = 0;
    private int current = 0;

    // line is the current line number being considered
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        
        // This adds the End of File token to the end for clarity
        tokens.add(new Token(EOF, "", null, this.line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            // The single char tokens have no literal value
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/':
                if (match('/')) {
                    while (peak() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH); break;
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                this.line++;
                break;
            default: Lox.error(line, "Unexpected character.");
            break;
        }
    }

    /**
     * This is like a conditional advance and only moves the current pointer forward if its what we are looking for
     * @param expected the expected next char
     * @return true if the next char is equal to the expected next char
     */
    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peak() {
        if(isAtEnd()) return '\0';
        return this.source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Consumes the next character in the source text and increments the current index
     * 
     * @return the next char
     */
    private char advance() {
        current++;
        return this.source.charAt(this.current - 1);
    }

    private void addToken(TokenType type) {
        this.addToken(type, null);
    }

    /**
     * Adds a token to tokens based on the determined type and potential literal
     * 
     * @param type the type of the token
     * @param literal the literal value associated
     */
    private void addToken(TokenType type, Object literal) {
        String text = this.source.substring(start, current);
        tokens.add(new Token(type, text, literal, this.line));
    }
}
