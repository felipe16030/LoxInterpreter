## Functions

The last chapters were pieces of the puzzle to develop expressions, statements, variables, flow control, etc.

No we combine it all to create reusable code -> functions.

### Function Calls

The thing being called can be anything that evaluates to a function (functions are first class variables).

The `()` operator has higher precedence than any other operator, even unary.

Therefore, we adjust the grammar:

```Java
unary -> ("!" | "-") unary | call ;
call -> primary ( "(" arguments? ")" )* ;
arguments -> expression ( "," expression)* ;
```

