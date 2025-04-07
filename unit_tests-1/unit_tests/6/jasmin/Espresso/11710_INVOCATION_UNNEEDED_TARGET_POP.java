class A {
	static void foo() {}
	static {
		A a = new A();
		a.foo();
		System.out.println(5);
	}
}