class INVOCATION_PARAMS_LITERAL {
  static void foo(int i) {}

  static void foo(int i, double d) {}

  static {
    foo(1);
    foo(1,1.1);
  }
}
