#pragma once
#include "pointer.h"
#include <string>
class ExecResult {
public:
    int exit_code;
    std::string out;
    std::string err;
    ExecResult() {
        exit_code = 0;
        out = "";
        err = "";
    }
};
extern ExecResult exec_program(int argc, const char * const *argv, std::string
input);
