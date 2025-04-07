class A {
	static {}
	void foo() {}
	void goo() {
		A a = new A();
		a.foo();
		this.foo();
	}
}