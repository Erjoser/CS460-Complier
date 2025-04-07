class A {

  public A() {}

  public A(int i) {}

  public A(int i, double d) {}

}

class NEW_PARAMS_LITERAL {
  static {
    A a_1 = new A(1);
    A a_2 = new A(1, 1.0);
  }
}