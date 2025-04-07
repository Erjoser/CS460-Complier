
abstract class A {
	public abstract void foo();
}

class B extends A {
	public void foo() {}
}

abstract class C extends B {
	public abstract void foo();
}

class CLASS_DECL_ABSTRACT_METHOD_NOT_IMPLEMENTED extends C {
}
