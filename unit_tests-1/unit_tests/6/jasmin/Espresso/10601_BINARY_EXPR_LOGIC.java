class A {
	/*
	 * The reference compiler implements short circuit evaluation (SCE); so must yours. 
	 * Remember, the short circuit evaluation will of && will operate  differently from ||. 
	 */
	static {
		boolean B = true;
		
    B = B && true;
    B = B && false;
    B = true && B;
    B = false && B;	// SCE 
    B = true && false && B; // SCE
		
    B = B || true;
    B = B || false;
    B = true || B; // SCE
    B = false || B;
    B = false || true || B; // SCE
	}
}