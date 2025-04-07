/**
 * If the target of a static function is null the method
 * must have been defined in the current context and no 
 * reference is needed to invoke the function. 
 */
class A {
	static void foo(int i, A a) {}
	static {
		foo(0xFF, new A());
	}
}