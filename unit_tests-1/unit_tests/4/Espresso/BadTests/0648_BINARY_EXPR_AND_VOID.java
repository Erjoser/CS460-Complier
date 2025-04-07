class BINARY_EXPR_AND_VOID {
  static void foo() {}
  static {
    int i;
    i = foo() & i;
  }
}
