class A {}
class B extends A {}
class D extends B {}

class CAST_EXPR_ILLEGAL_CAST_EXTENDED {
  static {
    B b;
    D d;
    d = (B)d;
  }
}
