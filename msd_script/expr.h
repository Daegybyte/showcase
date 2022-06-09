//
// Created by Diego on 14/1/22.
//
#pragma once
class Env;
#include <string>
#include <stdexcept>
#include <sstream>
#include "pointer.h"
/**
 * Contains the class Expr
 * and the following classes that implement Expr
 * NumExpr, AddExpr, MultExpr, VarExpr, LetExpr
 */

typedef enum {
    prec_none,
    prec_equals,
    prec_add,
    prec_mult
} precedence_t;

class Val;

CLASS(Expr){
public:

    virtual ~Expr() { }

    virtual bool equals(PTR(Expr) e ) = 0;

    virtual PTR(Val) interp(PTR(Env) env) = 0;

    virtual void print(std::ostream &out) = 0;

    std::string to_string();

    virtual void pretty_print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &) = 0;
};

class NumExpr : public Expr {
public:
    int representation;

    NumExpr(int number);

    virtual bool equals(PTR(Expr) other_num);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class AddExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;

    AddExpr(PTR(Expr) lhs, PTR(Expr) rhs);

    virtual bool equals(PTR(Expr) add);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class MultExpr : public Expr {
public:
    PTR(Expr) lhs;
    PTR(Expr) rhs;

    MultExpr( PTR(Expr) lhs, PTR(Expr) rhs );

    virtual bool equals(PTR(Expr) multiply);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class VarExpr : public Expr {
public:
    std::string str;

    VarExpr(std::string str);

    virtual bool equals(PTR(Expr) );

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class LetExpr : public Expr {
public:

    PTR(VarExpr) lhs;
    PTR(Expr) rhs;
    PTR(Expr) body;

    LetExpr(PTR(VarExpr) lhs, PTR(Expr) rhs, PTR(Expr) body);

    virtual bool equals( PTR(Expr) e);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class IfExpr : public Expr {
public:

    PTR(Expr) ifRep;
    PTR(Expr) thenRep;
    PTR(Expr) elseRep;

    IfExpr(PTR(Expr) ifRep, PTR(Expr) thenRep, PTR(Expr)elseRep);

    virtual bool equals(PTR(Expr) e);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class BoolExpr : public Expr {
public:

    bool repBoolExpr;

    BoolExpr(bool repBoolRep);

    virtual bool equals(PTR(Expr) e);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class EqualsExpr : public Expr {
public:

    PTR(Expr) lhs;
    PTR(Expr) rhs;

    EqualsExpr(PTR(Expr) lhs, PTR(Expr) rhs);

    virtual bool equals(PTR(Expr) e);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class FunExpr : public Expr {
public:
    std::string formal_arg;
    PTR(Expr) body;

    FunExpr(std::string formal_arg, PTR(Expr) body);
    virtual bool equals(PTR(Expr) e);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};

class CallExpr : public Expr {
public:
    PTR(Expr) to_be_called;
    PTR(Expr) actual_arg;

    CallExpr(PTR(Expr) to_be_called, PTR(Expr) actual_arg);

    virtual bool equals(PTR(Expr) e);

    virtual PTR(Val) interp(PTR(Env) env);

    virtual void print(std::ostream &out);

    virtual void pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos);
};