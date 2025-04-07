class ARRAY_ACCESS_EXPR {
  static {
    int[] i_1d;
    double[] d_1d;
    boolean[] B_1d;
    String[] S_1d;
    ARRAY_ACCESS_EXPR[] A_1d;
    
    int[][] i_2d;
    double[][] d_2d;
    boolean[][] B_2d;
    String[][] S_2d;
    ARRAY_ACCESS_EXPR[][] A_2d;

    int[][][] i_3d;
    double[][][] d_3d;
    boolean[][][] B_3d;
    String[][][] S_3d;
    ARRAY_ACCESS_EXPR[][][] A_3d;

    int i;
    double d;
    boolean B;
    String S;
    ARRAY_ACCESS_EXPR A;

    i = i_1d[0];
    i = i_2d[0][0];
    i = i_3d[0][0][0];
    i_1d = i_2d[0];
    i_1d = i_3d[0][0];
    i_2d = i_3d[0];
    
    d = d_1d[0];
    d = d_2d[0][0];
    d = d_3d[0][0][0];
    d_1d = d_2d[0];
    d_1d = d_3d[0][0];
    d_2d = d_3d[0];
    
    B = B_1d[0];
    B = B_2d[0][0];
    B = B_3d[0][0][0];
    B_1d = B_2d[0];
    B_1d = B_3d[0][0];
    B_2d = B_3d[0];

    S = S_1d[0];
    S = S_2d[0][0];
    S = S_3d[0][0][0];
    S_1d = S_2d[0];
    S_1d = S_3d[0][0];
    S_2d = S_3d[0];

    A = A_1d[0];
    A = A_2d[0][0];
    A = A_3d[0][0][0];
    A_1d = A_2d[0];
    A_1d = A_3d[0][0];
    A_2d = A_3d[0];

  }  
}
