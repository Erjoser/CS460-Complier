class A {}
class B extends A {}
class C extends A {}
class D extends B {}

class CAST_EXPR_HEIRARCHY_CASTS {
  static {
    A a;
    B b;
    C c;
    D d;

    a = (B)a;
    a = (C)a;
    a = (D)a;

    b = (D)b;
  }
}
