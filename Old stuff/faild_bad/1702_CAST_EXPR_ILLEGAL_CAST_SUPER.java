class A {}
class B extends A {}

class CAST_EXPR_ILLEGAL_CAST_SUPER {
  static {
    A a;
    B b;
    b = (A)b;
  }
}
