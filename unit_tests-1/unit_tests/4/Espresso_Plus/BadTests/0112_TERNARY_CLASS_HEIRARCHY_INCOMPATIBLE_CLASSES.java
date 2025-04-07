class A {}
class B {}

class TERNARY_CLASS_HEIRARCHY_INCOMPATIBLE_CLASSES {
  static {
    A a;
    B b;
    b = true ? a : b;
  }
}
