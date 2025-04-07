class TERNARY_VOID {
  static void foo() {}
  static {
    int i;
    i = foo() ? 0 : 1;
  }
}
