### Ambiguity and the Parsing Game

The parser will run through strings of tokens in reverse, mapping tokens to terminals to figure out which rule could have that string.

The ambiguity in parsing could lead to problems. For example, the string `6 / 3 - 1` could have been produced in 2 different ways:

1. pick binary expression, choose the left to be `6`, choose the operator to be `/`, and choose the right to be another binary expression with `3 - 1`.
2. pick binary expression, choose the left to be another binary expression with `6 / 3`, the operator to be `-`, and the right to be `1`

It produces the same string but not the same syntax tree which means different evalutions.

The way we deal with this is:
1. **Precedence** tells us which operator to evaluate first in an expression
2. **Associativity** tells us which operator is evaluated first in a series of the *same* operator
   1. left associative: $a + b + c \equiv (a + b) + c$
   2. right associative: $a = b = c \equiv a = (b = c)$
      1. only unary operators are right associative

Without these, an expression that uses multiple operators is ambiguous

To fix the ambiguity, stratify the grammar by defining a seperate rule for each precedence level:

```java
//Each expression matches expressions at its precedence level or higher

// lowest precedence
expression -> equality ;
equality -> comparison ( ("!=" | "==") comparison )* ;
comparison -> term ( (">" | ">=" | "<" | "<=") term)* ;
term -> factor ( ("-" | "+") factor)* ;
factor -> unary ( ("/" | "*") unary)* ;
unary -> ("!" | "-") unary | primary ;
primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
// highest precedence
```

### Recursive Descent Parsing

Simplest way to build a parser and doesn't require using complex parsing tools. These parsers are fast, robuts,
and support strong error handling.

It starts from the top outtermost grammar rule (expression) and matches downward.

Therefore, the parser is a literal translation of a grammar's rules straight into imperative code.