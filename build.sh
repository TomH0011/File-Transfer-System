#!/bin/bash

echo "Building File Transfer System..."

# Create build directory
mkdir -p build

# Compile Java files
echo "Compiling Java files..."
javac -d build src/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Build successful!"
echo ""
echo "To run the GUI application:"
echo "  java -cp build FileTransferUI"
echo ""
echo "To run the command-line server:"
echo "  java -cp build Server"
echo ""
echo "To run the command-line client:"
echo "  java -cp build Client"
