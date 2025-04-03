class BINARY_EXPR_AND {
  static {
    int i;
    char c;
    short s;
    byte b;

    boolean B; 
    
    B = B & B;
    
    i = i & i;
    i = i & c;
    i = i & s;
    i = i & b;

    i = c & i;
    i = c & c;
    i = c & s;
    i = c & b;

    i = s & i;
    i = s & c;
    i = s & s;
    i = s & b;

    i = b & i;
    i = b & c;
    i = b & s;
    i = b & b;
  }
}
