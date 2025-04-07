class BINARY_EXPR_ANDAND_VOID {
  static void foo() {}
  static {
    boolean B;
    B = foo() && B;
  }
}
