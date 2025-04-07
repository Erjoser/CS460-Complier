class BINARY_EXPR_EQEQ_VOID {
  static void foo() {}
  static {
    boolean B;
    B = foo() == foo();
  }
}
