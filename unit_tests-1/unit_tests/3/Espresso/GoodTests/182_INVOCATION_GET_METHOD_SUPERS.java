class A { static void foo() {} }
class B extends A {}
class C extends B {}
class INVOCATION_GET_METHOD_SUPERS extends C { static { foo(); } }