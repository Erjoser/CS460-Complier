class NEW_ARRAY_VOID_LEN {
  static void foo() {}
  static {
    int[] i = new int[foo()];
  }  
}
