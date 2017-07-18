package com.csie_chicago.csie_chicago;

public class ApproxLD {
	private int i , j;
	private int n, m;
	private int Tolerate =2;
	private double cost = 0.0d;
	private double[][] LD;
	private double approxCost;
	private String X, Y;
	 
	public ApproxLD(String str1, String str2){
		this.X = str1;
		this.Y = str2;
		this.n = str1.length();
	    this.m = str2.length();
		this.LD = new double[n + 1][m + 1];
		approxCost=approxCostLevenshtein();
		
		int x=5;
	}
	
	private double approxCostLevenshtein() {
        
        for (i = 0; i <= n; i++) {
            LD[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            LD[0][j] = j;
        }

        for (i = 1; i <= n; i++) {
            for (j = 1; j <= m; j++) {

                int symbolDistance = Math.abs((int) X.charAt(i - 1) - (int) Y.charAt(j - 1));              
                if (symbolDistance <= Tolerate) {
                    switch (symbolDistance) {
                        case 0:
                            cost=0;
                            break;
                        case 1:
                          cost=0.05;
                         
                            break;
                        case 2:
                             cost=0.1;
                         
                            break;         
                        default:   
                            break;
                    }
                } else {
                    cost = 1.0d;
                }


                LD[i][j] = findMinDoubleValue(LD[i - 1][j] + 1, LD[i][j - 1] + 1, LD[i - 1][j - 1] + cost);
            }
        }

        return LD[n][m];
    }
	
	  private  double findMinDoubleValue(double a, double b, double c) {
	        double min = a;
	        if (b < a) {
	            min = b;
	        } else if (c < min) {
	            min = c;
	        }
	        return min;
	    }

	public double getApproxCost() {
		return approxCost;
	}
	  
}
