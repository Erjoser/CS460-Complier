class A {
	A(int i) {}
	A(long l, double d) {}
	A(float f, int b, A a) {}
	A(A a) {}

	static {
		A a = new A(5);
		A a1 = new A(5, 5 + 4.f);
		A a2 = new A(4 - 3.1415f, 0x4, new A(a1));
	}
}