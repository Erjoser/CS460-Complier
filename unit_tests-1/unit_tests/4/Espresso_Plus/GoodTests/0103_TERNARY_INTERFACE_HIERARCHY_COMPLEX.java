interface N {}
interface J extends N {}
interface K extends N {}

interface I {}

class Y {}
class A extends Y implements I, J {}
class B extends Y implements I, K {}

class main {
	static {
		I i = true ? new A() : new B();
		Y y = true ? new A() : new B();
	}
}