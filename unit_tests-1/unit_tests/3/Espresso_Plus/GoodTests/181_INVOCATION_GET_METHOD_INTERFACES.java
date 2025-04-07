interface A {}
interface B {}
interface C { int c(); }
class NAME_EXPR_GET_FIELD_INTERFACE implements A, B, C {
  static { c(); }
}