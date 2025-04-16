class UNARY_PRE_EXPR_MINUS_CLASS_TYPE {
  static void foo() {}
  static {
    UNARY_PRE_EXPR_MINUS_CLASS_TYPE U;
    U = -U;
  }
}
