class A { static int a; } 
class B extends A {}
class C extends B {}
class FIELD_REF_GET_FIELD_SUPERS extends C { static { a++; } }