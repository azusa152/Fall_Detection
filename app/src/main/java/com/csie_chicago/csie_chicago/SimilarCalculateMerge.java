package com.csie_chicago.csie_chicago;

public class SimilarCalculateMerge {
	private String testData,trainData;
	private int testlDeviSum;
	private int testDifNum;
	private double testDataScore;
	private int trainDataNum,trainDeviSum,trainDifNum;
	private double trainDataScore;
	
	private ApproxLD approxLD;
	private int deviSumDif;
	private double similarMergeValue;
	
	private double alpha=0.5;
	private double ApproxLCSScore ;
	private double ApproxLDScore;
	
	public SimilarCalculateMerge(String testData, String trainData,int trainDataNum,int trainDeviSum, int trainDifNum, double trainDataScore) {
		super();
		this.testData = testData;
		this.trainData=trainData;
		this.trainDataNum = trainDataNum;
		this.trainDeviSum = trainDeviSum;
		this.trainDifNum = trainDifNum;
		this.trainDataScore = trainDataScore;
		approxLD = new ApproxLD(this.trainData,this.testData);
		// ���`��Ƥ���
		calculateDifNum();
		calculateDeviSum();
		calculateDataScore();
		// �p��}�l
		calculateDeviSumDif();
		caculateMergeSimilarity();
	}

	private String removeChr(String str, char x) {
		StringBuilder strBuilder = new StringBuilder();                                            
		char[] rmString = str.toCharArray();
		for (int i = 0; i < rmString.length; i++) {
			if (rmString[i] == x) {

			} else {
				strBuilder.append(rmString[i]);
			}
		}
		return strBuilder.toString();
	}

	private void caculateMergeSimilarity() {
		ApproxLCSScore =  ((double)(testDataScore * testDifNum) /(double)(trainDataScore * trainDifNum))*100;
		
		ApproxLDScore = (1.0d - (double)approxLD.getApproxCost() /(double) Math.max(testData.length(), trainData.length()))*100;

		similarMergeValue = (1-alpha)*ApproxLDScore +alpha *ApproxLCSScore;
	}

	private void calculateDeviSumDif() {
		deviSumDif = Math.abs(testlDeviSum - trainDeviSum);
	}

	private void calculateDeviSum() {
		int temp;
		for (int i = 0; i < testData.length() - 1; i++) {
			temp = (int) testData.charAt(i + 1) - (int) testData.charAt(i);
			testlDeviSum = testlDeviSum + Math.abs(temp);
		}
	}

	private void calculateDataScore() {
		int temp;
		for (int i = 0; i < testData.length(); i++) {
			temp = (int) testData.charAt(i);
			testDataScore = testDataScore + Math.abs(temp);
		}
	}

	private void calculateDifNum() {
		boolean A_Lock = true;
		boolean B_Lock = true;
		boolean C_Lock = true;
		boolean D_Lock = true;
		boolean E_Lock = true;
		boolean F_Lock = true;
		boolean G_Lock = true;
		boolean H_Lock = true;

		boolean L_Lock = true;
		boolean M_Lock = true;
		boolean N_Lock = true;
		boolean O_Lock = true;
		boolean P_Lock = true;
		boolean Q_Lock = true;
		boolean R_Lock = true;
		boolean S_Lock = true;
		for (int i = 0; i < testData.length(); i++) {
			int ASCII = (int) testData.charAt(i);
			switch (ASCII) {
			case 65:
				if (A_Lock == true) {
					testDifNum = testDifNum + 1;
					A_Lock = false;
					break;
				} else
					break;

			case 66:
				if (B_Lock == true) {
					testDifNum = testDifNum + 1;
					B_Lock = false;
					break;
				} else
					break;
			case 67:
				if (C_Lock == true) {
					testDifNum = testDifNum + 1;
					C_Lock = false;
					break;
				} else
					break;
			case 68:
				if (D_Lock == true) {
					testDifNum = testDifNum + 1;
					D_Lock = false;
					break;
				} else
					break;
			case 69:
				if (E_Lock == true) {
					testDifNum = testDifNum + 1;
					E_Lock = false;
					break;
				} else
					break;
			case 70:
				if (F_Lock == true) {
					testDifNum = testDifNum + 1;
					F_Lock = false;
					break;
				} else
					break;
			case 71:
				if (G_Lock == true) {
					testDifNum = testDifNum + 1;
					G_Lock = false;
					break;
				} else
					break;
			case 72:
				if (H_Lock == true) {
					testDifNum = testDifNum + 1;
					H_Lock = false;
					break;
				} else
					break;

			case 76:
				if (L_Lock == true) {
					testDifNum = testDifNum + 1;
					L_Lock = false;
					break;
				} else
					break;
			case 77:
				if (M_Lock == true) {
					testDifNum = testDifNum + 1;
					M_Lock = false;
					break;
				} else
					break;
			case 78:
				if (N_Lock == true) {
					testDifNum = testDifNum + 1;
					N_Lock = false;
					break;
				} else
					break;
			case 79:
				if (O_Lock == true) {
					testDifNum = testDifNum + 1;
					O_Lock = false;
					break;
				} else
					break;
			case 80:
				if (P_Lock == true) {
					testDifNum = testDifNum + 1;
					P_Lock = false;
					break;
				} else
					break;
			case 81:
				if (Q_Lock == true) {
					testDifNum = testDifNum + 1;
					Q_Lock = false;
					break;
				} else
					break;
			case 82:
				if (R_Lock == true) {
					testDifNum = testDifNum + 1;
					R_Lock = false;
					break;
				} else
					break;
			case 83:
				if (S_Lock == true) {
					testDifNum = testDifNum + 1;
					S_Lock = false;
					break;
				} else
					break;
			default:
				break;
			}
		}
	}

	public int getDeviSumDif() {
		return deviSumDif;
	}

	public double getSimilarMergeValue() {
		return similarMergeValue;
	}

	public String getTestData() {
		return testData;
	}

	public double getApproxLCSScore() {
		return ApproxLCSScore;
	}

	public double getApproxLDScore() {
		return ApproxLDScore;
	}
	
	
}
