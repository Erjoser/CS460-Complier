class BINARY_EXPR_XOR_VOID {
  static void foo() {}
  static {
    int i;
    i = foo() ^ i;
  }
}
