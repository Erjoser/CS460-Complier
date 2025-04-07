/**
 * If the target of a static function is null the method
 * must have been defined in the current context and no 
 * reference is needed to invoke the function. 
 */
class A {
	static void foo() {}
	static {
		A.foo();

		A a = new A();
		a.foo();
	}
}

class B {
	static {
		A.foo();
	}
}