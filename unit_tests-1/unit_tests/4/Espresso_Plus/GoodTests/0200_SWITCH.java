class SWITCH {
    static {
        int i;
        String S = "";

        switch (i) {
        case 0:
            i = 1;
            break;
        default:
            i = 0;
            break;
        }

        switch (S) {
        case "value":
            S = "s";
            break;
        default:
            S = "";
            break;
        }
    }
}
