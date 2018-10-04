package com.kapouta.aurubik.lib.rubikube.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class SquaredBitmap extends OpenGLBitMaps {
	public static final int DEFAULT_BORDER_COLOR = 0xff666666;
	public static final int SIZE_X = 64;
	public static final int SIZE_Y = 64;

	public static final float BORDER_THICKNESS = 6; //

	protected int bordercolor;
	protected float borderthickness;
	protected int ri, gi, bi;

	public SquaredBitmap() {
		this(1, 1, 0);
	}

	public SquaredBitmap(float r, float g, float b) {
	
		this.bordercolor = DEFAULT_BORDER_COLOR;

		this.ri = (int) (r * 255);
		this.gi = (int) (g * 255);
		this.bi = (int) (b * 255);
		int color = 0xff000000 | (ri << 16) | (gi << 8) | bi;

		bitmap = Bitmap.createBitmap(SIZE_X, SIZE_Y, Config.ARGB_8888);

		for (int cntx = 0; cntx < SIZE_X; cntx++) {
			for (int cnty = 0; cnty < SIZE_Y; cnty++) {
				if (cntx < BORDER_THICKNESS || cnty < BORDER_THICKNESS
						|| cntx > (SIZE_X - BORDER_THICKNESS)
						|| cnty > (SIZE_Y - BORDER_THICKNESS)) {
					bitmap.setPixel(cntx, cnty, bordercolor);
				} else {
					bitmap.setPixel(cntx, cnty, color);
				}
			}
		}
		bitmap.prepareToDraw();

	}

	@Override
	public String toString() {
		return this.getClass() + " ARGB -> ( " + ri + " , " + gi + " , " + bi
				+ " ) " + bitmap.toString();
	}
}
