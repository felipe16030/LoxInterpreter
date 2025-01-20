## Representing Code

Now that we have a series of tokens, we need a parser to transform this into a more complex representation.

For example, `1 + 2 * 3 - 4` can be represented as a tree where the leafs are numbers and intererior nodes are operators. Then, we can evaluate in a post order traversal style.

In **lexical grammar**, the alphabet is the chars of the source code, a string is a lexeme or token, and it's implemented by the scanner. On the otherhand, in **syntactic grammar**, the alphabet is tokens, a string is an expression, and it is implemented by the parser.

Therefore, we want to create a set of rules to define validity within our expressions. These rules are called **productions** because they produce strings. Each production has a head (its name) and a body (a description of what it generates).

In its pure form, the body is a list of symbols. The first kind of symbol is the **terminal symbol** which is a letter from the grammar's alphabet (literal value): tokens such as `if` or `1234` which are the "end point" because there are no further moves. The second kind of symbol is the **nonterminal** which is a reference to another rule in the grammar meaning to evaluate that rule first and insert its result here.

An example set of productions can be (terminals are quoted and nonterminals are lowercase):

```java
breakfast -> protein "with" breakfast "on the side";
breakfast -> protein ;
breakfast -> bread ;

protein -> crispiness "crispy" "bacon" ;
...
```

Anytime we hit multiple products with the same name, pick any one arbitrarily.

### Enhancing our notation:

1. Instead of repeating the rule name each time we want to add another production for it, allow a series of productions seperated by a "|"
   1. `bread -> "toast" | "biscuits" | "english muffin" ;`
2. We'll allow parentheses for grouping
   1. `protein -> ("scrambled" | "poached" | "fried") "eggs" ;`
3. Postfixing $\star$ allows the previous symbol group to repeated zero or more times
   1. `crispiness -> "really" "really" * ;`
4. The $+$ postfix is similar and requires the preceding production to appear at least once
   1. `crispiness -> "really" + ;`
5. The $?$ postfix is for an optional production. The preceding thing can appear zero or once
   1. `breakfast -> protein ( "with" breakfast "on the side")?`

### A Grammar for Lox Expressions

For now we will focus on only a handful of expressions

Thus, here is a grammar for them:

```java
expression -> literal | unary | binary | grouping ;

literal -> NUMBER | STRING | "true" | "false" | "nil" ;

grouping -> "(" expression ")" ;

unary -> ( "-" | "!") expression ;

binary -> expression operator expression ;

operator -> "==" | "!=" | "<" | "<=" | ">" | ">=" | "+" | "-" | "*" | "/" ;
```

## Implementing Syntax Trees

We can represent our grammar in the form of a tree data structure. This data structure will be called the **syntax tree**.

The sytax trees exist as interfaces between the parser and interpreter. That's why some of the classes (e.g. Expr) will lack behavior. We can generate these clases using a script... see `./tool/GenerateAst.java`