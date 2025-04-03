class BINARY_EXPR_PLUS {
  static {
    double d;
    float f;

    long l;
    int i;
    char c;
    short s;
    byte b;

    boolean B;

    d = d + d;
    d = d + f;
    d = d + l;
    d = d + i;
    d = d + c;
    d = d + s;
    d = d + b;

    f = f + f;
    f = f + l;
    f = f + i;
    f = f + c;
    f = f + s;
    f = f + b;

    l = l + l;
    l = l + i;
    l = l + c;
    l = l + s;
    l = l + b;

    i = i + i;
    i = i + c;
    i = i + s;
    i = i + b;

    // c = c + c;
    // c = c + s;
    // c = c + b;

    // s = s + s;
    // s = s + b;

    // b = b + b;
  }
}
