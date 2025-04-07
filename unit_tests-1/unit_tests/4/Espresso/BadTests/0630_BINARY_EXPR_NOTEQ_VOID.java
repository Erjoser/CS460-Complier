class BINARY_EXPR_NOTEQ_VOID {
  static void foo() {}
  static {
    boolean B;
    B = foo() != foo();
  }
}
