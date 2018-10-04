package com.kapouta.aurubik.lib.rubikube.tools;

import com.kapouta.aurubik.lib.rubikube.actions.RubiKubeActionSideRotation;

// In a cube, a piece has an index which is normally the coordinates in a 3 dimensional array, 
// Index is an object that represents this positioning and allows for operations on it

public class Index3d {
	public static final int RX = 0;
	public static final int RY = 1;
	public static final int RZ = 2;

	private int x;
	private int y;
	private int z;

	public Index3d(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void rotate(int side, int direction) {
		
		int[] rot = RubiKubeActionSideRotation.getRotAxis(side, direction);
		// e.g. rot[1,0,0] means rotate over the X axis
		float ang = 90.0f;
		int x2 = x;
		int y2 = y;
		int z2 = z;
		// rotates over the X axis
		// System.out.println("uM ROT = [ " + rot[X] + " , " + rot[Y] + " , " +
		// rot[Z] + " ]");
		if (rot[RX] != 0) {
			if (rot[RX] < 1)
				ang = -ang;
			x2 = (int) (x);
			y2 = Utils.round((y * Utils.cos(ang) - z * Utils.sin(ang)));
			z2 = Utils.round((y * Utils.sin(ang) + z * Utils.cos(ang)));
		} else if (rot[RY] != 0) {
			if (rot[RY] < 1)
				ang = -ang;
			x2 = Utils.round((x * Utils.cos(ang) + z * Utils.sin(ang)));

			y2 = (int) (y);
			z2 = Utils.round((-x * Utils.sin(ang) + z * Utils.cos(ang)));
		} else if (rot[RZ] != 0) {
			if (rot[RZ] < 1)
				ang = -ang;
			// System.out.println("uM Xf = " + ((x * Utils.cos(ang) - y *
			// Utils.sin(ang))));
			// System.out.println("uM Yf = " + ((x * Utils.sin(ang) + y *
			// Utils.cos(ang))));
			x2 = Utils.round(x * Utils.cos(ang) - y * Utils.sin(ang));
			y2 = Utils.round(x * Utils.sin(ang) + y * Utils.cos(ang));
			// System.out.println("uM (x,y,z) = " + x + " , " + y + " , " + z +
			// " , ");
			z2 = (int) (z);
		}
		x = x2;
		y = y2;
		z = z2;
	}
}
