package com.kapouta.aurubik.lib.rubikube.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class GLRectangle {
	private float[] coords;
	private FloatBuffer vertexBuffer;

	public GLRectangle(float x1, float y1, float x2, float y2) {
		coords = new float[] { x1, y1, x2, y1, x2, y2, x1, y2 };
		ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer(); // Convert
											// from
											// byte to
											// float
		vertexBuffer.put(coords); // Copy data into buffer
		vertexBuffer.position(0); // Rewind
	}

	public void draw(GL10 gl) {
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, coords.length / 2);

	}
}
