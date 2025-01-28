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

### Syntax Errors

A parser has 2 jobs: given a valid series of tokens, produce syntax tree AND given an invalid series of tokens, produce an error and tell the user of their mistake.

When the parser runs into a syntax error, it must:
1. Detect and report the error before making a syntax tree
2. Cannot crash or hang when detecting an error

A decent parser must also be:
1. Fast such that it allows for live syntax highlighting and reparsing in milliseconds
2. Report as many distincy errors as there are
3. Minimize cascaded errors. get back on track and see if their are other independent errors throughout the source code. The way the parser does this is called **error recovery**

#### Panic Mode Error Recovery

Panic Mode is the error recovery method that has best withstood the test of time.

Once an incoherent token is discovered, the parser enters panic mode. It **synchronizes** its state with the incoming tokens: it does this by jumping out of any nested productions and discards tokens until reaching one that can appear at that point in the rule. This means that we may miss some errors in the discarded tokens but do not report cascaded errors.

#### Synchronizing a recursive descent parser

We can use Java's call stack to check on what the parser is doing. Each rule is a frame on the stack and to synchronize, clear the frames.

To synchronize, throw the ParseError exception which is caught higher up in the method for the grammar rule.

