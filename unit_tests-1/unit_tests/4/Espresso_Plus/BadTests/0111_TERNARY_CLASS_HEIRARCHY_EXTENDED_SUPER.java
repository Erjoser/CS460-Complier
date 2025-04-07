class A {}
class B extends A {}
class C extends B {}

class TERNARY_CLASS_HEIRARCHY_EXTENDED_SUPER {
  static {
    A a;
    C c;
    c = true ? a : c;
  }
}
