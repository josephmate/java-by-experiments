# Compiler
CXX = g++

# Compiler flags
CXXFLAGS = -std=c++11 -pthread

# Target executable name
TARGET = SimpleRepro

# Source file
SRC = main.cpp

# Build target
all: $(TARGET)

$(TARGET): $(SRC)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(SRC)

# Clean up build files
clean:
	rm -f $(TARGET)

.PHONY: all clean

