class A {
	static {
		boolean B;

		int i = 0;
    char c = 0;
    short s = 0;
    byte b = 0;

		// EQ
		B = i == c;
    B = i == s;
    B = i == b;
    B = c == s;
    B = c == b;
    B = s == c;
    B = s == b;

		// NE
		B = i != c;
    B = i != s;
    B = i != b;
    B = c != s;
    B = c != b;
    B = s != c;
    B = s != b;

		// LT
		B = i < c;
    B = i < s;
    B = i < b;
    B = c < s;
    B = c < b;
    B = s < c;
    B = s < b;

		// LEQ
		B = i <= c;
    B = i <= s;
    B = i <= b;
    B = c <= s;
    B = c <= b;
    B = s <= c;
    B = s <= b;

		// GT
		B = i > c;
    B = i > s;
    B = i > b;
    B = c > s;
    B = c > b;
    B = s > c;
    B = s > b;

		// GE
		B = i >= c;
    B = i >= s;
    B = i >= b;
    B = c >= s;
    B = c >= b;
    B = s >= c;
    B = s >= b;

	}
}