interface A { int a(); }

class INVOCATION_GET_METHOD_INTERFACE implements A {
  static { a(); }
}