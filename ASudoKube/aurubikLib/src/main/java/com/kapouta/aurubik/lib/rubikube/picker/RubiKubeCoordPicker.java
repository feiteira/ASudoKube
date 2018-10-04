package com.kapouta.aurubik.lib.rubikube.picker;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.kapouta.aurubik.lib.arcball.Point2f;
import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.RubiKubeRenderer;
import com.kapouta.aurubik.lib.rubikube.piece.RubikPiece;

public class RubiKubeCoordPicker {
	private ColorPickerBitmap[] textures;

	private FloatBuffer textureBuffer;
	private FloatBuffer vertexBuffer;

	/** The initial texture coordinates (u, v) */
	private float texture[] = {
			// Mapping coordinates for the vertices
			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

	};

	public RubiKubeCoordPicker() {

		textures = new ColorPickerBitmap[RubiKubeModel.NSIDES];

		for (int cnt = 0; cnt < RubiKubeModel.NSIDES; cnt++) {
			textures[cnt] = new ColorPickerBitmap(cnt);
		}

		ByteBuffer vbb = ByteBuffer
				.allocateDirect(RubikPiece.CUBE_VERTICES.length * 4);
		vbb.order(ByteOrder.nativeOrder()); // Use native byte order
		vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
		vertexBuffer.put(RubikPiece.CUBE_VERTICES); // Copy data into buffer
		vertexBuffer.position(0); // Rewind

		ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);

	}

	public void load(GL10 gl) {

		for (int cnt = 0; cnt < RubiKubeModel.NSIDES; cnt++) {
			textures[cnt].loadGLTexture(gl);
		}
	}

	public PickerToMouseCoordinateMap getPickerCoordinateFromPixel(
			ByteBuffer pixel) {

		int x = pixel.get(0) & 0xFF;
		int y = pixel.get(1) & 0xFF;
		int id = pixel.get(2) & 0xFF;
		PickerToMouseCoordinateMap pcord = new PickerToMouseCoordinateMap();

		if (id < textures.length) {
			ColorPickerBitmap tex = textures[id];
			Point2f pf = tex.getFloatCoords(x, y);
			pcord.setPick(id, pf.x, pf.y);
		} else {
			pcord.setPick(RubiKubeRenderer.CLICK_OUTSIDE_CUBE, 0, 0);
		}

		return pcord;

	}

	public void draw(GL10 gl) {
		// orientation

		gl.glEnable(GL10.GL_TEXTURE_2D);

		// gl.glFrontFace(GL10.GL_CCW); // Front face in counter-clockwise

		gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
		gl.glCullFace(GL10.GL_BACK); // Cull the back face (don't display)

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		gl.glPushMatrix();
		// Render all the faces
		gl.glScalef(3f, 3f, 3f); // 3 because its 3x3 cube
		for (int face = 0; face < RubiKubeModel.NSIDES; face++) {
			textures[face].setTexture(gl);

			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4 * face, 4);

		}
		gl.glPopMatrix();

		// textureBuffer.rewind();
		// vertexBuffer.rewind();
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

}
