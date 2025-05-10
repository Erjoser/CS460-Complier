class UNARY_POST_EXPR_COMPLEX_LHS {
  static int i;
}

class A {
  static {
    UNARY_POST_EXPR_COMPLEX_LHS U;
    U.i = ++U.i;
    U.i = --U.i;
  }
}
