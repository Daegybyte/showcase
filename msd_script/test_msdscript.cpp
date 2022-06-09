#include <iostream>
#include "exec.h"
#include "test_msdscript.h"
#include <time.h>

int main(int argc, char **argv) {
    srand(clock());
    if (argc > 3 || argc == 1) {
        throw std::runtime_error("argc count issue");
    } else if (argc == 2) {
        const char *const interp_argv[] = {argv[1], "--interp"};
        const char *const print_argv[] = {argv[1], "--print"};
        for (int i = 0; i < 100; i++) {

            std::string in = random_expr_print();
            std::cout << "trying: " << in << "\n";

            ExecResult interp_result = exec_program(2, interp_argv, in);
            ExecResult print_result = exec_program(2, print_argv, in);
            ExecResult interp_again_result = exec_program(2, interp_argv, print_result.out);


            if (interp_again_result.out != interp_result.out) {
                std::cerr << "\ninterp '" << interp_result.out << "'\n";
                std::cerr << "print '" << print_result.out << "'\n";
                throw std::runtime_error("different result for printed");
            }
        }

    } else {
//        const char *commands[] = {"--interp", "--print", "--pretty_print"};
        const char *commands[] = {"--interp", "--print", "--pretty_print"};
        int numCommands = sizeof(commands) / sizeof(void *);

        std::string(*functptr[])() = {checkParentheses, toZero, random_expr_print,
                                      addRand, multRand, randNum,whiteSpace};
        int numFuncs = sizeof(functptr) / sizeof(void *);

        for (int cmd = 0; cmd < numCommands; cmd++) {
            for (int fnct = 0; fnct < numFuncs; fnct++) {
                for (int j = 0; j < 100; j++) {
                    const char *const interp1_argv[] = {argv[1], commands[cmd]};
                    const char *const interp2_argv[] = {argv[2], commands[cmd]};

                    std::string in = (*functptr[fnct])();
                    std::cout << j << " trying: " << in << "\n";

                    ExecResult interp1_result = exec_program(2, interp1_argv, in);
                    ExecResult interp2_result = exec_program(2, interp2_argv, in);

                    if (interp1_result.out != interp2_result.out) {
                        std::cerr << "\nfailed " << commands[cmd] << in << std::endl;
                        std::cerr << "argv[1] '" << interp1_result.out << "'\n";
                        std::cerr << "argv[2] '" << interp2_result.out << "'\n";
                        throw std::runtime_error("different results");
                    }
                }
            }
        }
    }
    return 0;
}

std::string random_expr_print() {
    int weight = rand() % 10;
    if (weight < 6) //number
        return std::to_string(rand());
    else if (weight < 7) { //add
        return random_expr_print() + "+" + random_expr_print();
    } else { //mult
        return random_expr_print() + "*" + random_expr_print();
    }
    //TODO
    //boolexpr

    //eqexpr

    //ifexpr
}

std::string toZero() {
    std::string str = std::to_string(rand());
    return str + "*0";
}

std::string addRand() {
    return std::to_string(rand()) + "+" + std::to_string(rand());
}

std::string multRand() {
    return std::to_string(rand()) + "*" + std::to_string(rand());
}

std::string randNum() {
    return std::to_string(rand());
}

std::string whiteSpace() {
    int weight = rand() % 15;
    if (weight < 4) {
        return " " + std::to_string(rand()) + std::to_string(rand());
    } else if (weight < 8) {
        return std::to_string(rand()) + std::to_string(rand()) + " ";
    } else if (weight < 10) {
        return std::to_string(rand()) + "* " + std::to_string(rand());
    } else if (weight < 12) {
        return std::to_string(rand()) + " *" + std::to_string(rand());
    } else if (weight < 14) {
        return std::to_string(rand()) + "+ " + std::to_string(rand());
    } else if (weight < 15) {
        return " " + std::to_string(rand()) + " +" + std::to_string(rand()) + " ";
    } else {
        return " " + std::to_string(rand()) + " * " + std::to_string(rand()) + " ";
    }
}

std::string checkParentheses() {
    int heads = rand() % 2;
    if (heads) {
        return " ( LetExpr var = " + random_expr_print() + " _in "
               + random_expr_print() + "+" + random_expr_print() + " ) ";
    }
    return " ( LetExpr var = " + random_expr_print() + " _in " + random_expr_print() + " ) ";
}
