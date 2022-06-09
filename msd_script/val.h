#pragma once
#include "pointer.h"
class Expr;
class Env;

CLASS(Val){
public:
    virtual ~Val() { }
    virtual bool equals(PTR(Val) v) = 0;

    virtual PTR(Val) add_to(PTR(Val) rhs) = 0;

    virtual PTR(Val) mult_with(PTR(Val) rhs) = 0;

    virtual PTR(Expr) to_expr() = 0;

    virtual std::string to_string() = 0;

    virtual bool is_true() = 0;

    virtual PTR(Val) call(PTR(Val) actual_arg) = 0;
};

class NumVal : public Val {
public:
    int rep;

    NumVal(int rep);

    bool equals(PTR(Val) v);

    PTR(Val) add_to(PTR(Val) rhs);

    PTR(Val) mult_with(PTR(Val) rhs);

    PTR(Expr) to_expr();

    std::string to_string();

    bool is_true();

    PTR(Val) call(PTR(Val) actual_arg);
};


class BoolVal : public Val {
public:
    bool boolRep;

    BoolVal(bool boolean);

    bool equals(PTR(Val) v);

    PTR(Val) add_to(PTR(Val) rhs);

    PTR(Val) mult_with(PTR(Val) rhs);

    PTR(Expr) to_expr();

    std::string to_string();

    bool is_true();

    PTR(Val) call(PTR(Val) actual_arg);
};

class FunVal : public Val {
public:
    std::string formal_arg;
    PTR(Expr) body;
    PTR(Env) env;

    FunVal(std::string formal_arg, PTR(Expr) body, PTR(Env) env);

    bool equals(PTR(Val) v);

    PTR(Val) add_to(PTR(Val) rhs);

    PTR(Val) mult_with(PTR(Val) rhs);

    PTR(Expr) to_expr();

    std::string to_string();

    bool is_true();

    PTR(Val) call(PTR(Val) actual_arg);
};