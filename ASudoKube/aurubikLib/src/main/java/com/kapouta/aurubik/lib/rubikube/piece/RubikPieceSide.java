package com.kapouta.aurubik.lib.rubikube.piece;

import javax.microedition.khronos.opengles.GL10;

import com.kapouta.aurubik.lib.rubikube.RubiKubeRenderer;
import com.kapouta.aurubik.lib.rubikube.tools.NumberSquaredBitmap;
import com.kapouta.aurubik.lib.rubikube.tools.SquaredBitmap;

public class RubikPieceSide {
	
	private int color = 0;
	private int face;
	private SquaredBitmap bitmap = null;

	private int resetFace;
	private int value;

	public RubikPieceSide(int face) {
		this.color = face;
		this.face = face;
		this.resetFace = this.face;

		float[] c_arr = RubiKubeRenderer.COLORS[color];

		bitmap = new SquaredBitmap(c_arr[0], c_arr[1], c_arr[2]);
		// bitmap = new NumberSquaredBitmap();
	}

	public RubikPieceSide(int face, int value) {
		this.color = face;
		this.face = face;
		this.resetFace = this.face;
		this.value = value;

		float[] c_arr = RubiKubeRenderer.COLORS[color];

		bitmap = new NumberSquaredBitmap(value);
	}

	public void setValue(int value) {
		this.value = value;
		float[] c_arr = RubiKubeRenderer.COLORS[color];

		bitmap = new NumberSquaredBitmap(value);
	}

	public int getValue() {
		return value;
	}

	public int getColor() {
		return color;
	}

	public void setColor(float r, float g, float b) {
		bitmap = new SquaredBitmap(r, g, b);
	}

	public int getFace() {
		return face;
	}

	public void setFace(int face) {
		this.face = face;
	}

	public void load(GL10 gl) {
		bitmap.loadGLTexture(gl);
	}

	public void setTexture(GL10 gl) {
		bitmap.setTexture(gl);
	}

	@Override
	public String toString() {
		return v("Face", face) + v("Color", color) + bitmap;
	}

	private String v(String s, int n) {
		return s + "( " + n + ") ";
	}

	public void reset() {
		color = face = resetFace;
	}
}