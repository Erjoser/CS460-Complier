class CAST_EXPR_NUMERIC {
  static {
    double d;
    float f;

    long l;
    int i;
    char c;
    short s;
    byte b;

    d = (float)d;
    d = (long)d;
    d = (int)d;
    d = (char)d;
    d = (short)d;
    d = (byte)d;
    
    f = (long)d;
    f = (int)d;
    f = (char)d;
    f = (short)d;
    f = (byte)d;
    
    l = (int)d;
    l = (char)d;
    l = (short)d;
    l = (byte)d;
    
    i = (char)d;
    i = (short)d;
    i = (byte)d;
  }  
}
