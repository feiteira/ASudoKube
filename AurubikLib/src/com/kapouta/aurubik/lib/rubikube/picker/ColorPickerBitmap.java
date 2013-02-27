package com.kapouta.aurubik.lib.rubikube.picker;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.kapouta.aurubik.lib.arcball.Point2f;
import com.kapouta.aurubik.lib.rubikube.tools.OpenGLBitMaps;

public class ColorPickerBitmap extends OpenGLBitMaps {
	private static final int DEFAULT_RESOLUTION = 256;

	private int id;
	@SuppressWarnings("unused")
	private int resolution = DEFAULT_RESOLUTION;
	int textureLoadedID = -1;

	public ColorPickerBitmap() {
		this(-1, DEFAULT_RESOLUTION);
	}

	public ColorPickerBitmap(int id) {
		this(id, DEFAULT_RESOLUTION);
	}

	public ColorPickerBitmap(int id, int resolution) {
		this.id = id;
		this.resolution = resolution;
		bitmap = Bitmap.createBitmap(resolution, resolution, Config.ARGB_8888);

		int ri, gi, bi, color;

		for (int cntx = 0; cntx < resolution; cntx++) {
			for (int cnty = 0; cnty < resolution; cnty++) {

				ri = (int) (255 * cntx * 1.0 / (resolution - 1));
				gi = (int) (255 * cnty * 1.0 / (resolution - 1));
				bi = (id);
				color = 0xff000000 | (ri << 16) | (gi << 8) | bi;

				bitmap.setPixel(cntx, cnty, color);
			}
		}
		bitmap.prepareToDraw();

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Point2f getFloatCoords(int ix, int iy) {
		Point2f p = new Point2f(ix / 255.0f, iy / 255.0f);
		return p;
	}

}
