class CONSTRUCTOR_DECL {
  int i, j;

  CONSTRUCTOR_DECL(int i) {
    this.i = i;
  }

  CONSTRUCTOR_DECL() {
    CONSTRUCTOR_DECL(5);
    this.j = 6;
  }
}
