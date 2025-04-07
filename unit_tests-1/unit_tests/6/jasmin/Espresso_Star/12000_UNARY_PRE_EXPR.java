class A {
	static {
		int[] ia = new int[] { 0, 1, 2 };

		++ia[0];
		++ia[1];
		++ia[2];

		--ia[0];
		--ia[1];
		--ia[2];
	}
}