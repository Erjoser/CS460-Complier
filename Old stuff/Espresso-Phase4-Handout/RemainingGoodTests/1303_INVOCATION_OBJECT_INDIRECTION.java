class A {
  void foo() {}

  void foo(int i) {}

  void foo(int i, double d) {}

  int foo_i() {}

  double foo_d() {}
}

class INVOCATION_OBJECT_INDIRECTION {
  static {
    A a = new A();
    
    a.foo(1);
    a.foo(1, 1.1);

    a.foo(a.foo_i());
    a.foo(a.foo_i(), a.foo_d());
  }
}