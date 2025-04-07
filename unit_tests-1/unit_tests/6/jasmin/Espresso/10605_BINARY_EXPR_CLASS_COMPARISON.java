class A {
	static {
		boolean B;
		A a1 = null;
		A a2 = null;

		// EQ
		B = a1 == a1;
		B = a1 == a2;
		B = a2 == a1;
		B = a2 == a2;

		// NE
		B = a1 != a1;
		B = a1 != a2;
		B = a2 != a1;
		B = a2 != a2;

	}
}