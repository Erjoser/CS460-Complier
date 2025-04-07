
class CINVOCATION_EXTENDS_THIS {

  public CINVOCATION_EXTENDS_THIS() {}

  public CINVOCATION_EXTENDS_THIS(int i) {}

  public CINVOCATION_EXTENDS_THIS(float f) {}

}

class A extends CINVOCATION_EXTENDS_THIS {

  public A() {}

  public A(int i) {
    this(1.1f);
  }

  public A(float f) {

  }

}
