#include "expr.h"
#include "val.h"
#include "catch.h"
#include "env.h"

NumVal::NumVal(int rep) {
    this->rep = rep;
}

bool NumVal::equals(PTR(Val) v) {
    PTR(NumVal) cmp = CAST(NumVal)(v);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->rep == cmp->rep);
    }
}

PTR(Expr) NumVal::to_expr() {
    return NEW(NumExpr)(this->rep);
}

PTR(Val)NumVal::add_to(PTR(Val) other_val) {
    PTR(NumVal) other_num = CAST(NumVal)(other_val);
    if (other_num == NULL) {
        throw std::runtime_error("add of non-number");
    }
    return NEW(NumVal)((unsigned)rep + (unsigned)other_num->rep);
}

PTR(Val) NumVal::mult_with(PTR(Val) other_val) {
    PTR(NumVal) other_num = CAST(NumVal)(other_val);
    if (other_num == NULL) {
        throw std::runtime_error("mult of non-number");
    }
    return NEW(NumVal)((unsigned)rep * (unsigned)other_num->rep);
}


std::string NumVal::to_string() {
    return std::to_string(rep);
}

bool NumVal::is_true() {
    throw std::runtime_error("Cannot compare NumVal");
}

PTR(Val) NumVal::call(PTR(Val) actual_arg) {
    throw std::runtime_error("bad NumVal call");
}
/**
 * BoolVal
 */
BoolVal::BoolVal(bool rep) {
    this->boolRep = rep;
}

bool BoolVal::equals(PTR(Val) v) {
    PTR(BoolVal)cmp = CAST(BoolVal)(v);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->boolRep == cmp->boolRep);
    }
}

PTR(Expr) BoolVal::to_expr() {
    return NEW(BoolExpr)(this->boolRep);
}


PTR(Val)BoolVal::add_to(PTR(Val)other_val) {
    throw std::runtime_error("Cannot add bools");
}


PTR(Val)BoolVal::mult_with(PTR(Val)other_val) {
    throw std::runtime_error("Cannot mult bools");
}


std::string BoolVal::to_string() {
    if (boolRep) {
        return "_true";
    }
    return "_false";
}

bool BoolVal::is_true() {
    return boolRep;
}

PTR(Val)BoolVal::call(PTR(Val)actual_arg) {
    throw std::runtime_error("bad BoolVal call");
}

/**
 * FunVal
 */

FunVal::FunVal(std::string formal_arg, PTR(Expr)body, PTR(Env) env) {
    this->formal_arg = formal_arg;
    this->body = body;
    this->env = env;
}

bool FunVal::equals(PTR(Val) v) {
    PTR(FunVal)cmp = CAST(FunVal)(v);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->formal_arg == cmp->formal_arg && this->body->equals(cmp->body));
    }
}

PTR(Val) FunVal::add_to(PTR(Val)other_val) {
    throw std::runtime_error("cannot add FunVal");
}

PTR(Val) FunVal::mult_with(PTR(Val)other_val) {
    throw std::runtime_error("cannot mult FunVal");
}

std::string FunVal::to_string() {
    return "[function]";
}

bool FunVal::is_true() {
    return false;
}

PTR(Expr) FunVal::to_expr() {
    return NEW(FunExpr) (this->formal_arg, this->body);
}

PTR(Val) FunVal::call(PTR(Val) actual_arg) {
    return body->interp(NEW(ExtendedEnv)(formal_arg, actual_arg, this->env));
}

TEST_CASE("BoolVal") {

    PTR(BoolVal)t = NEW(BoolVal)(true);
    PTR(BoolVal)f = NEW(BoolVal)(false);

    CHECK((NEW(BoolVal)(true))->equals(t));
    CHECK((NEW(BoolVal)(false))->equals(f));
    CHECK((NEW(BoolVal)(false))->equals(t) == false);

    CHECK_THROWS_WITH(t->add_to(t), "Cannot add bools");
    CHECK_THROWS_WITH(t->mult_with(t), "Cannot mult bools");

    CHECK((t->to_string() == "_true"));
    CHECK((f->to_string() == "_false"));
    CHECK((t->to_string() == "_false") == false);
    CHECK((f->to_string() == "_true") == false);


}

TEST_CASE("ValNum") {
    CHECK((NEW(NumVal)(1))->equals(NEW(NumVal)(1)) == true);
    CHECK((NEW(NumVal)(1))->equals(NEW(NumVal)(2)) == false);
    CHECK((NEW(NumVal)(1))->equals(nullptr) == false);

    CHECK((NEW(NumVal)(1))->add_to(NEW(NumVal)(2))->equals(NEW(NumVal)(3)) == true);
    CHECK((NEW(NumVal)(1))->add_to(NEW(NumVal)(2))->equals(NEW(NumVal)(4)) == false);
    CHECK_THROWS_WITH((NEW(NumVal)(3))->add_to(nullptr), "add of non-number");

    CHECK((NEW(NumVal)(3))->mult_with(NEW(NumVal)(2))->equals(NEW(NumVal)(6)) == true);
    CHECK((NEW(NumVal)(3))->mult_with(NEW(NumVal)(2))->equals(NEW(NumVal)(5)) == false);
    CHECK_THROWS_WITH((NEW(NumVal)(3))->mult_with(nullptr), "mult of non-number");

    CHECK(((NEW(NumVal)(1))->to_string() == "1") == true);
//    CHECK(((NEW(NumVal)(1))->to_string() == "1.000000") == true);
    CHECK(((NEW(NumVal)(1))->to_string() == "one") == false);
}

TEST_CASE("Conditionals") {
    PTR(BoolExpr)T = NEW(BoolExpr)(true);
    PTR(BoolExpr)F = NEW(BoolExpr)(false);
    PTR(NumVal) numval1 = NEW(NumVal)(1);
    PTR(NumVal) numval2 = NEW(NumVal)(2);
    PTR(NumExpr) numExpr1 = NEW(NumExpr)(1);
    PTR(NumExpr) numExpr2 = NEW(NumExpr)(2);
    PTR(VarExpr) varExprX = NEW(VarExpr)("x");
    PTR(EqualsExpr)equals1and1 = NEW(EqualsExpr)(numExpr1, numExpr1);
    PTR(IfExpr)ifT = NEW(IfExpr)(T, numExpr1, numExpr2);
    PTR(IfExpr)ifF = NEW(IfExpr)(F, numExpr1, numExpr2);
    PTR(IfExpr)ifX = NEW(IfExpr)(T, numExpr1, varExprX);
    PTR(IfExpr)ifEquals = NEW(IfExpr)(equals1and1, numExpr1, numExpr2);


    PTR(VarExpr) equal = NEW(VarExpr)("equal");
    PTR(EqualsExpr)eq12 = NEW(EqualsExpr)(numExpr1, numExpr2);

    PTR(IfExpr)ifExprNumVal = NEW(IfExpr)(numExpr1, numExpr1, numExpr2);
    CHECK_THROWS_WITH(ifExprNumVal->interp(Env::empty), "Cannot compare NumVal");

    CHECK(ifT->equals(ifT) == true);
    CHECK(ifF->equals(ifT) == false);
    CHECK(ifF->equals(ifF) == true);

    CHECK(ifT->interp(Env::empty)->equals(numval1));
    CHECK(ifF->interp(Env::empty)->equals(numval2));

    CHECK(ifT->to_string() == "(_if _true _then 1 _else 2)");
    CHECK(ifEquals->to_string() == "(_if (1==1) _then 1 _else 2)");
}

TEST_CASE("EqualsExpr") {
    PTR(BoolVal)boolValT = NEW(BoolVal)(true);
    PTR(BoolVal)boolValF = NEW(BoolVal)(false);
    PTR(BoolExpr)boolExprT = NEW(BoolExpr)(true);
    PTR(BoolExpr)boolExprF = NEW(BoolExpr)(false);

    PTR(NumVal) numVal1 = NEW(NumVal)(1);
    PTR(NumExpr) num1 = NEW(NumExpr)(1);
    PTR(NumVal) numVal2 = NEW(NumVal)(2);
    PTR(NumExpr) num2 = NEW(NumExpr)(2);

    PTR(AddExpr) addExpr1and1 = NEW(AddExpr)(num1, num1);
    PTR(AddExpr) addExpr1and2 = NEW(AddExpr)(num1, num2);

    PTR(VarExpr) varExprX = NEW(VarExpr)("x");

    PTR(EqualsExpr) equalsTT = NEW(EqualsExpr)(boolExprT, boolExprT);
    PTR(EqualsExpr) equalsTF = NEW(EqualsExpr)(boolExprT, boolExprF);
    PTR(EqualsExpr) equalsFF = NEW(EqualsExpr)(boolExprF, boolExprF);
    CHECK(equalsTT->equals(equalsTT) == true);
    CHECK(equalsTT->equals(equalsTF) == false);
    CHECK(equalsTT->equals(equalsFF) == false);


    PTR(EqualsExpr)equals1and1 = NEW(EqualsExpr)(num1, num1);
    PTR(EqualsExpr)equals1and2 = NEW(EqualsExpr)(num1, num2);
    PTR(EqualsExpr)equalsVarXAnd1 = NEW(EqualsExpr)(varExprX, num1);

    CHECK(equals1and1->equals(equals1and1) == true);
    CHECK(equals1and1->equals(equals1and2) == false);
    CHECK(addExpr1and1->equals(addExpr1and1) == true);

    CHECK(equalsTT->interp(Env::empty)->is_true() == true);
    CHECK(equalsTF->interp(Env::empty)->is_true() == false);
    CHECK(equalsFF->interp(Env::empty)->is_true() == true);
    CHECK(equals1and1->interp(Env::empty)->is_true() == true);
    CHECK(equals1and2->interp(Env::empty)->is_true() == false);
    CHECK_THROWS_WITH(addExpr1and1->interp(Env::empty)->is_true(), "Cannot compare NumVal");

    CHECK(equals1and1->to_string() == "(1==1)");
}
