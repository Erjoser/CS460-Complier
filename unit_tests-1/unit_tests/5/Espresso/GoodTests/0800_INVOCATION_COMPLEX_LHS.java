class INVOCATION_COMPLEX_LHS {
  void foo() {}
}

class A {
  static {
    INVOCATION_COMPLEX_LHS I;
    I.foo();
  }
}