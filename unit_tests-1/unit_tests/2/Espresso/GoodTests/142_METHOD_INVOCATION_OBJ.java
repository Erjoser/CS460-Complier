class METHOD_INVOCATION_LOCAL {
  public void foo_callee() {}
}

class Main {
  public static void main() {
    METHOD_INVOCATION_LOCAL M = new METHOD_INVOCATION_LOCAL();
    M.foo_callee();
  }
}