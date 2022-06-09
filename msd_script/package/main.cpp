#include <iostream>
#include "cmdline.h"

int main(int argc, char* argv[]) {
    try{
        use_arguments(argc, argv);
    } catch (std::runtime_error exn) {
        std::cerr << exn.what() << "\n";
        return 1;
    }
    return 0;
}
