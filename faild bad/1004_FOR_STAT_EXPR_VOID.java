class FOO_STAT_EXPR_VOID {
  static void foo() {}
  static {
    for (; foo();) {

    }
  }
}
