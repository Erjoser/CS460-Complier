class A {}
class B extends A {}
class D extends B {}

class CAST_EXPR_ILLEGAL_CAST_EXTENDED_SUPER {
  static {
    A a;
    D d;
    d = (A)d;
  }
}
