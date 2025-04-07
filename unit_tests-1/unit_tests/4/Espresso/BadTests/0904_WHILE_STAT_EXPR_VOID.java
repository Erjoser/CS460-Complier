class WHILE_STAT_EXPR_VOID {
    static void foo() {}

    static {
        while (foo()) {
        }
    }
}
