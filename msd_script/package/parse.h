//
// Created by Diego on 4/2/22.
//
#include <istream>
#include "pointer.h"
class Expr;
static PTR(Expr) parse_expr(std::istream &in);

static PTR(Expr) parse_comparg(std::istream &in);

static PTR(Expr) parse_addend(std::istream &in);

static PTR(Expr) parse_num(std::istream &in);

static PTR(Expr) parse_if(std::istream &in);

static void skip_whitespace(std::istream &in);

static void consume(std::istream &in, int expect);

PTR(Expr) parse_str(std::string s);

static PTR(Expr) parse_multicand(std::istream &in);

static PTR(Expr) parse_var(std::istream &in);

PTR(Expr) parse(std::istream &in);

static PTR(Expr) parse_false(std::istream &istream);

static PTR(Expr) parse_true(std::istream &istream);

std::string parse_keyword(std::istream &in);
