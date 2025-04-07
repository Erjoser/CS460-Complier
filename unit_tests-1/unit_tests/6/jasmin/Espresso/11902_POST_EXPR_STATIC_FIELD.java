class A {
	static int i;						// for demonstrating necessary object pop 
	static long l = 0l;			// for demonstrating dup2
	static float f = 0.f;		// for demonstrating typed instruction
	static {
		i++;
		A.i++;

		A a = new A();
		a.i++;

		l++;
		l--;

		f++;
		f--;
	}
}