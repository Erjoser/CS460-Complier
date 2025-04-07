class INVOCATION {
  static String S;
  static char c;
  static int i;

  static {
    i = S.length();
    c = S.charAt(i);
  }
}