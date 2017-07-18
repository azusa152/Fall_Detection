package com.csie_chicago.csie_chicago;

import java.util.ArrayList;

public class Normalization {
	private ArrayList<AccelData> acceldatas;
	private double[] maxValue ;

	public Normalization(ArrayList<AccelData> acceldata) {
		this.acceldatas = acceldata;
		maxValue = findMaxValue(this.acceldatas);
		NormalizationProcess();
	}

	/**
	 *
	 */
	private double[] findMaxValue(ArrayList<AccelData> acceldata) {
		double[] maxValueTemp = new double[3];
		maxValueTemp[0] =acceldata.get(0).getX();
		maxValueTemp[1] = acceldata.get(0).getY();
		maxValueTemp[2] = acceldata.get(0).getZ();

		for (int num = 1; num < acceldata.size(); num++) {
			double tempX = Math.abs(acceldata.get(num).getX());
			double tempY = Math.abs(acceldata.get(num).getY());
			double tempZ = Math.abs(acceldata.get(num).getZ());

			if (tempX > maxValueTemp[0]) {
				maxValueTemp[0] = tempX;
			}
			if (tempY > maxValueTemp[1]) {
				maxValueTemp[1] = tempY;
			}
			if (tempZ > maxValueTemp[2]) {
				maxValueTemp[2] = tempZ;
			}
		}

		return maxValueTemp;
	}

	/**

	 */
	private void NormalizationProcess() {
		for (int num = 0; num < this.acceldatas.size(); num++) {
			double tempX = this.acceldatas.get(num).getX() / maxValue[0];
			double tempY = this.acceldatas.get(num).getY() / maxValue[1];
			double tempZ = this.acceldatas.get(num).getZ() / maxValue[2];
			tempX = Math.round(tempX * 100d);
			tempY = Math.round(tempY * 100d);
			tempZ = Math.round(tempZ * 100d);
			this.acceldatas.get(num).setX(tempX);
			this.acceldatas.get(num).setY(tempY);
			this.acceldatas.get(num).setZ(tempZ);
		}
	}

	public ArrayList<AccelData> getAcceldatas() {
		return acceldatas;
	}
	
}
