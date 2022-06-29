# MSDSCRIPT Documentation
# Licensing
MSDSCRIPT is offered free of charge. However, if you want to give Braden five dollars,
his Venmo is: @Braden-Kirkham.

Copyright 2022 MSD

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE.

MSDSCRIPT is a basic language. It can handle basic, basic math functions
such as add, or multiply. For use of subtraction, please click 
[here](https://www.google.com/search?client=firefox-b-1-d&q=calculator). 
For a paid version, click [here](https://smile.amazon.com/Casio-MS-80B-Standard-Function-Calculator/dp/B003822IRA/ref=sr_1_2?crid=3KV0W3ZQPTYBJ&keywords=calculator&qid=1649360013&sprefix=calculator%2Caps%2C132&sr=8-2).

## So, you have decided to use MSDSCRIPT... 
...Are you sure?

### Build process
From the msdscript folder in the command line run:

To create the library:

`$ make libmsdscript.a` 

To create msdscript to then run it:

`$ make`

## The essentials:
Msdscript is a command line based scripting language. To make use of it, 
you must first open the command line.
After you have used `make` to build msdscript on your machine,
msdscript can be run with the following commandline arguments:

### `$ ./msdscript --help`
* The `help` argument outputs a list of available command line inputs. 

### `$ ./msdscript --test`
* The `test` argument runs the build of msdscript against of battery of tests 
* that verify msdscript is operating properly.

### `$ ./msdscript --print`
* The `print` argument outputs user input in a format specific to MSD script. 
After passing the argument, the user can enter their expression, `2*2`, press
`return` to create a new line, and finally `ctrl-d` to run msdscript. The user 
can expect to see `(2*2)` in the next line of the command line.

### `$ ./msdscript --interp`
* The `interp` argument parses through a user input, and performs the necessary mathematical
operations and then prints the answer. After passing the argument, the user can enter their expression,
`2*2`, press `return` to create a new line, and finally `ctrl-d` to run msdscript. The user can expect to see `4D`
in the next line of the command line.

# MSDSCRIPT Grammar

```
<expr>  = <number>
        | <boolean>
        | <expr> == <expr>
        | <expr> + <expr>
        | <expr> * <expr>
        | <expr> ( <expr> )
        | <variable>
        | _let <variable> = <expr> _in <expr>
        | _if <expr> _then <expr> _else <expr>
        | _fun ( <variable> ) <expr>
```

# Semantics
In MSDSCRIPT, user input must follow the following specific formats:

**Addition:**

Takes two values that must also be numbers.
```
<expr> + <expr>
$ ./msdscript --interp
2 + 1
3D
```
**Subtraction:**

Takes two values that must also be numbers.

  * The way in which subtraction is performed in MSDSCRIPT is through the
    addition of negative numbers.
    * Note that there cannot be a gap between the minus sign and the expr.

```
<expr> + -<expr>
$ ./msdscript --interp
2 + -1
1D
```
**Multiplication:**

Takes two values that must also be numbers.

```
<expr> + <expr>
$ ./msdscript --interp
2 * 2
4D
```
**Division:**

* MSDSCRIPT does not support division at this time. There are currently no plans to do so.

**If/else:**

Takes a boolean value as its first expression, and then evaluates 
the next two expressions.

```
_if <expr> _then <expr> _else <expr>
```

**Variable declaration**

`_let` is how to bind a variable to an expression. 
The scope of the variable is only applicable with the `_in` expression
```
_let <variable> = <expr> _in <expr>
```

**Function declaration**

Creates a function value
```
_fun ( <variable> ) <expr>
```
```
_fun (a)
    a+1
```

This can be assigned to a variable using the syntax provided in `_let`.

**Function call**

```
<expr>(<expr>) //will be parsed as a function call but is limited by --interp
```
in order to interpret properly, the syntax from function declaration should be applied to the first `<expr>`

`_fun ( <variable> ) <expr> (<expr>)`
```
(_fun (a)
    a+1)(2)
3
```

**Fibonacci in MSDSCRIPT:**
```
_let fib = _fun (fib) 
             _fun (x)
               _if x == 0
               _then 1
               _else _if x == 1
                     _then 1
                     _else fib(fib)(x + -2) + fib(fib)(x + -1)
_in  fib(fib)(30)
```

# MSDSCRIPT API

MSDSCRIPT is useful for calculations. Using the appropriate grammar, specified above, you could write
programs to calculate an outcome.

e.g. a program to calculate days.

Build your program with the libmsdscript library

`c++ -O2 -o program_name cpp_file.cpp libmsdscript.a`
```
_let altTueThur = _fun (altTueThur)
                     _fun (n)
                      _if n == 0
                      _then 2
                      _else _if n == 1
                      _then 4
                      _else altTueThur(altTueThur)(n + -2)
 _in _fun(n) altTueThur(altTueThur)(n)
```

You can pass a program into MSDSCRIPT as a string that can then be interpreted
```
PTR(Expr) parse_str(std::string s);
```
With the included library, you can parse through your formatted string using the `parse_str(expr)` function.
```
std::string output = parse_str(expr)->interp(Env::empty)->to_string();
```

`c++ -o which_day which_day.cpp`

`c++ -O2 -o which_day which_day.cpp libmsdscript.a`
# Errors
`consume mismatch` 
  * You should never get this error. If you receive a 'consume mismatch' error, something has gone horribly
  wrong inside msdscript. Please run the program again. If this does not fix it, please follow the instructions in
  the bug reporting section.

## Limitations of MSDSCRIPT
* Floating point numbers
  * MSDSCRIPT currently does not offer support for floats or doubles. There is currently no plan to implement this in the future.
* Division
  * Due to the lack of floating point numbers, division is not possible using MSDSCRIPT.
* Functions can only take one parameter.
* Using and underscore in a name is not allowed, because they are reserved for the parser.

### Bug Reporting
Please report all bugs to [@elonmusk](https://twitter.com/elonmusk/with_replies?lang=en) on Twitter.

### Miscellaneous 
No animals were harmed in the making of this program.
