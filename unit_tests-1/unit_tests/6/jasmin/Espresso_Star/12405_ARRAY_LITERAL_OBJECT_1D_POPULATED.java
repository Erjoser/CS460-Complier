class A {}
class B extends A {}
class C extends B {
	static {
		A[] Aa = new A[]{
			new A(),
			new A(),
			new A()
		};

		A[] Aaa = new A[]{
			new A(),
			new B(),
			new C()
		};
	}
}