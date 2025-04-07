class STAT_EXPR_PRE_POST {
  static int i, j;
  static double d;
  static {
    ++i;
    --i;
    i++;
    i--;
    i = i + ++i;
    i = i - ++i;
    i = -i;
    i = +i;
    j = ~i + 1;
    j = !i + 1;
    d = (double)i;
  }
}