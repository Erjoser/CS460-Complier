class BINARY_EXPR_LSHIFT {
  static {
    double d;
    float f;

    long l;
    int i;
    char c;
    short s;
    byte b;

    boolean B;

    l = l << l;
    l = l << i;
    l = l << c;
    l = l << s;
    l = l << b;

    i = i << l;
    i = i << i;
    i = i << c;
    i = i << s;
    i = i << b;

    i = c << l;
    i = c << i;
    i = c << c;
    i = c << s;
    i = c << b;

    i = s << l;
    i = s << i;
    i = s << c;
    i = s << s;
    i = s << b;

    i = b << l;
    i = b << i;
    i = b << c;
    i = b << s;
    i = b << b;
  }
}
