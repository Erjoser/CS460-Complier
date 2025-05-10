class NEW_FINAL_ARGUMENT { 
	public static final int i = 1;
  public NEW_FINAL_ARGUMENT(int j) {}
}

class A {
  static {
    NEW_FINAL_ARGUMENT foo = new NEW_FINAL_ARGUMENT(NEW_FINAL_ARGUMENT.i++);
  }
}