class ARRAY_ACCESS_EXPR_ACCESSING_NON_ARRAY_TYPE_ {
  static void foo() {}
  static {
    int i;
    i = foo()[i];
  }
}
