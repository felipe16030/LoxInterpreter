
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