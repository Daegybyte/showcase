CXX = c++
CXXFLAGS = --std=c++14 -O2

INCLUDES = cmdline.h pointer.h
OBJECTS = cmdline.o expr.o parse.o val.o env.o
MAINOBJECTS = main.o $(OBJECTS)

msdscript: $(MAINOBJECTS)
	$(CXX) $(CXXFLAGS) -o msdscript $(MAINOBJECTS)

libmsdscript.a: $(OBJECTS)
	ar -ruv libmsdscript.a $(OBJECTS)

main.o: main.cpp $(INCLUDES)
	$(CXX) $(CXXFLAGS) -c main.cpp

cmdline.o: cmdline.cpp $(INCLUDES)
	$(CXX) $(CXXFLAGS) -c cmdline.cpp

expr.o: expr.cpp $(INCLUDES)
	$(CXX) $(CXXFLAGS) -c expr.cpp

parse.o: parse.cpp $(INCLUDES)
	$(CXX) $(CXXFLAGS) -c parse.cpp

val.o: val.cpp $(INCLUDES)
	$(CXX) $(CXXFLAGS) -c val.cpp

.PHONEY: test
test: msdscript
	./msdscript --test

##############################################################
####################### test_msdscript #######################
##############################################################

TEST_INCLUDES = test_msdscript.h
TEST_OBJECTS = test_msdscript.o exec.o
test_msdscript: $(TEST_OBJECTS)
	$(CXX) $(CXXFLAGS) -o test_msdscript $(TEST_OBJECTS)

test_msdscript.o: test_msdscript.cpp $(TEST_INCLUDES)
	$(CXX) $(CXXFLAGS) -c test_msdscript.cpp

exec.o: exec.cpp $(TEST_INCLUDES)
	$(CXX) $(CXXFLAGS) -c exec.cpp
