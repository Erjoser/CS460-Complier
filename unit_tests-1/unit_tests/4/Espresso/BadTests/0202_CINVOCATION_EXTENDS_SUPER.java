
class CINVOCATION_EXTENDS_SUPER {

  public CINVOCATION_EXTENDS_SUPER() {}

  public CINVOCATION_EXTENDS_SUPER(int i) {}

}

class A extends CINVOCATION_EXTENDS_SUPER {

  public A() {}

  public A(int i) {
    super(i);
  }

  public A(float f) {
    super(f);
  }

}
