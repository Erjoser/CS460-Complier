class STAT_EXPR_LOGIC {
  static boolean b;
  static int i;
  static {
    b = i < i;
    b = i > i;
    b = i <= i;
    b = i >= i;
    b = i instanceof i;
    b = i == i;
    b = i != i;
    i = i & i;
    i = i ^ i;
    i = i | i;
    b = b && b;
    b = b || b;
  }
}
