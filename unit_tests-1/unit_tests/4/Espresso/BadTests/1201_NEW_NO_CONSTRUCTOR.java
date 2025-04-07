class A {
  public A(int i) {}
}

class NEW_PARAMS_LITERAL {
  static {
    A a = new A("hello");
  }
}