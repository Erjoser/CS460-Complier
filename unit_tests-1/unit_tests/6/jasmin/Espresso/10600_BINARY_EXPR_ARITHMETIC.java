class A {
	/*
	 * Don't over think this. 
	 * Get the ceiling of the sides and build the instruction
	 * using a string made of that type and the operator encoding.
	 */
	static {
		double d = 0;
    float f = 0;
    long l = 0;
    int i = 0;
    char c = 0;
    short s = 0;

		/*
		 * Decimal
		 */
		// Addition
		d = f + i;
		d = i + f;
    d = d + f;
    d = d + c;
    f = f + l;
    f = f + c;
    l = l + i;
    i = i + c;
    i = i + s;

		// Subtraction
    d = d - f;
    d = d - c;
    f = f - l;
    f = f - c;
    l = l - i;
    i = i - c;
    i = i - s;

		// Multiplication
    d = d * f;
    d = d * c;
    f = f * l;
    f = f * c;
    l = l * i;
    i = i * c;
    i = i * s;

		// Division
    d = d / f;
    d = d / c;
    f = f / l;
    f = f / c;
    l = l / i;
    i = i / c;
    i = i / s;

		// Modulation
    d = d % f;
    d = d % c;
    f = f % l;
    f = f % c;
    l = l % i;
    i = i % c;
    i = i % s;

		/*
		 * Bitwise
		 */
		boolean B = true;

		// AND
		B = B & B;
    i = i & i;
    i = i & s;
    i = c & s;
    i = s & c;

		// OR
		B = B | B;
    i = i | i;
    i = i | s;
    i = c | s;
    i = s | c;

		// XOR
		B = B ^ B;
    i = i ^ i;
    i = i ^ s;
    i = c ^ s;
    i = s ^ c;
	}
}