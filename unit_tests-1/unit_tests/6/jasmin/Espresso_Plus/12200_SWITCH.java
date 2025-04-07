class A {
	static {
		int i = 0;
		String S = "a";

		switch (i) {
			case 0: break;
			default: break;
			case 1: break;
			case 4:
			case 8: break;
		}

		switch (S) {
			case "a": break;
			default: break;
			case "b": break;
			case "d": break;
			case "f": 
			case "i": break;
		}
	}
}