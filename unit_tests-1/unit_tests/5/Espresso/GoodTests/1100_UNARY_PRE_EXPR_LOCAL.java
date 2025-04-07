class UNARY_POST_EXPR_LOCAL {
  static int i;

  static {
    i = ++i;
    i = --i;
  }
}
