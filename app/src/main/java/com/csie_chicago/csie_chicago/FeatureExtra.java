package com.csie_chicago.csie_chicago;

import java.util.ArrayList;

public class FeatureExtra {
	private ArrayList<AccelData> acceldatas;
	private double timeWindows;
	private int Firstnum,Endnum,EndFirst;
	private String charSymbol;
	
	public FeatureExtra(ArrayList<AccelData> acceldata,int Firstnum,int Endnum, double timeWindows) {
		super();
		this.acceldatas = acceldata;
		this.timeWindows = timeWindows;
		this.Firstnum=Firstnum;
		this.Endnum=Endnum;
		this.EndFirst=Endnum-Firstnum;
		this.charSymbol=featureExtra_1();
	}
	
	public FeatureExtra(ArrayList<AccelData> acceldata){
		super();
		this.acceldatas = acceldata;
		this.charSymbol=featureExtra_2();
	}
    /**
     * �S�x�^���@�N
     * @return
     */
	private String featureExtra_1() {
		long t = acceldatas.get(Firstnum).getTimestamp();
		double mintvalue = acceldatas.get(Firstnum).getY();
		double maxtvalue;
		double tempmintvalue;
		StringBuilder eigenvalue = new StringBuilder();

		for (int num = 0; num < EndFirst; num++) {

			long t2 = acceldatas.get(Firstnum+num).getTimestamp();
			double tt = t2 - t;
			double ttt = tt / 1000;
			if (ttt > timeWindows) {
				t = acceldatas.get(Firstnum+num - 1).getTimestamp();
				tempmintvalue = acceldatas.get(Firstnum+num - 1).getY();
				maxtvalue = acceldatas.get(Firstnum+num - 1).getY();
				double difference = maxtvalue - mintvalue;
				if (difference > 12) {
					eigenvalue.append("G");
					mintvalue = tempmintvalue;
				} else if (difference > 10) {
					eigenvalue.append("F");
					mintvalue = tempmintvalue;
				} else if (difference > 8) {
					eigenvalue.append("E");
					mintvalue = tempmintvalue;
				} else if (difference > 6) {
					eigenvalue.append("D");
					mintvalue = tempmintvalue;
				} else if (difference > 4) {
					eigenvalue.append("C");
					mintvalue = tempmintvalue;
				} else if (difference > 2) {
					eigenvalue.append("B");
					mintvalue = tempmintvalue;
				} else if (difference > 1) {
					eigenvalue.append("A");
					mintvalue = tempmintvalue;
				} else if (difference < -1 & difference > -2) {
					eigenvalue.append("K");
					mintvalue = tempmintvalue;
				} else if (difference < -2 & difference > -4) {
					eigenvalue.append("L");
					mintvalue = tempmintvalue;
				} else if (difference < -4 & difference > -6) {
					eigenvalue.append("M");
					mintvalue = tempmintvalue;
				} else if (difference < -6 & difference > -8) {
					eigenvalue.append("N");
					mintvalue = tempmintvalue;
				} else if (difference < -8 & difference > -10) {
					eigenvalue.append("O");
					mintvalue = tempmintvalue;
				} else if (difference < -10 & difference > -12) {
					eigenvalue.append("P");
					mintvalue = tempmintvalue;
				} else if (difference < -12) {
					eigenvalue.append("Q");
					mintvalue = tempmintvalue;
				}
				
			}
		}
		return eigenvalue.toString();
	}

	/**
	 * �S�x�^���G�N
	 * @return
	 */
	private String featureExtra_2(){
		StringBuilder eigenvalue = new StringBuilder();

		for (int i = 1; i < acceldatas.size(); i++) {

			double temp_new_Y = acceldatas.get(i).getY();
			double temp_past_Y = acceldatas.get(i - 1).getY();
			double difference = temp_new_Y - temp_past_Y;

			if(difference > 84){
				eigenvalue.append("H");
			}else if (difference > 72) {
				eigenvalue.append("G");
			} else if (difference > 60) {
				eigenvalue.append("F");
			} else if (difference > 48) {
				eigenvalue.append("E");
			} else if (difference > 36) {
				eigenvalue.append("D");
			} else if (difference > 24) {
				eigenvalue.append("C");
			} else if (difference > 12) {
				eigenvalue.append("B");
			} else if (difference >=  3) {
				eigenvalue.append("A");
			} else if (difference <= -3 & difference > -12) {
				eigenvalue.append("L");
			} else if (difference <= -12 & difference > -24) {
				eigenvalue.append("M");
			} else if (difference <= -24 & difference > -36) {
				eigenvalue.append("N");
			} else if (difference <= -36 & difference > -48) {
				eigenvalue.append("O");
			} else if (difference <= -48 & difference > -60) {
				eigenvalue.append("P");
			} else if (difference <= -60 & difference > -72) {
				eigenvalue.append("Q");
			} else if (difference <= -72 & difference > -84) {
				eigenvalue.append("R");
			}else if(difference <= -84) {
			   eigenvalue.append("S");
			}else{
				eigenvalue.append("X");;
		  	}		
	}
		return eigenvalue.toString();
	}
	public ArrayList<AccelData> getSensorData() {
		return acceldatas;
	}

	public void setSensorData(ArrayList<AccelData> acceldata) {
		this.acceldatas = acceldata;
	}

	public double getTimeWindows() {
		return timeWindows;
	}

	public void setTimeWindows(double timeWindows) {
		this.timeWindows = timeWindows;
	}

	public String getCharSymbol() {
		return charSymbol;
	}
	
}
