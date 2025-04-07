class BINARY_EXPR_OROR_VOID {
  static void foo() {}
  static {
    boolean B;
    B = foo() || B;
  }
}
