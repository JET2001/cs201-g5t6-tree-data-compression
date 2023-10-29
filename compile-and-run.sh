#!/bin/bash

# Remove the contents of the "compiled" directory
rm -rf compiled/*

# Remove the contents of the "Compressed" directory
rm -rf Compressed/*

# Remove the contents of the "Decompressed" directory
rm -rf Decompressed/*

# Compile and run your Java program
javac -d compiled -sourcepath . "app/App.java"

if [ $? -eq 0 ]; then
    echo "Compilation successful. Now running..."
    cd compiled
    java app.App
else
    echo "Compilation failed."
fi
