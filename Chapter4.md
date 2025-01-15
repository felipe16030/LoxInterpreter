## Chapter 4 Scanning

### Lexeme and Tokens

```Java
var language = "lox";
```

Here, the sequence "v-a-r" means something but "g-u-a" does not. Lexical analysis is about scanning through the code and assmebling the smallest blobs of meaningful information, called **lexeme**. Finding lexeme also reveals other useful information which, when combined with lexeme, forms a token. This includes:

#### Token Type

The moment we recognize a lexeme, we want to know what type it is: keyword, operator, bit of punctuation, etc.

#### Literal Values

There are lexeme for literal values like strings, numbers, etc.

#### Local Information

For example noting which line we are on in order to report an error with a given lexeme, token, or configuration.

### Regular Languages and Expressions

The core of the scanner is just a loop that reads on char at a time. It attributes the current char to a lexeme and at the end of each lexeme emits a Token.

The rules for how a language groups characters into lexeme is called its lexical grammar. Particularly, you could use RegEx's to read and classify all lexeme.