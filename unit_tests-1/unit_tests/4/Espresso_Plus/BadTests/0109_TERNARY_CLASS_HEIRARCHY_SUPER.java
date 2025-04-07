class A {}
class B extends A {}
class C extends B {}

class TERNARY_CLASS_HEIRARCHY {
  static {
    A a;
    B b;
    b = true ? a : b;
  }
}
