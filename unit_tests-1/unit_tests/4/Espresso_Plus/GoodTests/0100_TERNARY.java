class TERNARY {
  static {
    double d;
    int i;
    boolean B;
    String S;
    TERNARY T;

    d = B ? 1 : 0.;
    d = B ? 1. : 0;

    i = B ? 1 : 0;
    i = B ? 1 : 0;

    B = B ? true : false;
    B = B ? true : false;

    S = B ? "true" : "flase";
    S = B ? "true" : "flase";

    T = B ? new TERNARY() : new TERNARY();
    T = B ? new TERNARY() : new TERNARY();

    T = B ? new TERNARY() : null;
    T = B ? null : new TERNARY();
  }
}
