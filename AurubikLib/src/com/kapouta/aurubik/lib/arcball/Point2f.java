package com.kapouta.aurubik.lib.arcball;

/**
 * Created by IntelliJ IDEA. User: pepijn Date: Aug 7, 2005 Time: 5:46:24 PM To
 * change this template use File | Settings | File Templates.
 */
public class Point2f {
	public float x, y;

	public Point2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float atan2() {
		return (float) Math.atan2(y, x);
	}

	public float angleWith(Point2f p2) {
		float x1 = p2.x;
		float y1 = p2.y;
		float op = x1 * y - x * y1;
		float ip = x * x1 + y * y1;
		
		return (float) Math.toDegrees(Math.atan2(op, ip));
	}
}
