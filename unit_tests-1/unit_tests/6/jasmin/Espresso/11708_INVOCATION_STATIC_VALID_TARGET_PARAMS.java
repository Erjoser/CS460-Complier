/**
 * If the target of a static function is null the method
 * must have been defined in the current context and no 
 * reference is needed to invoke the function. 
 */
class A {
	static void foo(double d, float f) {}
	static {
		A.foo(5., 3.1415f);

		A a = new A();
		a.foo(5., 3.1415f);
	}
}

class B {
	static {
		A.foo(5., 3.1415f);
	}
}