class A {
	static {}
	void foo(int i, double d) {}
	void goo() {
		A a = new A();
		a.foo(4, 5 + 4.);
		this.foo(5, 4. - 3.1415);
	}
}