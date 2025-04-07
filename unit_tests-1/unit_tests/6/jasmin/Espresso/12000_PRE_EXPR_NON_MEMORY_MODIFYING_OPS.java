class A {
	static {
		boolean b = false;
		int i = 1;
		long l = 1;
		float f = 1;
		double d = 1;

		i = -i;
		l = -l;
		f = -f;
		d = -d;

		i = +i;
		l = +l;
		f = +f;
		d = +d;
		
		i = ~i;
		l = ~l;

		b = !b;

	}
}