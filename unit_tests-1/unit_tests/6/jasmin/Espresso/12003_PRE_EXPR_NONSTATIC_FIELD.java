class A {
	int i;						// for demonstrating dup_x1 
	long l = 0l;			// for demonstrating dup2_x1
	float f = 0.f;		// for demonstrating typed instruction
	static {
		A a = new A();
		
		++(a.i);

		++(a.l);
		--(a.l);

		++(a.f);
		--(a.f);
	}
}