class A {}
class B extends A {}
class CAST_EXPR_TARGET_CLASS_NAME {
  static {
    A a = (B)A;
  }  
}
