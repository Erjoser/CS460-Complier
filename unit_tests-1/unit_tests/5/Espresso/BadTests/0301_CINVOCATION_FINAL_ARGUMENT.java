class CINVOCATION_FINAL_ARGUMENT {
	public static final int i = 1;
  public CINVOCATION_FINAL_ARGUMENT(int j) {}
}

class A extends CINVOCATION_FINAL_ARGUMENT {
  public A() {
		super(i++);
  }
}
