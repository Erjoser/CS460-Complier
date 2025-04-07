interface A { int a(); }
interface B { int b(); }
interface C extends A, B { int c(); }

class NAME_EXPR_GET_FIELD_INTERFACE_NESTED implements C {
    static {
        c();
        b();
        a();
    }
}