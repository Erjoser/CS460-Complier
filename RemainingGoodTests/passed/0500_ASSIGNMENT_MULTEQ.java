class ASSIGNMENT_MULTEQ {
  static {
    double d;
    float f;

    long l;
    int i;
    char c;
    short s;
    byte b;

    d *= d;
    d *= f;
    d *= l;
    d *= i;
    d *= c;
    d *= s;
    d *= b;

    f *= f;
    f *= l;
    f *= i;
    f *= c;
    f *= s;
    f *= b;

    l *= l;
    l *= i;
    l *= c;
    l *= s;
    l *= b;
    
    i *= i;
    i *= c;
    i *= s;
    i *= b;
    
    c *= c;
    c *= s;
    c *= b;
    
    s *= s;
    s *= b;
    
    b *= b;
  }
}
