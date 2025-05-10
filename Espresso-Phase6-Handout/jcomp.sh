#!/bin/bash

# Check if a file path was provided
if [ -z "$1" ]; then
  echo "Usage: $0 <path-to-file>"
  exit 1
fi

FILE="$1"

# Run the three commands
./espressoc "$FILE"
./jasmin A.j
./espresso test.class > output.txt
./espressocr "$FILE"
./jasmin A.rj
./espresso test.class > ref_out.txt
diff A.rj A.j
