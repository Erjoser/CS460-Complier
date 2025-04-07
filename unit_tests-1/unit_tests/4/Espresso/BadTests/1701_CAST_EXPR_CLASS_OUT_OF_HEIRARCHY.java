class A {}
class B {}
class CAST_EXPR_CLASS_OUT_OF_HEIRARCHY {
  static {
    A a = new A();
    a = (B)a;
  }  
}
