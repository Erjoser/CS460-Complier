class FIELD_REF_PRIVATE {
  static private int i;
}

class A {
  static {
    FIELD_REF_PRIVATE.i = 0;
  }  
}