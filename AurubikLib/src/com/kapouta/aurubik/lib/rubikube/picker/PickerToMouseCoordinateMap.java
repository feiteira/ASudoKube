package com.kapouta.aurubik.lib.rubikube.picker;

import android.util.FloatMath;

public class PickerToMouseCoordinateMap {
	private int pside;
	private float px, py;
	private float my;
	private float mx;

	public static PickerToMouseCoordinateMap vectorize(
			PickerToMouseCoordinateMap p1, PickerToMouseCoordinateMap p2) {
		PickerToMouseCoordinateMap v = new PickerToMouseCoordinateMap();

		v.setMouseX(p2.getMouseX() - p1.getMouseX());
		v.setMouseY(p2.getMouseY() - p1.getMouseY());
		v.setPickX(p2.getPickX() - p1.getPickX());
		v.setPickY(p2.getPickY() - p1.getPickY());

		return v;
	}

	public static void inferPickerPointFromVector(
			PickerToMouseCoordinateMap vp1, PickerToMouseCoordinateMap vp2,
			PickerToMouseCoordinateMap mousepoint) {

		float vdist = vp1.mouseDist(vp2);
		float mdist = vp1.mouseDist(mousepoint);
		PickerToMouseCoordinateMap dv = PickerToMouseCoordinateMap.vectorize(
				vp1, vp2);

		// System.out.println("Delta: " + dv);

		float pmx = vp1.getPickX();
		float pmy = vp1.getPickY();

		pmx += (dv.getPickX() * (mdist / vdist));
		pmy += (dv.getPickY() * (mdist / vdist));

		mousepoint.setPick(vp1.getSide(), pmx, pmy);

		//
		// Point2f vx = new Point2f(x,y);
	}

	public PickerToMouseCoordinateMap() {
	}

	public PickerToMouseCoordinateMap(int side, float x, float y, float mx,
			float my) {
		this.pside = side;
		this.px = x;
		this.py = y;
		this.mx = mx;
		this.my = my;
	}

	public PickerToMouseCoordinateMap(float x, float y) {
		this.px = x;
		this.py = y;
	}

	public void setMouse(float mx, float my) {
		this.mx = mx;
		this.my = my;
	}

	public void setPick(int side, float x, float y) {
		this.pside = side;
		this.px = x;
		this.py = y;
	}

	public static PickerToMouseCoordinateMap middle(
			PickerToMouseCoordinateMap p1, PickerToMouseCoordinateMap p2) {
		PickerToMouseCoordinateMap ret = new PickerToMouseCoordinateMap(p1.px,
				p1.py);
		ret.px += (p2.px - p1.px) / 2;
		ret.py += (p2.py - p1.py) / 2;

		return ret;
	}

	public float pickDist(PickerToMouseCoordinateMap p) {
		float dx = px - p.px;
		float dy = py - p.py;
		return FloatMath.sqrt(dx * dx + dy * dy);
	}

	public float mouseDist(PickerToMouseCoordinateMap p) {
		float dx = mx - p.mx;
		float dy = my - p.my;
		return FloatMath.sqrt(dx * dx + dy * dy);
	}

	public boolean equals(PickerToMouseCoordinateMap o) {
		if (px != o.px)
			return false;
		if (py != o.py)
			return false;
		if (pside != o.pside)
			return false;

		return true;
	}

	@Override
	public String toString() {
		String ret = "PICK ( x , y ; side ) => ( " + px + " , " + py + " ; "
				+ pside + " ) MOUS (x,y) => " + mx + " , " + my + "";
		return ret;
	}

	public int getSide() {
		return pside;
	}

	public void setSide(int side) {
		this.pside = side;
	}

	public float getPickX() {
		return px;
	}

	public void setPickX(float x) {
		this.px = x;
	}

	public float getPickY() {
		return py;
	}

	public void setPickY(float y) {
		this.py = y;
	}

	public float getMouseY() {
		return my;
	}

	public void setMouseY(float my) {
		this.my = my;
	}

	public float getMouseX() {
		return mx;
	}

	public void setMouseX(float mx) {
		this.mx = mx;
	}
}
