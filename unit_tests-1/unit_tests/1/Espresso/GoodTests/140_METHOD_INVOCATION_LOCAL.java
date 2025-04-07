class METHOD_INVOCATION_LOCAL {
  public void foo_callee() {}

  public void foo_caller() {
    foo_callee();
    this.foo_callee();
  }
}
