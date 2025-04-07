class X {}
class Y extends X {}
class U extends Y {}

interface L {}
interface K {}
interface I extends K {}
interface J extends K {}

class A extends Y implements I, L {}
class B extends U implements J, L {}

class main {
	static {
		Y y = true ? new A() : new B();
		y = true ? new Y() : new U();
		X x = true ? new X() : new U();
	}
}