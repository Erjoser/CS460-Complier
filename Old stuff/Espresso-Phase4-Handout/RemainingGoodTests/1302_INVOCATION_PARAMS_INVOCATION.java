class INVOCATION_PARAMS_INVOCATION {
  static void foo(int i) {}

  static void foo(int i, double d) {}

  static int foo_i() {}

  static double foo_d() {}

  static {
    foo(foo_i());
    foo(foo_i(), foo_d());
  }
}
