class UNARY_POST_EXPR {
  static {
    double d;
    float f;

    long l;
    int i;
    char c;
    short s;
    byte b;

    d = +d;
    f = +f;
    l = +l;
    i = +i;
    i = +c;
    i = +s;
    i = +b;

    d = -d;
    f = -f;
    l = -l;
    i = -i;
    i = -c;
    i = -s;
    i = -b;

    l = ~l;
    i = ~i;
    i = ~c;
    i = ~s;
    i = ~b;

    ++d;
    ++f;
    ++l;
    ++i;
    ++c;
    ++s;
    ++b;
  }  
}
