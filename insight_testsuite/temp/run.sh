#!/bin/bash
clear
mkdir -p bin
echo "---------Compiling Java classes---------"
echo
javac src/Marketeer.java -cp src/lib/json-simple-1.1.1.jar -d ./bin/
echo "---------Executing Java classes---------"
echo
java -cp bin/:src/lib/json-simple-1.1.1.jar Marketeer
echo "---------DONE---------"