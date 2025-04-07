class A {
	static {
		boolean B;
		A a1 = null;

		// EQ
		B = a1 == null;
		B = null == a1;
		B = null == null;

		// EQ
		B = a1 != null;
		B = null != a1;
		B = null != null;
	}
}