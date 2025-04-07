class BINARY_EXPR_OR_NULL {
  static void foo() {}
  static {
    int i;
    i = null | i;
  }
}
