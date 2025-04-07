class A {
	private void foo() {}
	void goo() {}

	static {
		(new A()).foo();
	}
}

class B extends A {
	void hoo() {
		super.goo();
	}
	static {}
}