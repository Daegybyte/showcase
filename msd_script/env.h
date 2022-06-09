//
// Created by Diego on 21/03/22.
//

#pragma once
#include "pointer.h"
#include <string>
class Val;


CLASS(Env) {
public:
    virtual PTR(Val) lookup(std::string find_name) = 0;
    static PTR(Env) empty;
};

class EmptyEnv : public Env {
public:
    EmptyEnv();
    PTR(Val) lookup(std::string find_name);
};

class ExtendedEnv : public Env {
public:
    std::string name;
    PTR(Val) val;
    PTR(Env) rest;
    ExtendedEnv(std::string name, PTR(Val) val, PTR(Env) rest);
    PTR(Val) lookup(std::string find_name);
};