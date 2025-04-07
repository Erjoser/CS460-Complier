class A {
	private void foo(char c, long l) {}
	void goo(A a, int i) {}

	static {
		(new A()).foo('\n', 0x1F625);
	}
}

class B extends A {
	void hoo() {
		super.goo(new A(), 0xA9);
	}
	static {}
}