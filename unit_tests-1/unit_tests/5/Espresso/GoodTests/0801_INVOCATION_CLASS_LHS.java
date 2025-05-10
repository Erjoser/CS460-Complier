class INVOCATION_COMPLEX_LHS {
  static void foo() {}
}

class A {
  static {
    INVOCATION_COMPLEX_LHS.foo();
  }
}