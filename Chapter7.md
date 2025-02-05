## Evaluating Expressions

For our first interpreter we will simply execute the syntax trees themselves in order to evaluate expressions

For each kind of expression syntax we can parse (e.g. literal, operator, etc.) we need code that knows how to evaluate the tree and produce a result. We then need to know how to store the result and organize the code that evaluates.

### Representing Values

We will have the bridge the gap between Lox's dynamic variables and Java's static variables. To do this, any variable in Lox will be stored as a `java.Object` and java also provides wrapper classes for each of its primitive types that inherit from `Object`. Thus, we will use polymorphism and then check types during runtime using `instanceof`

### Evaluating Expressions

We will need an `interpret()` method to evaluate any syntax we parse and thus we use the visitor pattern again.

### Runtime ERrors

These are failures detected while the program is running

Right now, if the user comes accross a runtime error, the JVM will throw an error and unwind, leaving the user with a java error trace stack.

#### Detecting Runtime Errors

Our current, tree-walk interpreter needs to unwind should a runtime error occur.