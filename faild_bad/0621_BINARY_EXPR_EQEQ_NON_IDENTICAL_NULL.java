class BINARY_EXPR_EQEQ_NON_IDENTICAL_NULL {
  static void foo() {}
  static {
    boolean B;
    B = null == B;
  }
}
