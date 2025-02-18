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

#### Maximum Argument Counts

We will report an error if there are more than 255 arguments.

#### Callable

all callable objects in Lox will implement the LoxCallable interface which defines a `call()` method

#### Arity

Arity is the number of expected operands. For functions, this is the number of parameters in the function declaration.

We must ensure that the arity of a function is equal to the number of arguments when called.

### Native Functions

**native functions** are those exposed to the user but implemented in the host language (for us Java).

These are called primitives, external functions, or foreign functions. They form part of the runtime.

Some languages also allow users to write their own native functions through **foreign function interface**.

#### Telling time

Jlox will implement one native function: telling time in order to benchmark performance.

