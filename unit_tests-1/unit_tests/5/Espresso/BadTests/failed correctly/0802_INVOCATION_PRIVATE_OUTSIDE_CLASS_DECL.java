class INVOCATION_PRIVATE {
  private static void foo() {}
}

class A {
  static {
    INVOCATION_PRIVATE.foo();
  }
}
