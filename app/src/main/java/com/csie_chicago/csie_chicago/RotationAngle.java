package com.csie_chicago.csie_chicago;

import java.util.ArrayList;

import android.view.ViewDebug.FlagToString;

public class RotationAngle {
	private ArrayList<AccelData> RawAcceldatas;
	private final double[] reference_gravity = { 0d, 0d, -9.8d };
	private final double gravity = 9.8;
	private double[] rotationAngle;
	private double[] rotationAnglePitch;
	private double[] rotationAngleRoll;
	private int decreaseCount = 0;
	private double angleVariation;
	private double pitchAngleVariation;
	private double rollAngleVariation;
	
	public RotationAngle(ArrayList<AccelData> acceldata) {
		this.RawAcceldatas = acceldata;
		rotationAngle = new double[RawAcceldatas.size()];
		rotationAnglePitch = new double[RawAcceldatas.size()];
		rotationAngleRoll =new double[RawAcceldatas.size()];
		//��k�T
		storageRotationAngle();
		angleVariation = calculateAngleVariation(isIncrease(rotationAngle,rotationAngle.length));
		//��k�G
		storagePitchRollRotationAngle();
		pitchAngleVariation = calculatePitchAngleVariation(isIncrease(rotationAnglePitch,rotationAnglePitch .length));
		rollAngleVariation = calculateRollAngleVariation(isIncrease(rotationAngleRoll,rotationAngleRoll.length));

	}

	private void storageRotationAngle() {
		for (int i = 0; i < RawAcceldatas.size(); i++) {
			rotationAngle[i] = calculateRotationAngle(RawAcceldatas.get(i)
					.getX(), RawAcceldatas.get(i).getY(), RawAcceldatas.get(i)
					.getZ());
		}
	}

	private void storagePitchRollRotationAngle() {
		for (int i = 0; i < RawAcceldatas.size(); i++) {
			rotationAnglePitch[i] = calculatePitchRotationAngle(RawAcceldatas.get(i)
					.getX(), RawAcceldatas.get(i).getY(), RawAcceldatas.get(i)
					.getZ());
			rotationAngleRoll[i] = calculateRollRotationAngle(RawAcceldatas.get(i)
					.getX(), RawAcceldatas.get(i).getY(), RawAcceldatas.get(i)
					.getZ());
		}
	}
	
	private double calculateRotationAngle(double x, double y, double z) {

		double[] current_gravity = { x, y, z };
		double Molecular = current_gravity[2] * reference_gravity[2];
		double current_gravity_sqrt = Math.sqrt(current_gravity[0]
				* current_gravity[0] + current_gravity[1] * current_gravity[1]
				+ current_gravity[2] * current_gravity[2]);
		double reference_gravity_sqrt = Math.sqrt(gravity * gravity);
		double Denominator = current_gravity_sqrt * reference_gravity_sqrt;
		double Angle = Math.acos(Molecular / Denominator);
		double rotationAngle = Math.abs(((Math.ceil(Angle * (180d / Math.PI)
				* 100d) / 100d) - 90));
		return rotationAngle;

	}

	private double calculatePitchRotationAngle(double x, double y, double z) {
	
		double pitch = Math.asin(z/gravity);
		double rotationAnglePitch = Math.abs((Math.round(pitch*(180d/Math.PI)*100d)/100d));
		
		return rotationAnglePitch;
	}
	private double calculateRollRotationAngle(double x, double y, double z) {
		
		double roll = Math.asin(x/gravity);
		double rotationAnglerRoll  =  Math.abs((Math.round(roll*(180d/Math.PI)*100d)/100d));
		
		return rotationAnglerRoll;
	}
	private boolean isIncrease(double[] value, int n) {
		if (n == 1) {
			if (decreaseCount > 10) {
				return false;
			} else {
				return true;
			}
		} else {
			if (value[n - 2] - value[n - 1] >= 3.0d)
				decreaseCount = decreaseCount + 1;
			return isIncrease(value, n - 1);
		}
	}

	private double calculateAngleVariation(boolean isIncrease) {
		double startAngle, endAngle;
		if (isIncrease) {
			startAngle = findMinValue(rotationAngle);
			endAngle = findMaxValue(rotationAngle);
			return Math.abs(startAngle - endAngle);
		} else {
			return 0.0d;
		}
	}

	private double calculatePitchAngleVariation(boolean isIncrease) {
		double startAngle, endAngle;
		if (isIncrease) {
			startAngle = findMinValue(rotationAnglePitch);
			endAngle = findMaxValue(rotationAnglePitch);
			return Math.abs(startAngle - endAngle);
		} else {
			return 0.0d;
		}
	}
	
	private double calculateRollAngleVariation(boolean isIncrease) {
		double startAngle, endAngle;
		if (isIncrease) {
			startAngle = findMinValue(rotationAngleRoll);
			endAngle = findMaxValue(rotationAngleRoll);
			return Math.abs(startAngle - endAngle);
		} else {
			return 0.0d;
		}
	}
	
	private double findMinValue(double[] Value) {
		double min = Value[0];
		for (int i = 1; i < Value.length; i++) {
			if (Value[i] < min) {
				min = Value[i];
			}
		}
		return min;
	}
	private double findMaxValue(double[] Value) {
		double max = Value[0];
		for (int i = 1; i < Value.length; i++) {
			if (Value[i] > max) {
				max = Value[i];
			}
		}
		return max;
	}

	public double[] getRotationAngle() {
		return rotationAngle;
	}

	public double getAngleVariation() {
		return angleVariation;
	}

	public double[] getRotationAnglePitch() {
		return rotationAnglePitch;
	}

	public double[] getRotationAngleRoll() {
		return rotationAngleRoll;
	}

	public double getPitchAngleVariation() {
		return pitchAngleVariation;
	}

	public double getRollAngleVariation() {
		return rollAngleVariation;
	}

	
}
