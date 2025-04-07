class A {
	static int[] sia = new int[] { 1, 2, 3 };
	int[] ia;
	
	A(int len) {
		ia = new int[len];
	}

	static {
		A a = new A(3);
		a.ia[0] = 3;

		A.sia[0] = 4;		
	}
}