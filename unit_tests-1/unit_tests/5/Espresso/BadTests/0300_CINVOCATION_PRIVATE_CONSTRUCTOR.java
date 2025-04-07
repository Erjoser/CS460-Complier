class CINVOCATION_PRIVATE_CONSTRUCTOR {
  private CINVOCATION() {}
}

class A extends CINVOCATION_PRIVATE_CONSTRUCTOR {
  public A() {
    super();
  }
}
