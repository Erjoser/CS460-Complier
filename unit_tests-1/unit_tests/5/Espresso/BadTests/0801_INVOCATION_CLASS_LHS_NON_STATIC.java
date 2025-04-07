class INVOCATION_COMPLEX_LHS_NON_STATIC {
  void foo() {}
}

class A {
  static {
    INVOCATION_COMPLEX_LHS_NON_STATIC.foo();
  }
}