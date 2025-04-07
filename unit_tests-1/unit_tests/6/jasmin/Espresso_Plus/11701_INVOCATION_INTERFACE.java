interface I {
	public void foo();
	public void goo(int i);
	public int hoo(int i, double d);
	// static public void ioo() {} // Espresso doesn't support static interface methods
}

class A implements I {
	public void foo() {}
	public void goo(int i) {}
	public int hoo(int i, double d) {return 0;}
	static {
		A a = new A();

		((I)a).foo();
		((I)a).goo(5);
		((I)a).hoo(5, 5.);

		// I.ioo();
	}
}
