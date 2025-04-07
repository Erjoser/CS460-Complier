class A {}
class B extends A {}
class C extends B {}

class TERNARY_CLASS_HEIRARCHY {
  static {
    B b;
    C c;
    c = true ? b : c;
  }
}
