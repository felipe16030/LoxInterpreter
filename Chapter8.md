## Statements and State

The goal of this chapter is to give our interpreter a "memory" by storing the expressions in variables

Statements produce **side-effects** which may or may not be visible to the user (think `print` and `var`)

### Statements

**expression-statements** lets you place an expression where a statement is expected. These are function calls followed `;`

a **print-statement** evaluates an expression and prints it to the console.

We must define some new productions to handle statements

```Java
program -> statement* EOF;
statement -> exprStmt | printStmt;

exprStmt -> expression ";";
printStmt -> "print" expression ";";
```
Therefore, the production becomes `program` which identifies a list of statements followed by the `EOF` token.

#### Statement syntax trees

Statement will be a seperate class altogether from Expression but it makes use of an expression.

### Global Variables

This is the easiest type of state to support and to accomplish it, we need:
1. **variable declaration** that brings a new variable into the world by binding the name with a value
2. **variable expression** accesses the value associated with a name

#### Variable Syntax

We have to subdivide statements into 2 kinds: those allowed in the body of scope statements (think loops/if-else) and those not. We will restrict variables from being declared within loops.

To accomodate this distinction, we ammend the statement productions:

```Java
program -> declaration* EOF;

declaration -> varDecl | statement;

statement -> exprStmt | printStmt;

varDecl -> "var" IDENTIFIER ( "=" expression )? ";";
```

Any place where a statement is allowed, so to is a declaration.

Therefore, we also update primary expressions to include `IDENTIFIER` as well

### Environments

Environments is where we associate variable names with values. It is a map from variable name to value.

It is ok for variables to be referenced before they are defined so long as they are not evaluated.

### Assignment

It is possible to create languages where reassignment is not possible (e.g. Haskell).

Mutating a variable is a side-effect and can be considered "dirty" but in Lox it is allowed.

#### Assignment Syntax

Assignment is denoted with the `=` operator and is actually the lowest precedence expression.

```Java
expression -> assignment ;
assignment -> IDENTIFIER "=" assignment | equality ;
```

### Scope

**scope** defines a region where a name maps to a certain entity. Lexical scope is a style where the text/source code identifies the scope of a variable:

```Java
{
    var a = 10;
    print a; // 10
}

{
    var a = 20;
    print a; // 20
}
```

**dynamic scope** means that we are unsure what a name refers to until runtime. 

Lox's scope is controlled via curly braced-blocks called **block scope**

```Java
{
    var a = "in block";
}

print a; // Error outside scope
```

In lox, it is possible to create a variable inside a scope with the same name as one in an encapusulating scope. In this way, the local variable **shadows** the more global variable. Therefore, every time we enter a new scope, we create a new environment with all of the local variables and then discard it at the end of the block.

To implement this, each nested environment has a pointer to the immediately enclosing scope and we work our way in to out when looking up a variable.

Thus, our new grammar becomes:

```Java
statement -> exprStmt
            | printStmt
            | block ;

block -> "{" declaration* "}" ;
```