class A {}
class B extends A {}
class C extends B {}

/**
 * Explicit up casts require a check cast.
 */
class main {
	static {
		A a;
		B b;
		C c;

		a = (A)a;
		a = (A)b;
		a = (A)c;
		
		a = (B)a;	// CC
		a = (C)a;	// CC
		
		b = (B)b;
		b = (B)c;
		
		b = (C)b; // CC

		c = (C)c;
	}	
}