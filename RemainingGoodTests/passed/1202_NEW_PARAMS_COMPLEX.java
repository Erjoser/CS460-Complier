class A {

  public A() {}

  public A(int i) {}

  public A(int i, double d) {}

}

class NEW {
  static int foo_i() {}

  static double foo_d() {}

  static {
    A a_0 = new A();
    A a_1 = new A(1);
    A a_2 = new A(1, 1.0);
    A a_3 = new A(foo_i());
    A a_4 = new A(foo_i(), foo_d());
  }
}