class FIELD_REF_VISIT_RHS_EXPR_COMPLEX {
  static int i, j;
}

class A {
  static {
    FIELD_REF_VISIT_RHS_EXPR_COMPLEX.i = FIELD_REF_VISIT_RHS_EXPR_COMPLEX.j;
  }
}
