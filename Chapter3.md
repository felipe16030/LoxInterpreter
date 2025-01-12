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

#### Statements

Statements produce an effect, where as expressions produce a value. Statements modify the environment in some valuable way.

An expresion followed by a `;` promotes the expression to a statement, called an **expression statement**

To pack a series of statements together, use the `{}`:

```Java
{
    print "Statement one";
    print "Statement two";
}
```

### Variables

Variables are declared using `var` statements. Omiting the initializer means the variable will default to `nil`

```Java
var variableOne = "Hello";
var variableTwo;
```

### Control Flow

Lox lifts the three main control flow statements straight from C:

```Java
if(condition) {
    // True statements
} else {
    // False statements
}
```

```Java
while(condition) {
    // Loop statement
}
```

```Java
for (var i = 1; i < 10; i++) {
    // Loop statement
}
```

### Functions

Function declaration binds a function's type to its name so it can be type checked. Function definitions bind the body of the function to the name. Because Lox is dynamically typed, it makes no difference.

A function call looks identical to C:

```Java
thisFunctionCall(param1, param2);
```

Using the `()` invokes the function. Without them, the statement becomes a a refence to the function itself.

To define our own functions, Lox uses the keyword `fun` 

```Java
fun printSum(a, b) {
    return a + b;
}
```

An **argument** is the actual value passed to a function when it is called. Therefore, a function *call* has an argument list. On the otherhand, a **parameter** is the variable that holds the value of the argument inside the body of the function. Thus, a funtion *declaration* has a parameter list.

A function body is a block that can be exited using the `return` statement. Upon reaching the end of a function body without a `return` statement, `nil` is implicitly returned.

#### Closures

Functions in Lox are *first class* which means that they can be stored in variables and passed as arguments:

```Java
fun sum(a, b) {
    return a + b
}

fun other(a) {
    return a
}

// returns 3
other(sum)(1, 2)
```

Additionally, since function declarations are just statements, functions can be declared locally inside of other functions:

```Java
fun firstFunction(a, b) {
    var someLocal = "a"

    fun secondFunction() {
        return someLocal;
    }

    return secondFunction;
}
```

To create this kind of inner function, we need to "hold onto" references to surrounding variables such as `someLocal` even after the outter function has returned. Functions that do this are called **closures**.

### Classes

Classes are useful because they enable us to define compound data types. Additionally, hanging functions off of them is useful because we don't need to prefix function names with the types they operate on.

In a class-based language, there are two core concepts: instances and classes. Instances store the state for a specific object where as the class stores the specific methods and inheritance structure.

Prototype-based languages merge these two concepts. There are no classes, only objects which may contain both state data and methods. Additionally, objects may delegate (inherit) from one another. This makes them more simple.

#### Classes in Lox

The following is an example of a class in Lox:

```Java
class Breakfast {
    cook() {
        print "cooking breakfast";
    }

    clean() {
        print "cleaning up";
    }
}
```

In Lox, classes are also first-class. Additionally, the class itself an instance factory itself using the `()`.

```Java
// breakfast is in instance of the Breakfast class
var breakfast = Breakfast();
```

#### Initialization and Instantiation

Lox lets you add properties onto objects like many other dynamically typed languages:

```Java
breakfast.meat = "sausage";
breakfast.bread = "sourdough";
```

To access a field or method from within a method, use the `this` keyword.

The `init()` function can be defined within a class to make sure that objects with the proper state are defined upon instantiation.

```Java
class Breakfast {
    init(meat, bread) {
        this.meat = meat;
        this.bread = bread;
    }
}

var baconAndToast = Breakfast("Bacon", "Toast")
```

Inheritance is supported using the `<` operator. Lox classes can only inherit from one other class. Every method available in a superclass is also available in a subclass. Subclasses also inherit the `init()` method but should probably define their own.

```Java
class Brunch < Breakfast {
    drink() {
        print "How about a Bloody Mary?";
    }
}
```

The keyword `super` is used to gain access to the superclasses methods.
```Java
class Brunch < Breakfast {
    init(meat, bread, drink) {
        super.init(meat, bread);
        this.drink = drink;
    }
}
```

### Standard Libarary

For now the standard library will only have a `clock()` function that returns the number of seconds since the program started.