## The Lox Programming Language

### Hello, Lox
```Java
// Your first Lox program!
print "Hello world!";
```

Lox's syntax is a member of the C family

### A High-Level Language

Lox will appear the most like JavaScript but has scoping similar to Scheme and will also inherit similarities from Lua.

#### Dynamic Typing
Variables will be capable of storing any type including multiple types. Therefore, type operation mismatches will have to be caught and reported during runtime.

#### Automatic Memory Management
Lox will be capable of automatically allocating and freeing memory. The technique Lox will employ to handle this is **tracing garbage collection**.

### Data Types

Only a few atomic types will compose the Lox universe.

#### Booleans

Lox will support boolean types which evaluate to literals `true` or `false`

#### Numbers

Lox will only support double-precision floating point numbers. Lox literal's will be base-ten integers or decimals such as `1234` or `12.34`

#### Strings

String literats will be enclosed in double quotes `"Hello World!"`

#### Nil

`nil` will represent the null "no value" or "null". This will help resolve null pointers

### Expressions

#### Arithmetic

Lox features the basic arithmetic operators from C. Namely, `+`, `-`, `/`, `*`. The values on which these operators are performed are named the operands. Because the operator sits berween the operands, it is dubbed an infix operator. However, the `-` can be used as a prefix operator to negate a number.

#### Comparison and Equality

These operators will compare 2 operands and return a boolean result: `<`, `>`, `>=`, and `<=`.

Additionally, using `==` or `!=` we can test for equality or inequality between types.

#### Logical Operators

The `!` operator when used as a prefix negates its operand. The `and` operator determines whether both of its operands evalutate to `true`. On the other hand, the `or` operator determines if either of its operands evalute to `true`. Both `and` and `or` will feature short circuit evaluation.

#### Precedence and Grouping

All operators will, by default, have the same precedence and associativity that you'd expect coming from C. To modify it, use the `()` to group stuff.

Lox will not support bit comparisons, shifting, module, ternaries, etc.
