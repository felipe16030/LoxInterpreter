package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;

public class Lox {

    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    private static final Interpreter interpreter = new Interpreter();
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            // If more than one command line argument is passed
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            // Runs the Lox code as specified by the file path provided through the command line argument
            runFile(args[0]);
        } else {
            // Runs you into a prompt where you can execute one line at a time
            // This is also called a REPL
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) {
            System.exit(65);
        }

        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;) {
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);

            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if (hadError) return;
        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void runtimeError(RuntimeError error) {
        System.out.println(error.getMessage() + 
            "\n[line " + error.token.line + "]");

        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.out.println(
            "[line " + line + "] Error" + where + ": " + message
        );
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}



