class FOR_STAT_INIT_VAR_EXPR_INVOCATION_INCR_EMPTY {
  static boolean foo() {}
  static {
    for (int i = 0; foo();) {
      
    }
  }  
}
