class A { void f() {} }
class B extends A {}
class C extends B { void f() {} }
class IDENTICAL_RETURN_TYPES_LOCAL extends C { int f() {} }