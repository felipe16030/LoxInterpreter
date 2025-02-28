package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    // This class exists solely as a script to generate syntax trees
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        defineAst(outputDir, "Stmt", Arrays.asList(
            "Block: List<Stmt> statements",
            "Expression: Expr expression",
            "Function: Token name, List<Token> params," + " List<Stmt> body", 
            "If: Expr condition, Stmt thenBranch," + 
                " Stmt elseBranch",
            "Print: Expr expression",
            "Var: Token name, Expr initializer",
            "While: Expr condition, Stmt body"
        ));
    }

    /**
     * Helper function that prints to a file, given the outputDir, baseName, and types
     * @param outputDir
     * @param baseName
     * @param types
     * @throws IOException
     */
    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println("abstract class " + baseName + " {");
        
        // This will generate the visitor interface
        defineVisitor(writer, baseName, types);

        writer.println();
        
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
            writer.println();
        }

        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("  interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("  }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        // inner class header
        writer.println("  static class " + className + " extends " + baseName + " {");

        // constructor
        writer.println("    " + className + "(" + fieldList + ")" + "{");
        
        // store each argument in instance data
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" + className + baseName + "(this);");
        writer.println("    }");
        
        writer.println();

        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");
    }
}
