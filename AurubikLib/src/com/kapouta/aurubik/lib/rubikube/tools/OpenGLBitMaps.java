package com.kapouta.aurubik.lib.rubikube.tools;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public abstract class OpenGLBitMaps {
	protected Bitmap bitmap = null;

	int textureLoadedID = -1;
	private int[] textures = new int[1];

	protected float texture[] = {
			// Mapping coordinates for the vertices
			0.0f, 1.0f, // top left (V2)
			0.0f, 0.0f, // bottom left (V1)
			1.0f, 1.0f, // top right (V4)
			1.0f, 0.0f // bottom right (V3)
	};
	protected GL10 mygl;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getTextureLoadedID() {
		return textureLoadedID;
	}

	public int loadGLTexture(GL10 gl) {

		if (mygl != null && mygl.equals(gl))
			return textureLoadedID;
		else {
			mygl = gl;
		}
		

		gl.glEnable(GL10.GL_TEXTURE_2D);

		// Generate one texture pointer...
		gl.glGenTextures(1, textures, 0);
		int mTextureId = textures[0];
		textureLoadedID = mTextureId;

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		// gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
		// /*GL10.GL_REPLACE*/ GL10.GL_MODULATE);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		// gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
		// GL10.GL_REPEAT);
		// gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
		// GL10.GL_REPEAT);

		// Use Android GLUtils to specify a two-dimensional texture image from
		// our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// Clean up
	//	bitmap.recycle();
		gl.glDisable(GL10.GL_TEXTURE_2D);

		return mTextureId;
	}

	public void setTexture(GL10 gl) {
		// Set the face rotation
		// gl.glFrontFace(GL10.GL_CW);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureLoadedID);

		// gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
	}

}
