class BINARY_EXPR_AND_NULL {
  static void foo() {}
  static {
    int i;
    i = null & i;
  }
}
