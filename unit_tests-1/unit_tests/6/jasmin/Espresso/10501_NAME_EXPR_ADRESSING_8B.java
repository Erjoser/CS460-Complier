class A {
	static {
		double d0, d1, d2, d3;
		
		// Note the difference and incorporate it into your code
		// Instruction
		d0 = 0.; 
		d1 = d0; // dload_0
		// SimpleInstruction
		d2 = d1; // dload_2
		d3 = d2; // dload 4
	}
}