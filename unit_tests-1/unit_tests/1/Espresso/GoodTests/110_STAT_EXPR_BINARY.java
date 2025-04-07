class STAT_EXPR_BINARY {
  static int i;
  static {
    i = i * i;
    i = i / i;
    i = i % i;
    i = i + i;
    i = i - i;
    i = i << i;
    i = i >> i;
    i = i >>> i;
  }
}
