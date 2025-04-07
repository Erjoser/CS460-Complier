class UNARY_PRE_EXPR_COMP_VOID {
  static void foo() {}
  static {
    double d;
    d = ~foo();
  }
}
