class A {
	static {
		// Note the difference and incorporate it into your code
		// Instruction
		int i0 = 0; 
		int i1 = 0; 
		int i2 = i0; // iload_0
		int i3 = i1; // iload_1
		int i4 = i2; // iload_2
		
		// SimpleInstruction
		int i5 = i4; // iload 4
		int i6 = i5; // iload 5
	}
}