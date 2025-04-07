class BINARY_EXPR_OROR {
  static {
    double d;
    float f;

    long l;
    int i;
    char c;
    short s;
    byte b;

    boolean B;

    B = B || B;
    B = B || true;
    B = B || false;
    B = true || B;
    B = false || B;
  }
}
