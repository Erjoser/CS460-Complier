class A {
	static {
		boolean B;

		double d = 0;
    float f = 0;
    long l = 0;
		int i = 0;

		// EQ
		B = d == f;
    B = f == l;
    B = l == i;

		// NE
		B = d != f;
    B = f != l;
    B = l != i;

		// LT
		B = d < f;
    B = f < l;
    B = l < i;

		// LEQ
		B = d <= f;
    B = f <= l;
    B = l <= i;

		// GT
		B = d > f;
    B = f > l;
    B = l > i;

		// GE
		B = d >= f;
    B = f >= l;
    B = l >= i;

	}
}