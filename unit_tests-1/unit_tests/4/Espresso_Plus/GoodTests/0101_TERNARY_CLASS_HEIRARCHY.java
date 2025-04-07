class A {}
class B extends A {}
class C extends B {}

class TERNARY_CLASS_HEIRARCHY {
  static {
    A a;
    B b;
    C c;
    a = true ? b : b;
    a = true ? c : c;
    a = true ? b : c;
    b = true ? b : c;
    b = true ? c : b;
    c = true ? c : c;
  }
}
