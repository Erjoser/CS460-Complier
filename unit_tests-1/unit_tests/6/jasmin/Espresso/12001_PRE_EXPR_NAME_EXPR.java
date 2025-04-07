class A {
	static {
		int i = 0;			// for integer iinc instructions
		long l = 0l;		// for demonstrating dup2 and typed instruction
		float f = 0.f;	// for demonstrating dup  and typed instruction
		double d = 0.;	// for demonstrating complex store instruction

		++i;
		--i;

		++l;
		--l;

		++f;
		--f;

		++d;
		--d;
	}
}