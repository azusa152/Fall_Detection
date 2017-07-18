package com.csie_chicago.csie_chicago;

import android.util.Log;

public class ApproxLCS {
	int i, j;
	int n, m;
	String X, Y;
	int[][] C, B;
	int Tolerate =2;
    int MaxdeT = 1;
    private String lcs = new String();
    int[] location ; 
    int[] location2 ; 
    private int PositionSum=0;
    
	public ApproxLCS(String x, String y) {
		this.X = x;
		this.Y = y;
		this.n = x.length();
		this.m = y.length();
		this.C = new int[n + 1][m + 1];
		this.B = new int[n + 1][m + 1];
		location   = new int[m+1]; 
		Initialization();
	}

	private void Initialization() {

		/* C[i][0] = 0 for 0<=i<=n */
		for (i = 0; i <= n; i++) {
			C[i][0] = 0;
		}

		/* C[0][j] = 0 for 0<=j<=m */
		for (j = 0; j <= m; j++) {
			C[0][j] = 0;
		}
		
		 /* dynamic programming */
        for (i = 1; i <= n; i++) {
            for (j = 1; j <= m; j++) {
                if (Math.abs((int)X.charAt(i - 1) - (int)Y.charAt(j - 1)) <= Tolerate) {
                    C[i][j] = C[i - 1][j - 1] + 1;
                    B[i][j] = 1;  /* diagonal */
                } else if (C[i - 1][j] >= C[i][j - 1]) {
                    C[i][j] = C[i - 1][j];
                    B[i][j] = 2;  /*  down*/
                } else {
                    C[i][j] = C[i][j - 1];
                    B[i][j] = 3;   /*  forword*/
                }
            }
        }
        
        /* Backtracking */
       // String lcs = new String();
        i = n;
        j = m;
        while (i != 0 && j != 0) {
            if (B[i][j] == 1) {   /* diagonal */
                int x = (int) X.charAt(i - 1);
                int y = (int) Y.charAt(j - 1);
                if (x == y) {
                	location[j]=j;
                    lcs = X.charAt(i - 1) + lcs;
                    i = i - 1;
                    j = j - 1;
                } else if (x > y) {
                	location[j]=j;
                	 int DifdeT=x-y;/*x-y�j�h��*/
                     if(DifdeT >= MaxdeT){
                         DifdeT=MaxdeT;
                     }else{
                         DifdeT=DifdeT;
                     }
                    lcs = (char) (x - MaxdeT) + lcs;
                    i = i - 1;
                    j = j - 1;
                } else {
                	location[j]=j;
                	 int DifdeT=y-x;/*y-x�j�h��*/
                     if(DifdeT >= MaxdeT){
                         DifdeT=MaxdeT;
                     }else{
                         DifdeT=DifdeT;
                     }
                    lcs = (char) (y - MaxdeT) + lcs;
                    i = i - 1;
                    j = j - 1;
                }
            }
            if (B[i][j] == 2) {  /* up */
                i = i - 1;
            }
            if (B[i][j] == 3) {  /* backword */
                j = j - 1;
            }
        }
        
        /*LocationProcess*/
		location2 = new int[C[n][m]]; 
        int y =0;
        for(int k = 0 ; k < location.length ; k++){
            if(location[k]==0){
                //Log.d("location:",String.valueOf(k));
            }else{
                location2[y]=location[k];
                
                Log.d("location:",String.valueOf(location[k]));
                y++;
                //Log.d("location2:",String.valueOf(Y));
            } 
        }
        
        Log.d("location2長度:",String.valueOf(y));
        for(int w =1 ;w < location2.length;w++){
        	PositionSum = PositionSum + (location2[w]-location2[w-1]);
        }     
        Log.d("location2PositionSum:",String.valueOf(PositionSum));

	}

	public int getPositionSum(){
		return PositionSum;
	}
	
	public String getLcs() {
		return lcs;
	}

}
