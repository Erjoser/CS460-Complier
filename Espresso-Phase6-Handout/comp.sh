#!/bin/bash

# Check if a file path was provided
if [ -z "$1" ]; then
  echo "Usage: $0 <path-to-file>"
  exit 1
fi

FILE="$1"

# Run the three commands
./espressoc "$FILE" > 1.txt
./espressocr "$FILE" > 2.txt
diff 1.txt 2.txt
