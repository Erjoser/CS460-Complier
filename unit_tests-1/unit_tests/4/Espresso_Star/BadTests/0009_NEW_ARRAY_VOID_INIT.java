class NEW_ARRAY_VOID_INIT {
  static void foo() {}
  static {
    int[] i = new int[] { 1, 2, 3, 4, foo() };
  }
}
