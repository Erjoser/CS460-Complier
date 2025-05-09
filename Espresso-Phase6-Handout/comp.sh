#!/bin/bash

# Check if a file path was provided
if [ -z "$1" ]; then
  echo "Usage: $0 <path-to-file>"
  exit 1
fi

FILE="$1"

# Run the three commands
echo "--------------- diff in c / cr----------------"
./espressoc "$FILE" > 1.txt

./espressocr "$FILE" > 2.txt

diff 1.txt 2.txt


echo "--------------- diff in Jasmin----------------"
./espressaoc "$FILE" > /dev/null 2>&1
./jasmin A.j > /dev/null 2>&1
./espressocr "$FILE" > /dev/null 2>&1
./jasmin A.rj > /dev/null 2>&1


diff A.j A.rj

echo "--------------- diff in .class----------------"
./espressaoc "$FILE" > /dev/null 2>&1
./jasmin A.j > /dev/null 2>&1
./espresso A > output.txt

./espressocr "$FILE" > /dev/null 2>&1
./jasmin A.rj > /dev/null 2>&1
./espresso A > ref_out.txt


diff output.txt ref_out.txt
