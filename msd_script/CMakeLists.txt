cmake_minimum_required(VERSION 3.17)
project(msdscript)

set(CMAKE_CXX_STANDARD 14)

add_executable(msdscript main.cpp cmdline.cpp expr.cpp expr.h catch.h parse.cpp parse.h val.cpp val.h pointer.h env.cpp env.h)

#add_executable(test_msdscript exec.cpp)