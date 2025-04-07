class BINARY_EXPR_OR_VOID {
  static void foo() {}
  static {
    int i;
    i = foo() | i;
  }
}
