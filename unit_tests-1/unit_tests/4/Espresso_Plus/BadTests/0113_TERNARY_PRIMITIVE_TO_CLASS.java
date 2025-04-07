class A {}
class B extends A{}

class TERNARY_PRIMITIVE_TO_CLASS {
  static {
    A a;
    B b;
    b = true ? a : 5;
  }
}
