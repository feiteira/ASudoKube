package com.kapouta.aurubik.lib.rubikube.tools;

public class Utils {

	public static double cos(float angDegrees) {
		return Math.cos(Math.toRadians(angDegrees));
	}

	public static double sin(float angDegrees) {
		return Math.sin(Math.toRadians(angDegrees));
	}

	public static double dist(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

	public static double dist(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static int round(double d){
				return (int) Math.round(d);
	}
}
