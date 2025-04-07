class A {
  int i;
}

class B extends A {
  int j;

  B() {
    j = super.i;
  }
}

class Main {
  static {
    B b = new B();
    b.j = 1;
  }
}
