package lox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

public class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

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
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if(isAlpha(c)) {
                    identifier();
                }
                    else {
                    Lox.error(line, "Unexpected character.");
                }
            break;
        }
    }

    private void identifier() {
        while(isAlphaNumeric(peak()) && !isAtEnd()) advance();

        String text = source.substring(this.start, this.current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;

        addToken(type);
    }

    private void string() {
        // while the char is not the closing quote and not at the end
        while(peak() != '"' && !isAtEnd()) {
            // supports multi-line strings
            if (peak() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        
        // takes care of the closing quote
        advance();

        // means we do not support escape sequences in string literals (e.g. \n)
        String value = this.source.substring(this.start + 1, this.current - 1);
        this.addToken(STRING, value);
    }

    private void number() {
        // Consume as many digits as possible
        while(isDigit(peak()) && !isAtEnd()) advance();
        
        // Looks for the fraction part
        if (peak() == '.' && isDigit(peakNext())) {
            // Consumes the '.'
            advance();
        }
        
        // Consume as many digits as possible
        while(isDigit(peak()) && !isAtEnd()) advance();

        addToken(NUMBER, Double.parseDouble(this.source.substring(this.start, this.current)));
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

    private char peakNext() {
        if(this.current + 1 >= this.source.length()) return '\0';
        return this.source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
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
