class A {
	static {
		int i = 0;
		
		float f = i;
		f = (float)i;

		double d = 0;
		d = f;

		byte b = (byte)i;
		char c = (char)i;
		short s = (short)i;
	}
}