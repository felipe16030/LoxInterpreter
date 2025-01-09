
## From Source Code to Machine Code

We have to work our way up the mountain to get a bird's-eye view of the user's program to see what the code *means*. Then, we descend down the other side of the mountain to transform it into the machine code. This involves the following steps:

#### Scanning
A scanner takes in a linear stream of cahracters and chunks them together in a series of tokens. Tokens can be 1 character (e.g. "," or "+") or more (e.g.  "1234", "min"). It often discards whitespace, leaving a clean sequence of meaningful tokens.

#### Parsing

A parser gives our syntax a grammar to compose larger expressions: it takes the flat sequence of tokens and builds a tree structure that mirrors the nested nature of grammer. These trees are called "ASTs", "syntax trees", etc.

#### Static Analysis
The first bit of analysis is called **binding**, where identifiers are linked to their definition (variable names to their value). This is where scope comes into play and, if a language is statically typed, type errors as well. 

From here we have reached the summit of the mountain and need to store the semantic insight visible to us from analysis. This is often done in a few places:

- Stored as attributes on the syntax tree
- Stored in a look-up table off to the side called a **symbol table**
- Transform the tree into a new DS that more directly expresses the semantics of the code

Everything up to Statis Analysis is considered the front end of the implementation.

#### Intermediate Representations

After the front end, the code may be stored in an intermediate representation that is not specific to either the source language or the destination computer.

This enables one uniform representation for the front end given multiple potential source languages and one starting point for each backend given a variety of target ISA's.

#### Optimization

Once we have an understanding of the user programming, we are free to swap it out with a different one that carries the same semantics (e.g. simple folding: calculate static expressions at compile time)

#### Code Generation

Here we begin the backend, generating more and more primitive code that a CPU can actually run (e.g. assembly, machine code).

Additionally, we are presented with a choice: creating an executable that our OS can load directly onto the chip (very complicated given the complexity of modern ISA's) vs generating code for an idealized "virtual" machine. This idealized virtual code is valled **bytecode** and is "portable" from machine to machine.

#### Virtual Machine

From here, we make another decision. We can further compile our bytecode into native code for the specific machine it runs on or we can write a virutal machine, a program that emulates a hypothetical chip supporting our virtual architecture. 

Running a VM is much slower than running native code because every instruction is simulated at runtime, but the benefit is portability.

#### Runtime

Once the programming is in an executing state, we need a services for our language while the programming is running (e.g. garbage collector, instance of, etc.).

In a fully compiled language, the runtime is inserted directly in the executable. If a language is run inside an interpreter, the runtime exists in the VM.

### Shortcuts and Alternate Routes

#### Single Pass Compilers

Some simple compilers interleave parsing, analysis, and code generation all in one pass. There is no global information held about the program in data structures or IR's. This means that, at compile time, each reference must be known. Languages such as C and Pascal were designed around this restriction and it is why, in those languages, functions must be defined before any references to them.

#### Tree-Walk Interpreters
This shortcut involves executing code right after parsing it to an AST. Running the code involves traversing the syntax tree one branch and leaf at a time, which is slow.

#### Transpilers
This shortcut involves taking some IR of the source code and generating valid source code in another language, about as high level as yours. Then, use existing compilation tools on the newly generated "IR" source code. This is called **source-to-source compiler** or a **transpiler**.

The "machine code" of web browsers of today is JavaScript. Therefore, most languages have a compiler that targets JS.

#### Just in Time Compilation
Reserved more for experts, JIT involves compiling the source code native for the architecture of the end user/computer.

### Compilers and Interpreters

**Compiling** is an *implementation technique* that involves translating a source language to some other (typically lower level) form. Examples include, generating bytecode, machine code, or transpiling. After the source code is compiled, the user has to run the resulting output themselves.

Conversely, an **interpreter** takes the source code and executes it immediately.

Some compilers include GCC and Clang which compile the source code to machine code and require the user to run it. On the other hand, Ruby was fun from source, executed directly by traversing the syntax tree meaning Ruby is interpreted. Finally, in the case of CPython, it is first converted to an internal bytecode format before being executed from this source in a VM. Therefore, CPython is both compiled and interpreter.