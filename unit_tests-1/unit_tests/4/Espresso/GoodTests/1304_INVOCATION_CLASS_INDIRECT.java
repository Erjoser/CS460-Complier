class A {
  static void foo() {}

  static void foo(int i) {}

  static void foo(int i, double d) {}

  static int foo_i() {}

  static double foo_d() {}
}

class INVOCATION_CLASS_INDIRECTION {
  static {
    A.foo(1);
    A.foo(1, 1.1);

    A.foo(A.foo_i());
    A.foo(A.foo_i(), A.foo_d());
  }
}