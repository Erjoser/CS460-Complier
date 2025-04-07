class TERNARY_INCOMPATIBLE_VOID_TO_INT {
  static void foo() {}
  static {
    int i;
    i = true ? 0 : foo();
  }
}
