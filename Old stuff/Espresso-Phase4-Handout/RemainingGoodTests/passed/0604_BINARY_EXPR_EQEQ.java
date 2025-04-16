class BINARY_EXPR_EQEQ {
  static {
    double d;
    float f;

    long l;
    int i;
    char c;
    short s;
    byte b;

    boolean B;

    B = d == d;
    B = d == f;
    B = d == l;
    B = d == i;
    B = d == c;
    B = d == s;
    B = d == b;

    B = f == d;
    B = f == f;
    B = f == l;
    B = f == i;
    B = f == c;
    B = f == s;
    B = f == b;

    B = l == d;
    B = l == f;
    B = l == l;
    B = l == i;
    B = l == c;
    B = l == s;
    B = l == b;

    B = i == d;
    B = i == f;
    B = i == l;
    B = i == i;
    B = i == c;
    B = i == s;
    B = i == b;

    B = c == d;
    B = c == f;
    B = c == l;
    B = c == i;
    B = c == c;
    B = c == s;
    B = c == b;

    B = s == d;
    B = s == f;
    B = s == l;
    B = s == i;
    B = s == c;
    B = s == s;
    B = s == b;

    B = b == d;
    B = b == f;
    B = b == l;
    B = b == i;
    B = b == c;
    B = b == s;
    B = b == b;
  }
}
