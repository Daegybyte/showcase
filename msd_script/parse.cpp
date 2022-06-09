//
// Created by Diego on 4/2/22.
//

#include "parse.h"
#include "catch.h"
#include "expr.h"
#include "val.h"
#include "env.h"
#include <iostream>

PTR(Expr) parse(std::istream &in) {
    PTR(Expr) e = parse_expr(in);

    skip_whitespace(in);
    if (!in.eof()) {
        throw std::runtime_error("invalid input");
    }
    return e;
}

static PTR(Expr) parse_expr(std::istream &in) {
    PTR(Expr) e = parse_comparg(in);

    skip_whitespace(in);

    int c = in.peek();
    if (c == '=') {
        consume(in, '=');
        c = in.peek();
        if (c == '=') {
            consume(in, '=');
            PTR(Expr) rhs = parse_expr(in);
            return NEW(EqualsExpr)(e, rhs);
        }
    } else {
        return e;
    }
}

static PTR(Expr) parse_comparg(std::istream &in) {
    PTR(Expr) e = parse_addend(in);

    skip_whitespace(in);

    int c = in.peek();
    if (c == '+') {
        consume(in, '+');
        PTR(Expr) rhs = parse_expr(in);
        return NEW(AddExpr)(e, rhs);
    } else {
        return e;
    }
}

std::string parse_keyword(std::istream &in) {
    std::string str = "";

    consume(in, '_');

    int c = in.peek();
    while (isalpha(c)) {
        str += c;
        consume(in, c);
        c = in.peek();
    }
    return str;
}

static PTR(Expr) parse_let(std::istream &in) {
    skip_whitespace(in);

    PTR(VarExpr) lhs = CAST(VarExpr)(parse_var(in));
    skip_whitespace(in);


    consume(in, '=');
    PTR(Expr) rhs = parse_expr(in);
    skip_whitespace(in);

    if (parse_keyword(in) != "in") {
        throw std::runtime_error("invalid parse let");
    }
    skip_whitespace(in);
    PTR(Expr) body = parse_expr(in);
    return NEW(LetExpr)(lhs, rhs, body);
}


static PTR(Expr) parse_fun(std::istream &in) {
    skip_whitespace(in);
    int c = in.peek();
    if (c != '(') {
        throw std::runtime_error("bad paren in fun");
    }
    consume(in, '(');

    skip_whitespace(in);
    PTR(VarExpr) var = CAST(VarExpr)(parse_var(in));
    skip_whitespace(in);

    c = in.peek();
    if (c != ')') {
        throw std::runtime_error("bad paren in fun");
    }
    consume(in, ')');


    skip_whitespace(in);
    PTR(Expr) body = parse_expr(in);

    PTR(Expr) e = NEW(FunExpr)(var->str, body);
    return e;
}

static PTR(Expr) parse_inner(std::istream &in) {
    skip_whitespace(in);
    PTR(Expr) e;

    int c = in.peek();
    if (c == '-' || isdigit(c)) {
        return parse_num(in);
    } else if (c == '(') {
        consume(in, '(');
        PTR(Expr) e = parse_expr(in);
        skip_whitespace(in);
        c = in.get();
        if (c != ')') {
            throw std::runtime_error("missing close parenthesis");
        }
        return e;

    } else if (isalpha(c)) {
        return parse_var(in);

    } else if (c == '_') {

        std::string kw = parse_keyword(in);
        if (kw == "let") {
            e = parse_let(in);
        } else if (kw == "false") {
            e = parse_false(in);
        } else if (kw == "true") {
            e = parse_true(in);
        } else if (kw == "if") {
            e = parse_if(in);
        } else if (kw == "fun") {
            e = parse_fun(in);
        } else {
            throw std::runtime_error("mismarcthed keyword");
        }
    } else {
        consume(in, c);
        throw std::runtime_error("invalid input");
    }
    return e;
}


static PTR(Expr) parse_if(std::istream &in) {
    PTR(Expr) ifRep;
    PTR(Expr) thenRep;
    PTR(Expr) elseRep;
    skip_whitespace(in);

    ifRep = parse_expr(in);
    skip_whitespace(in);

    int c = in.peek();
    if (c == '_') {
        if (parse_keyword(in) != "then") {
            throw std::runtime_error("invalid parse_if");
        }
    } else {
        throw std::runtime_error("invalid parse_if");
    }
    thenRep = parse_expr(in);
    skip_whitespace(in);


    if (c == '_') {
        if (parse_keyword(in) != "else") {
            throw std::runtime_error("invalid parse_if");
        }
    } else {
        throw std::runtime_error("invalid parse_if");
    }
    elseRep = parse_expr(in);

    return NEW(IfExpr)(ifRep, thenRep, elseRep);
}

static PTR(Expr) parse_true(std::istream &istream) {
    return NEW(BoolExpr)(true);
}

static PTR(Expr) parse_false(std::istream &istream) {
    return NEW(BoolExpr)(false);
}


static PTR(Expr) parse_num(std::istream &in) {
    double n = 0;
    bool negative = false;

    if (in.peek() == '-') {
        negative = true;
        consume(in, '-');
        if (!(isdigit(in.peek()))) {
            throw std::runtime_error("invalid input");
        }
    }

    while (1) {
        int c = in.peek();
        if (isdigit(c)) {
            consume(in, c);
            if (n > (INT_MAX - (c - '0') / 10)) {
                throw std::runtime_error("overflow");
            } else {
                n = 10 * n + (c - '0');
            }
        } else {
            break;
        }
    }


    if (negative) {
        n = -n;
    }

    return NEW(NumExpr)(n);
}

static void skip_whitespace(std::istream &in) {
    while (1) {
        int c = in.peek();
        if (!isspace(c)) {
            break;
        }
        consume(in, c);
    }
}

static void consume(std::istream &in, int expect) {
    int c = in.get();
    if (c != expect) {
        throw std::runtime_error("consume mismatch");
    }
}

PTR(Expr) parse_str(std::string s) {
    std::stringstream ss(s);
    return parse(ss);
}

static PTR(Expr) parse_addend(std::istream &in) {
    PTR(Expr) e = parse_multicand(in);

    skip_whitespace(in);

    int c = in.peek();
    if (c == '*') {
        consume(in, '*');
        PTR(Expr) rhs = parse_addend(in);
        return NEW(MultExpr)(e, rhs);
    } else { return e; }
}

static PTR(Expr) parse_var(std::istream &in) {
    std::string str = "";
    while (1) {
        char c = in.peek();
        if (isalpha(c)) {
            str += c;
            consume(in, c);
        } else { break; }
    }

    if (str.length() < 1) throw std::runtime_error("Empty VarExpr");
    int c = in.peek();
    if (!isspace(c) && c != ')' && c != EOF) std::runtime_error("invalid input");
    return NEW(VarExpr)(str);
}

static PTR(Expr) parse_multicand(std::istream &in) {
    PTR(Expr) e = parse_inner(in);

    skip_whitespace(in);

    while (in.peek() == '(') {
        consume(in, '(');
        PTR(Expr) actual_arg = parse_expr(in);
        skip_whitespace(in);
        if (in.peek() != ')') {
            throw std::runtime_error("missing ')' parse_mult");
        }
        consume(in, ')');
        e = NEW(CallExpr)(e, actual_arg);
    }
    return e;
}

TEST_CASE("parse") {

    CHECK(parse_str("(1)")->equals(NEW(NumExpr)(1)));
    CHECK(parse_str("(((1)))")->equals(NEW(NumExpr)(1)));

    CHECK(parse_str("1")->equals(NEW(NumExpr)(1)));
    CHECK(parse_str("10")->equals(NEW(NumExpr)(10)));
    CHECK(parse_str("-3")->equals(NEW(NumExpr)(-3)));
    CHECK(parse_str("  \n 5  ")->equals(NEW(NumExpr)(5)));

    CHECK(parse_str("x")->equals(NEW(VarExpr)("x")));
    CHECK(parse_str("xyz")->equals(NEW(VarExpr)("xyz")));
    CHECK(parse_str("xYz")->equals(NEW(VarExpr)("xYz")));

    CHECK(parse_str("x + y")->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))));
    CHECK(parse_str("x * y")->equals(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))));
    CHECK(parse_str("z * x + y")
                  ->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("z"), NEW(VarExpr)("x")),
                                        NEW(VarExpr)("y"))));
    CHECK(parse_str("z * (x + y)")
                  ->equals(NEW(MultExpr)(NEW(VarExpr)("z"),
                                         NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y")))));
}


TEST_CASE("consume") {
    std::stringstream test("test");
    CHECK_THROWS_WITH(consume(test, 'x'), "consume mismatch");
}

TEST_CASE("parse Nums") {
    CHECK(parse_str("1")->equals(NEW(NumExpr)(1)));
    CHECK(parse_str("-1")->equals(NEW(NumExpr)(-1)));
    CHECK(parse_str("  \n 1  ")->equals(NEW(NumExpr)(1)));
    CHECK(parse_str("(1)")->equals(NEW(NumExpr)(1)));
    CHECK(parse_str("(((1)))")->equals(NEW(NumExpr)(1)));
    CHECK(parse_str("1234")->equals(NEW(NumExpr)(1234)));
    CHECK(parse_str("1234")->equals(NEW(NumExpr)(1234)));

    CHECK_THROWS_WITH(parse_str(""), "invalid input");
    CHECK_THROWS_WITH(parse_str("()"), "invalid input");
    CHECK_THROWS_WITH(parse_str("(1"), "missing close parenthesis");
    CHECK_THROWS_WITH(parse_str("-"), "invalid input");
    CHECK_THROWS_WITH(parse_str(" -   5  "), "invalid input");
    CHECK_THROWS_WITH(parse_str("0 + "), "invalid input");
    CHECK_THROWS_WITH(parse_str("0        ++"), "invalid input");
}

TEST_CASE("parse AddExpr") {
    CHECK(parse_str("1 + 1")->equals(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1))));
    CHECK(parse_str("\tx+y")->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))));
    CHECK(parse_str("\nx+y")->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))));
    CHECK(parse_str("(-1 + a) + (2 + 2)")
                  ->equals(NEW(AddExpr)(NEW(AddExpr)(NEW(NumExpr)(-1),
                                                     NEW(VarExpr)("a")),
                                        NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(2)))));
}

TEST_CASE("parse MultExpr") {
    CHECK(parse_str("1*1")->equals(NEW(MultExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1))));
    CHECK(parse_str("x * y")->equals(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))));
    CHECK(parse_str("(0*12345)\n*a")->equals(
            NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(0), NEW(NumExpr)(12345)), NEW(VarExpr)("a"))));
}

TEST_CASE("parse VarExpr") {
    CHECK(parse_str("x")->equals(NEW(VarExpr)("x")));
    CHECK(parse_str("xyz")->equals(NEW(VarExpr)("xyz")));
    CHECK(parse_str("xYZ")->equals(NEW(VarExpr)("xYZ")));
    CHECK(parse_str("\nxYZ")->equals(NEW(VarExpr)("xYZ")));
    CHECK(parse_str("\txYZ")->equals(NEW(VarExpr)("xYZ")));

    CHECK_THROWS_WITH(parse_str("*t"), "invalid input");
    CHECK_THROWS_WITH(parse_str("x_z"), "invalid input");
    CHECK_THROWS_WITH(parse_str("x Y"), "invalid input");
}

TEST_CASE("parse mixed vars") {
    CHECK(parse_str("a * b + c")
                  ->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("a"), NEW(VarExpr)("b")),
                                        NEW(VarExpr)("c"))));
    CHECK(parse_str("a * (b + c)")
                  ->equals(NEW(MultExpr)(NEW(VarExpr)("a"),
                                         NEW(AddExpr)(NEW(VarExpr)("b"), NEW(VarExpr)("c")))));
}

TEST_CASE("parse LetExpr") {
    CHECK(parse_str("  _let  x  =  5  _in  x  +  1")->equals(
            NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));
    CHECK(parse_str("\n_let\nx\n=\n5\n_in\nx\n+\n1")->equals(
            NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));
    CHECK(parse_str("\t_let\tx\t=\t5\t_in\tx\t+\t1")->equals(
            NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));


    CHECK_THROWS_WITH(parse_str("_leet x = 1 _in 1"), "mismarcthed keyword");
    CHECK_THROWS_WITH(parse_str("_lett x = 1 _in 1"), "mismarcthed keyword");
    CHECK_THROWS_WITH(parse_str("_let x 5 _in 1"), "consume mismatch");
    CHECK_THROWS_WITH(parse_str("_let x = 5 _on 1"), "invalid parse let");
}

TEST_CASE("Parse If") {

    PTR(Expr) t = NEW(IfExpr)(NEW(BoolExpr)(true), NEW(NumExpr)(1), NEW(NumExpr)(2));
    CHECK(parse_str("_if _true _then 1 _else 2")->equals(t));

    PTR(Expr) f = NEW(IfExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(1), NEW(NumExpr)(2));
    CHECK(parse_str("_if _false _then 1 _else 2")->equals(f));

    CHECK((NEW(IfExpr)(NEW(BoolExpr)(true), NEW(BoolExpr)(true), NEW(BoolExpr)(false)))->to_string() ==
          "(_if _true _then _true _else _false)");

    CHECK_THROWS_WITH(parse_str("_if _false _tthen 1 _else 2"), "invalid parse_if");
    CHECK_THROWS_WITH(parse_str("_if _false _then 1 _eelse 2"), "invalid parse_if");
    CHECK_THROWS_WITH(parse_str("_iif _false _then 1 _else 2"), "mismarcthed keyword");
}

TEST_CASE("factorial test") {
    CHECK((parse_str("_let factrl = _fun (factrl)\n"
                     "                _fun (x)\n"
                     "                  _if x == 1\n"
                     "                  _then 1\n"
                     "                  _else x * factrl(factrl)(x + -1)\n"
                     "_in  factrl(factrl)(10)"))
                  ->interp(Env::empty)->equals(NEW(NumVal)(3628800)));
}
