interface A {
  void foo();

  int foo_i();

  double foo_d();
}

class CLASS_DECL_INTERFACE_ABSTRACT_METHOD_IMPLEMENTATION implements A {
  public void foo() {

  }

  public double foo_d() {
    return 0;
  }

  public int foo_i() {
    return 0;
  }

}
