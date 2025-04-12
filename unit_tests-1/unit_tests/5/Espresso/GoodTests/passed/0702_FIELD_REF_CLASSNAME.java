class FIELD_REF_CLASSNAME {
  static int i;
}

class A {
  static {
    FIELD_REF_CLASSNAME.i = 0;
  }
}
