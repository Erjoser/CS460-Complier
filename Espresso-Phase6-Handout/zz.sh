#!/bin/bash

# Check if a file path was provided
if [ -z "$1" ]; then
  echo "Usage: $0 <path-to-file>"
  exit 1
fi

FILE="$1"

# Run the three commands
./comp.sh "$FILE"
./jcomp.sh "$FILE"

