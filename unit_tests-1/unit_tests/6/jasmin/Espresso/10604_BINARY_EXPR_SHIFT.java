class A {
	static {
    long l = 0;
    int i = 0;
    char c = 0;
    short s = 0;

		// SHL
    l = l << i;
    i = i << c;
    i = i << s;
    l = l << l;

		// SHR
    l = l >> i;
    i = i >> c;
    i = i >> s;
    l = l >> l;

		// USHR
    l = l >>> i;
    i = i >>> c;
    i = i >>> s;
    l = l >>> l;
	}
}