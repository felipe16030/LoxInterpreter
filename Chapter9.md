## Control Flow

Lox becomes Turing-complete in this chapter by enabling loops, branching, etc.

### Turing Machines

If our language can simulate a turing machine, it is turing complete. This means it can compute all computable functions.

The requirements is basically arbitrary memory allocation, arithmetic, and flow control.

### Conditional Execution

There are 2 kinds of flow control:
1. Branching - used to not execute some portions of code
2. Looping - used to repeat some portions of code

The first we will explore is branching, composed of `if` statements and conditional operations `?:`

```Java
statement -> exprStmt | ifStmt | printStmt | block;

ifStmt -> "if" "(" expression ")" statement
          ( "else" statement )? ;
```

We must define how else's bind to if's because this is ambigious:
```Java
if (first) if (second) whenTrue(); else whenFalse();
```

This is known as the **dangling-else** problem.

For us (and most languages) we will bind an else to the closest if that precedes it.

### Logical Operators

Technically `and` and `or` also control flow constructs. They can **short circuit** evaluate to a condition:
```Java
//containsOne() is never executed
false and containsOne();
```

Therefore, we create new productions for the logical operators:

```Java
expression -> assignment;
assignment -> IDENTIFIER "=" assignment | logic_or ;

logic_or -> logic_and ( "or" logic_and )* ;
logic_and -> equality ( "and" equality )* ;
```

This means that assignment cascades to logic_or which cascades to logic_and which cascades to equality.

### While Loops

The while loop will be a type of statement (because it produces a side effect -> new environment).

A `statement` can match to a `whileStmt` which has the same composition as a C while loop.

### For Loops

This is the last control flow construct. It has the same C-style declaration and it is a type of statement.

The production for the forStmt looks like:

```Java
"for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ";" ")" statement ;
```

The first clause is the initializer, the second is the condition, and the third is the increment.

For loops are a kind of **syntactic sugar** because they are not necessary. While loops could encompass everything.

We can **desugar** the while loop by converting it into a form that the interpreter already knows how to execute.