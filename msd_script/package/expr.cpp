//
// Created by Diego on 14/1/22.
//

#include "expr.h"
#include "catch.h"
#include "val.h"
#include "env.h"
/**
 * Expr
 */
std::string Expr::to_string() {
    std::ostringstream strout("");
    this->print(strout);
    return strout.str();
}

void Expr::pretty_print(std::ostream &out) {
    std::streampos streampos = 0;
    pretty_print_at(out, prec_none, false, streampos);
}

/**
 * NumExpr
 */
NumExpr::NumExpr(int number){ this->representation = number;
}

bool NumExpr::equals(PTR(Expr) other_num) {
    PTR(NumExpr) cmp = CAST(NumExpr)(other_num);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->representation == cmp->representation);
    }
}

PTR(Val) NumExpr::interp(PTR(Env) env) { return NEW(NumVal)(representation); }


void NumExpr::print(std::ostream &out) { out << this->representation; }

void NumExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &) {
    out << this->representation;
}

/**
 * AddExpr()
 */
AddExpr::AddExpr(PTR(Expr) lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool AddExpr::equals(PTR(Expr) add) {
    PTR(AddExpr) cmp = CAST(AddExpr)(add);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->lhs->equals(cmp->lhs) && this->rhs->equals(cmp->rhs));
    }
}

PTR(Val) AddExpr::interp(PTR(Env) env) {
    return lhs->interp(env)->add_to(rhs->interp(env));
}


void AddExpr::print(std::ostream &out) {
    out << "(";
    lhs->print(out);
    out << "+";
    rhs->print(out);
    out << ")";
}

void AddExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {
    if (prec == prec_add || prec == prec_mult) {
        out << "(";
        this->lhs->pretty_print_at(out, prec_add, true, streampos);
        out << " + ";
        this->rhs->pretty_print_at(out, prec_none, false, streampos);
        out << ")";
    } else {
        this->lhs->pretty_print_at(out, prec_add, true, streampos);
        out << " + ";
        this->rhs->pretty_print_at(out, prec_none, false, streampos);
    }
}

/**
 * MultExpr
 */
MultExpr::MultExpr(PTR(Expr) lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool MultExpr::equals(PTR(Expr) multiply) {
    PTR(MultExpr) cmp = CAST(MultExpr)(multiply);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->lhs->equals(cmp->lhs) && this->rhs->equals(cmp->rhs));
    }
}

PTR(Val) MultExpr::interp(PTR(Env) env) {
    return (lhs->interp(env)->mult_with(rhs->interp(env)));
}

void MultExpr::print(std::ostream &out) {
    out << "(";
    lhs->print(out);
    out << "*";
    rhs->print(out);
    out << ")";
}

void MultExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {

    if (prec == prec_mult) {
        out << "(";
        this->lhs->pretty_print_at(out, prec_mult, true, streampos);
        out << " * ";
        this->rhs->pretty_print_at(out, prec_add, parentheses, streampos);
        out << ")";
    } else {
        this->lhs->pretty_print_at(out, prec_mult, true, streampos);
        out << " * ";
        this->rhs->pretty_print_at(out, prec_add, parentheses, streampos);
    }
}

/**
 * VarExpr
 */
VarExpr::VarExpr(std::string str) { this->str = str; }

bool VarExpr::equals(PTR(Expr) e) {
    PTR(VarExpr) cmp = CAST(VarExpr)(e);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->str == cmp->str);
    }
}

PTR(Val) VarExpr::interp(PTR(Env) env) {
return env->lookup(this->str);
}

void VarExpr::print(std::ostream &out) { out << this->str; }

void VarExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {
    out << this->str;
}

/**
 * LetExpr
 */
LetExpr::LetExpr(PTR(VarExpr) var, PTR(Expr) rhs, PTR(Expr) body) {
    this->lhs = var;
    this->rhs = rhs;
    this->body = body;
}

bool LetExpr::equals(PTR(Expr) let) {
    PTR(LetExpr) cmp = CAST(LetExpr)(let);
    if (cmp == nullptr) {
        return false;
    } else {
        return (
                this->lhs->equals(cmp->lhs) &&
                this->body->equals(cmp->body) &&
                this->rhs->equals(cmp->rhs));
    }
}

PTR(Val) LetExpr::interp(PTR(Env) env) {
    PTR(Val) rhs_val = rhs->interp(env);
    PTR(Env) new_env = NEW(ExtendedEnv)(lhs->str, rhs_val, env);
    return this->body->interp(new_env);
}

void LetExpr::print(std::ostream &out) {
    out << "(_let ";
    lhs->print(out);
    out << "=";
    rhs->print(out);
    out << " _in ";
    body->print(out);
    out << ")";
}

void LetExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {
    if (parentheses) {
        out << "(";
    }

    int startPos = out.tellp() - streampos;
    out << "_let ";
    out << lhs->str << " = ";
    rhs->pretty_print_at(out, prec, parentheses, streampos);

    out << "\n";
    streampos = out.tellp();
    for (int i = 0; i < startPos; i++) {
        out << " ";
    }

    out << "_in  ";
    body->pretty_print_at(out, prec_none, false, streampos); //reset

    if (parentheses) {
        out << ")";
    }
}

/**
 * IfExpr
 */
IfExpr::IfExpr(PTR(Expr) ifRep, PTR(Expr) thenRep, PTR(Expr) elseRep) {
    this->ifRep = ifRep;
    this->thenRep = thenRep;
    this->elseRep = elseRep;
}

PTR(Val) IfExpr::interp(PTR(Env) env) {
    if (ifRep->interp(env)->is_true()) {
        return thenRep->interp(env);
    } else {
        return elseRep->interp(env);
    }
}

bool IfExpr::equals(PTR(Expr) e) {
    PTR(IfExpr) cmp = CAST(IfExpr)(e);
    if (cmp == nullptr) {
        return false;
    } else {

        return (this->ifRep->equals(cmp->ifRep)
                && this->thenRep->equals(cmp->thenRep)
                && this->elseRep->equals(cmp->elseRep));
    }
}

void IfExpr::print(std::ostream &out) {
    out << "(_if ";
    ifRep->print(out);
    out << " _then ";
    thenRep->print(out);
    out << " _else ";
    elseRep->print(out);
    out << ")";
}

void IfExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {}

/**
 * BoolExpr
 */

BoolExpr::BoolExpr(bool repBoolRep) {
    this->repBoolExpr = repBoolRep;
}

bool BoolExpr::equals(PTR(Expr) e) {
    PTR(BoolExpr) cmp = CAST(BoolExpr)(e);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->repBoolExpr == cmp->repBoolExpr);
    }
}

PTR(Val) BoolExpr::interp(PTR(Env) env) {
    return NEW(BoolVal)(repBoolExpr);
}

void BoolExpr::print(std::ostream &out) {
    if (repBoolExpr) {
        out << "_true";
    } else {
        out << "_false";
    }
}

void BoolExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {}


/**
 * EqualsExpr
 */
EqualsExpr::EqualsExpr(PTR(Expr) lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

bool EqualsExpr::equals(PTR(Expr) e) {
    PTR(EqualsExpr) cmp = CAST(EqualsExpr)(e);
    if (cmp == nullptr) {
        return false;
    } else {
        return (this->lhs->equals(cmp->lhs) && this->rhs->equals(cmp->rhs));
    }
}

PTR(Val) EqualsExpr::interp(PTR(Env) env) {
    return NEW(BoolVal)(lhs->interp(env)->equals(rhs->interp(env)));
}

void EqualsExpr::print(std::ostream &out) {
    out << "(";
    lhs->print(out);
    out << "==";
    rhs->print(out);
    out << ")";
}

void EqualsExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {}

/**
 * FunExpr
 */

FunExpr::FunExpr(std::string formal_arg, PTR(Expr) body) {
    this->formal_arg = formal_arg;
    this->body = body;
}

PTR(Val) FunExpr::interp(PTR(Env) env) {
    return NEW(FunVal) (formal_arg, body, env);
}

bool FunExpr::equals(PTR(Expr) let) {
    PTR(FunExpr) cmp = CAST(FunExpr) (let);
    if (cmp == nullptr) {
        return false;
    } else {
        return (
                this->body->equals(cmp->body) &&
                this->formal_arg == cmp->formal_arg);
    }
}


void FunExpr::print(std::ostream &out){
    out << "(_fun (";
    out << formal_arg;
    out << ") ";
    body->print(out);
    out << ")";
}

void FunExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {}

/**
 * CallExpr
 */

CallExpr::CallExpr(PTR(Expr) to_be_called, PTR(Expr) actual_arg) {
    this->to_be_called = to_be_called;
    this->actual_arg = actual_arg;
}

bool CallExpr::equals(PTR(Expr) e) {
    PTR(CallExpr) cmp = CAST(CallExpr) (e);
    if (cmp == nullptr) {
        return false;
    } else {
        return this->actual_arg->equals(cmp->actual_arg) && this->to_be_called->equals(cmp->to_be_called);
    }
}


PTR(Val) CallExpr::interp(PTR(Env) env) {
   return to_be_called->interp(env)->call(actual_arg->interp(env));
}

void CallExpr::print(std::ostream &out) {
    this->to_be_called->print(out);
    out << "(";
    this->actual_arg->print(out);
    out << ")";
}

void CallExpr::pretty_print_at(std::ostream &out, precedence_t prec, bool parentheses, std::streampos &streampos) {}


/**
 *********************** TESTS ***********************
 */
TEST_CASE("pretty print") {
    std::stringstream out("");
    (NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))))->pretty_print(out);
    CHECK(out.str() == "_let x = 5\n"
                       "_in  x + 1");

    out.str(std::string());
    (NEW(AddExpr)(NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(VarExpr)("x")), NEW(NumExpr)(1)))->pretty_print(out);
    CHECK(out.str() == "(_let x = 5\n"
                       " _in  x) + 1");

    out.str(std::string());
    (NEW(MultExpr)(NEW(NumExpr)(5), NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5),
                                              NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))))->pretty_print(out);
    CHECK(out.str() == "5 * _let x = 5\n"
                       "    _in  x + 1");

    out.str(std::string());
    (NEW(AddExpr)(NEW(MultExpr)(NEW(NumExpr)(5), NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(VarExpr)("x"))),
                 NEW(NumExpr)(1)))->pretty_print(out);
    CHECK(out.str() == "5 * (_let x = 5\n"
                       "     _in  x) + 1");

    out.str(std::string());
    (NEW(LetExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5), NEW(AddExpr)(
            NEW(LetExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))),
            NEW(VarExpr)("x"))))
            ->pretty_print(out);
    CHECK(out.str() == "_let x = 5\n"
                       "_in  (_let y = 3\n"
                       "      _in  y + 2) + x");

    out.str(std::string());
    (NEW(LetExpr)(NEW(VarExpr)("x"),
                 NEW(LetExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(6), NEW(MultExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))),
                 NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))))->pretty_print(out);
    CHECK(out.str() == "_let x = _let y = 6\n"
                       "         _in  y * 2\n"
                       "_in  x + 1");

}


TEST_CASE("LetExpr interp") {
    PTR(VarExpr) var = NEW(VarExpr)("x");
    PTR(NumExpr) num = NEW(NumExpr)(1);
    PTR(AddExpr) add = NEW(AddExpr)(var, num);
    CHECK((NEW(LetExpr)(var, num, add))->interp(Env::empty)->equals(NEW(NumVal)(2)));
}

TEST_CASE("Num_equals") {
    PTR(NumExpr) one = NEW(NumExpr)(1);
    PTR(NumExpr) one_also = NEW(NumExpr)(1);
    PTR(NumExpr) two = NEW(NumExpr)(2);
    CHECK(two->equals(one) == false);
    CHECK(one->equals(one_also) == true);
}

TEST_CASE("AddExpr") {
    PTR(NumExpr) intOne = NEW(NumExpr)(1);
    PTR(NumExpr) intTwo = NEW(NumExpr)(2);

    PTR(NumExpr) one = NEW(NumExpr)(1);
    PTR(NumExpr) two = NEW(NumExpr)(2);
    PTR(NumExpr) three = NEW(NumExpr)(3);
    CHECK((NEW(AddExpr)(one, two))->equals(nullptr) == false);

    CHECK(intOne->equals(nullptr) == false);

    PTR(AddExpr) add = NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(1));
    PTR(AddExpr) addNested = NEW(AddExpr)(NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(4)), NEW(NumExpr)(6));
    CHECK(addNested->interp(Env::empty)->equals(NEW(NumVal)(12)));
    CHECK(add->to_string() == "(3+1)");
    CHECK(addNested->to_string() == "((2+4)+6)");

}

TEST_CASE("MultExpr") {
    PTR(NumExpr) intTwo = NEW(NumExpr)(2);
    PTR(NumExpr) intThree = NEW(NumExpr)(3);
    PTR(MultExpr) product = NEW(MultExpr)(intTwo, intThree);

    CHECK((NEW(MultExpr)(intTwo, intThree))->interp(Env::empty)->equals(NEW(NumVal)(6)));
    CHECK(product->equals(product) == true);
    CHECK(product->equals(intTwo) == false);
    CHECK(product->equals(nullptr) == false);

    PTR(MultExpr) mult = NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(1));
    CHECK(mult->to_string() == "(3*1)");
    CHECK((NEW(MultExpr)(intTwo, intTwo))->equals(nullptr) == false);

}


TEST_CASE("VarExpr") {
    CHECK((NEW(VarExpr)("test"))->equals(NEW(VarExpr)("test")) == true);
    CHECK((NEW(VarExpr)("test"))->equals(NEW(VarExpr)("tests")) == false);
//    CHECK(((NEW(VarExpr)("x"))->has_variable() == true));
    PTR(VarExpr) var = NEW(VarExpr)("Braden ain't gonna make it");
    CHECK(var->equals(NEW(NumExpr)(4)) == false);
    CHECK_THROWS_WITH((NEW(VarExpr)("x"))->interp(Env::empty), "free variable: x");

    PTR(VarExpr) varString = NEW(VarExpr)("test");
    CHECK(varString->to_string() == "test");

}

TEST_CASE("VarExpr Pretty Print") {
    std::stringstream out("");
    (NEW(VarExpr)("x"))->pretty_print(out);
    CHECK(out.str() == "x");
    out.str(std::string());
    (NEW(MultExpr)(NEW(AddExpr)(NEW(NumExpr)(1), NEW(VarExpr)("x")), NEW(NumExpr)(3)))->pretty_print(out);
    CHECK(out.str() == "(1 + x) * 3");
}

TEST_CASE("MultExpr pretty print") {
    PTR(NumExpr) intTwo = NEW(NumExpr)(2);
    PTR(NumExpr) intThree = NEW(NumExpr)(3);

    PTR(MultExpr) test_mult_prec = NEW(MultExpr)(intTwo, intThree);
    std::stringstream out("");
    test_mult_prec->pretty_print(out);
    CHECK(out.str() == "2 * 3");

    PTR(MultExpr) test_mult_prec_right = NEW(MultExpr)(intTwo, NEW(MultExpr)(intTwo, intThree));
    std::stringstream out2("");
    test_mult_prec_right->pretty_print(out2);
    CHECK(out2.str() == "2 * 2 * 3");

    PTR(MultExpr) test_mult_prec_left = NEW(MultExpr)(NEW(MultExpr)(intTwo, intThree), intTwo);
    std::stringstream outLeft("");
    test_mult_prec_left->pretty_print(outLeft);
    CHECK(outLeft.str() == "(2 * 3) * 2");

    PTR(MultExpr) leftSide_mult = NEW(MultExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2));
    PTR(MultExpr) rightSide_mult = NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4));
    PTR(AddExpr) test_add_double_mult = NEW(AddExpr)(leftSide_mult, rightSide_mult);
    std::stringstream double_mult("");
    test_add_double_mult->pretty_print(double_mult);
    CHECK(double_mult.str() == "1 * 2 + 3 * 4");

    PTR(AddExpr) leftSide_add = NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2));
    PTR(AddExpr) rightSide_add = NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4));
    PTR(MultExpr) test_add_double_add = NEW(MultExpr)(leftSide_add, rightSide_add);
    std::stringstream double_add("");
    test_add_double_add->pretty_print(double_add);
    CHECK(double_add.str() == "(1 + 2) * (3 + 4)");
}

TEST_CASE("AddExpr pretty print") {
    PTR(NumExpr) intTwo = NEW(NumExpr)(2);
    PTR(NumExpr) intThree = NEW(NumExpr)(3);

    PTR(AddExpr) test_add_prec = NEW(AddExpr)(intTwo, intThree);
    std::stringstream out("");
    test_add_prec->pretty_print(out);
    CHECK(out.str() == "2 + 3");

    PTR(AddExpr) test_add_prec_right = NEW(AddExpr)(intTwo, NEW(AddExpr)(intTwo, intThree));
    std::stringstream out2("");
    test_add_prec_right->pretty_print(out2);
    CHECK(out2.str() == "2 + 2 + 3");

    PTR(AddExpr) test_add_prec_left = NEW(AddExpr)(NEW(AddExpr)(intTwo, intThree), intTwo);
    std::stringstream outLeft("");
    test_add_prec_left->pretty_print(outLeft);
    CHECK(outLeft.str() == "(2 + 3) + 2");
}

TEST_CASE("AddExpr/MultExpr pretty Print") {
    PTR(NumExpr) intTwo = NEW(NumExpr)(2);
    PTR(NumExpr) intThree = NEW(NumExpr)(3);

    PTR(MultExpr) test_add_prec_right = NEW(MultExpr)(intTwo, NEW(AddExpr)(intTwo, intThree));
    std::stringstream out("");
    test_add_prec_right->pretty_print(out);
    CHECK(out.str() == "2 * (2 + 3)");

    PTR(AddExpr) test_add_prec_left = NEW(AddExpr)(intTwo, NEW(MultExpr)(intTwo, intThree));
    std::stringstream outLeft("");
    test_add_prec_left->pretty_print(outLeft);
    CHECK(outLeft.str() == "2 + 2 * 3");
}

TEST_CASE("Pretty Print At") {
    std::stringstream out("");
    out.str("");
    PTR(AddExpr) addVar = NEW(AddExpr)(NEW(NumExpr)(5), NEW(VarExpr)("a"));
    std::streampos streampos = 0;
    addVar->pretty_print_at(out, prec_none, false, streampos);
}

TEST_CASE("lhs var equals") {
    PTR(VarExpr) x = NEW(VarExpr)("x");
    PTR(NumExpr) pNum = NEW(NumExpr)(1);
    PTR(AddExpr) pAdd = NEW(AddExpr)(x, pNum);
    PTR(LetExpr) pLet = NEW(LetExpr)(x, pNum, pAdd);

    std::stringstream out;
}

TEST_CASE("Let equals") {
    PTR(VarExpr) x = NEW(VarExpr)("x");
    PTR(VarExpr) y = NEW(VarExpr)("y");
    PTR(NumExpr) pNum = NEW(NumExpr)(1);
    PTR(AddExpr) add = NEW(AddExpr)(x, pNum);
    PTR(LetExpr) let = NEW(LetExpr)(x, y, add);
    PTR(LetExpr) let2 = NEW(LetExpr)(x, y, add);
    CHECK(let->equals(let2));
}

TEST_CASE("BoolEquals") {
    PTR(Expr)f = NEW(BoolExpr)(false);
    PTR(Expr)T = NEW(BoolExpr)(true);
}

TEST_CASE("bool/num call val error checks"){
    PTR(NumVal)n = NEW(NumVal)(1);
    PTR(BoolVal)b = NEW(BoolVal)(true);
    CHECK_THROWS_WITH(n->call(n), "bad NumVal call");
    CHECK_THROWS_WITH(b->call(b), "bad BoolVal call");
}

TEST_CASE("additional call methods"){
    PTR(NumVal)n = NEW(NumVal)(1);
    PTR(BoolVal)b = NEW(BoolVal)(true);
    CHECK_THROWS_WITH(n->call(n), "bad NumVal call");
    CHECK_THROWS_WITH(b->call(b), "bad BoolVal call");
}

TEST_CASE("FunVal"){
    std::string strX = "x";
    PTR(VarExpr) varX = NEW(VarExpr)("x");
    PTR(NumVal)numval1 = NEW(NumVal)(1);
    PTR(NumVal)numval2 = NEW(NumVal)(2);
    PTR(NumVal)numval3 = NEW(NumVal)(3);
    PTR(NumExpr)num1 = NEW(NumExpr)(1);
    PTR(NumExpr)num2 = NEW(NumExpr)(2);
    PTR(AddExpr)addX1 = NEW(AddExpr)(varX, num1);
    PTR(AddExpr)addX2 = NEW(AddExpr)(varX, num2);
    PTR(AddExpr)addXX = NEW(AddExpr)(varX, varX);
    PTR(FunVal)xx1 = NEW(FunVal) (strX, addX1, Env::empty);
    PTR(FunVal)xx2 = NEW(FunVal) (strX, addX2, Env::empty);
    PTR(FunVal)xaddXX = NEW(FunVal) (strX, addXX, Env::empty);
    PTR(FunExpr)XX1 = NEW(FunExpr) (strX, addX1);

    CHECK((xx1->equals(xx1)) == true);
    CHECK((xx1->equals(xx2)) == false);
    CHECK((xx1->equals(nullptr)) == false);

    CHECK(xx1->to_expr()->equals(XX1));


    CHECK_THROWS_WITH(xx1->add_to(xx2), "cannot add FunVal");

    CHECK_THROWS_WITH(xx1->mult_with(xx2), "cannot mult FunVal");

    CHECK(xx1->is_true() == false);

    CHECK(xx1->call(numval1)->equals(numval2));
    CHECK(xx2->call(numval1)->equals(numval3));
    CHECK(xaddXX->call(numval1)->equals(numval2));
}

TEST_CASE("FunExpr"){
    std::string strX = "x";
    std::string strY = "y";
    PTR(VarExpr) varF = NEW(VarExpr)("f");
    PTR(VarExpr) varX = NEW(VarExpr)("x");
    PTR(VarExpr) varY = NEW(VarExpr)("y");
    PTR(NumVal)numval1 = NEW(NumVal)(1);
    PTR(NumVal)numval2 = NEW(NumVal)(2);
    PTR(NumExpr)num1 = NEW(NumExpr)(1);
    PTR(NumExpr)num2 = NEW(NumExpr)(2);
    PTR(AddExpr)addX1 = NEW(AddExpr)(varX, num1);
    PTR(AddExpr)addY1 = NEW(AddExpr)(varY, num1);
    PTR(AddExpr)addX2 = NEW(AddExpr)(varX, num2);
    PTR(FunVal)xx1 = NEW(FunVal) (strX, addX1, Env::empty);
    PTR(FunVal)xx2 = NEW(FunVal) (strX, addX2, Env::empty);
    PTR(FunExpr)XX1 = NEW(FunExpr) (strX, addX1);
    PTR(FunExpr)XX2 = NEW(FunExpr) (strX, addX2);
    PTR(FunExpr)XY1 = NEW(FunExpr) (strX, addY1);
    PTR(FunExpr)XfunXY1 = NEW(FunExpr) (strX, XY1);
    PTR(CallExpr)f1 = NEW(CallExpr)(varF, num1);
    PTR(LetExpr)letFunXX1 = NEW(LetExpr)(varF, XX1, f1);
    PTR(AddExpr)addFun = NEW(AddExpr)(XX1, num2);

    CHECK(XX1->equals(XX1) == true);
    CHECK(XX1->equals(XX2) == false);
    CHECK(XX1->equals(nullptr) == false);

    CHECK(XX1->interp(Env::empty)->equals(xx1) == true);
    CHECK(XX1->interp(Env::empty)->equals(xx2) == false);

}

TEST_CASE("CallExpr"){
    std::string strX = "x";
    std::string strY = "y";
    std::string strF = "f";
    PTR(VarExpr)  varF = NEW(VarExpr)("f");
    PTR(VarExpr)  varX = NEW(VarExpr)("x");
    PTR(VarExpr)  varY = NEW(VarExpr)("y");
    PTR(NumVal)  numval1 = NEW(NumVal)(1);
    PTR(NumVal)  numval2 = NEW(NumVal)(2);
    PTR(NumVal)  numval3 = NEW(NumVal)(3);
    PTR(NumExpr) num1 = NEW(NumExpr)(1);
    PTR(NumExpr) num2 = NEW(NumExpr)(2);
    PTR(NumExpr) num3 = NEW(NumExpr)(3);
    PTR(AddExpr) add11 = NEW(AddExpr)(num1, num1);
    PTR(AddExpr) addX1 = NEW(AddExpr)(varX, num1);
    PTR(AddExpr) addY1 = NEW(AddExpr)(varY, num1);
    PTR(AddExpr) addX2 = NEW(AddExpr)(varX, num2);
    PTR(AddExpr) addXX = NEW(AddExpr)(varX, varX);
    PTR(FunVal) xx1 = NEW(FunVal) (strX, addX1, Env::empty);
    PTR(FunVal) xx2 = NEW(FunVal) (strX, addX2, Env::empty);
    PTR(FunVal) xaddxx = NEW(FunVal) (strX, addXX, Env::empty);

    PTR(FunExpr) XX1 = NEW(FunExpr) (strX, addX1);
    PTR(FunExpr) X11 = NEW(FunExpr) (strX, add11);
    PTR(FunExpr) XX2 = NEW(FunExpr) (strX, addX2);
    PTR(FunExpr) XaddXX = NEW(FunExpr) (strX, addXX);
    PTR(FunExpr) XY1 = NEW(FunExpr) (strX, addY1);
    PTR(FunExpr) XfunXY1 = NEW(FunExpr) (strX, XY1);

    PTR(CallExpr) f1 = NEW(CallExpr)(varF, num1);
    PTR(CallExpr) f2 = NEW(CallExpr)(varF, num2);
    PTR(CallExpr) fx = NEW(CallExpr)(varF, varX);

    PTR(CallExpr) XX11 = NEW(CallExpr)(XX1, num1);
    PTR(CallExpr) X111 = NEW(CallExpr)(X11, num1);
    PTR(CallExpr) XX12 = NEW(CallExpr)(XX1, num2);
    PTR(LetExpr) letFunXX1 = NEW(LetExpr)(varF, XX1, f1);
    PTR(AddExpr) addFun = NEW(AddExpr)(XX1, num3);
    PTR(AddExpr) addCall = NEW(AddExpr)(XX11, num3);
    PTR(AddExpr) addFunCall = NEW(AddExpr)(letFunXX1, addCall);
    PTR(AddExpr) addCallFunCall = NEW(AddExpr)(addCall, addFunCall);
    PTR(CallExpr) callcall = NEW(CallExpr)(XX11, XX12);

    CHECK(f1->equals(f1) == true);
    CHECK(f1->equals(f2) == false);

    XX11->interp(Env::empty)->equals(numval2);
    CHECK(XX12->interp(Env::empty)->equals(numval3));

    CHECK(f1->to_string() == "f(1)");
    CHECK(XX11->to_string() == ("(_fun (x) (x+1))(1)"));
}
