class ARRAY_ACCESS_EXPR_VOID {
  static void foo() {}
  static {
    int[] a;
    int i = a[foo()];
  }
}
