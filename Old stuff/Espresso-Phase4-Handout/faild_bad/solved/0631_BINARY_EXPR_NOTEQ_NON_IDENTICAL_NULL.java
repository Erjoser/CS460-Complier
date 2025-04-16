class BINARY_EXPR_NOTEQ_NON_IDENTICAL_NULL {
  static void foo() {}
  static {
    boolean B;
    B = null != B;
  }
}
