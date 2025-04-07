class SWITCH_DUPLICATE_CASE_LABEL {
  static {
    int i;
    switch (i) {
      case 0:
        break;
      default:
        break;
      default:
        break;
    }
  }
}
