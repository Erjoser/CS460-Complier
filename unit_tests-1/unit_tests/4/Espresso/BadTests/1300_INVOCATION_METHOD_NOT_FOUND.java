class A {
  static void foo(int i) {}
  static void foo(int i, double d) {}
}

class INVOCATION_METHOD_NOT_FOUND {
  static {
    A.foo();
  }  
}
