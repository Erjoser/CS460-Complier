class INVOCATION_FINAL_ARGUMENT {
	static final int i = 1;
	static void foo(int i) {}
  static {
    foo(i++);
  }
}
