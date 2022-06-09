#define CATCH_CONFIG_RUNNER
#include <iostream>
#include "cmdline.h"
#include "expr.h"
#include "parse.h"
#include "catch.h"
#include "val.h"
#include "env.h"
/**
 * Takes in multiple user input parameters and outputs to terminal.
 */
void use_arguments(int argc, char *argv[]) {
    bool tested = false;
    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "--help") == 0) {
            std::cout << "Available commands:" << std::endl;
            std::cout << "--help" << std::endl;
            std::cout << "--test" << std::endl;
            std::cout << "--interp" << std::endl;
            std::cout << "--print" << std::endl;
            std::cout << "--pretty-print" << std::endl;
            exit(0);

        } else if (strcmp(argv[i], "--test") == 0 && !tested) {
            tested = true;
            if(Catch::Session().run() != 0){
                exit(1);
            }
            continue;

        } else if (strcmp(argv[i], "--test") == 0 && tested) {
            std::cerr << "Already tested" << std::endl;
            exit(1);

        } else if (strcmp(argv[i], "--interp") == 0 ) {
            std::string userInput = (parse(std::cin)->interp(Env::empty)->to_string());
            std::cout << userInput << "\n";
            exit(0);

        } else if (strcmp(argv[i], "--print") == 0 ) {
            parse(std::cin)->print(std::cout);
            std::cout << "\n";
            exit(0);

        } else if (strcmp(argv[i], "--pretty-print") == 0 ) {
            parse(std::cin)->pretty_print(std::cout);
            std::cout << "\n";
            exit(0);

        } else {
            std::cerr << "Error - argv invalid input" << std::endl;
            exit(1);
        }
    }
    return;
}