class A {
  void foo_A() {}
}

class B extends A {
  void foo_B() {
    super.foo_A();
  }
}
