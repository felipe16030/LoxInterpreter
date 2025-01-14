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