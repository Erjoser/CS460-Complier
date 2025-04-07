class ASSIGNMENT_RSHIFTEQ {
  static {
    long l;
    int i;
    char c;
    short s;
    byte b;

    l >>= l;
    l >>= i;
    l >>= c;
    l >>= s;
    l >>= b;
    
    i >>= l;
    i >>= i;
    i >>= c;
    i >>= s;
    i >>= b;
    
    c >>= l;
    c >>= i;
    c >>= c;
    c >>= s;
    c >>= b;
    
    s >>= l;
    s >>= i;
    s >>= c;
    s >>= s;
    s >>= b;
    
    b >>= l;
    b >>= i;
    b >>= c;
    b >>= s;
    b >>= b;
  }
}
