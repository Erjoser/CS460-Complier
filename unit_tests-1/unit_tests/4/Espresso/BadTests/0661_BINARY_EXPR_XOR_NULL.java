class BINARY_EXPR_XOR_NULL {
  static void foo() {}
  static {
    int i;
    i = null ^ i;
  }
}
