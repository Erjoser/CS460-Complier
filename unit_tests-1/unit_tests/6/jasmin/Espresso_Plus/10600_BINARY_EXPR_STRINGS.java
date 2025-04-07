class A {
	static {
		String S;

		S = "a" + 5;
		S = 5 + "a";
		S = "a" + S + 23;

		boolean B;

		String S1 = "abc";
		String S2 = "abc";
		B = S1 == S2;
		B = S1 != S2;

		S2 = "def";
		B = S1 == S2;
		B = S1 != S2;
	}
}