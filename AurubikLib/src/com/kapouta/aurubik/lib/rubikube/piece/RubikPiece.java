package com.kapouta.aurubik.lib.rubikube.piece;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.RubiKubeRenderer;
import com.kapouta.aurubik.lib.rubikube.actions.RubiKubeActionSideRotation;
import com.kapouta.aurubik.lib.rubikube.tools.Index3d;
import com.kapouta.aurubik.lib.rubikube.tools.SquaredBitmap;

public class RubikPiece {
	private static final int NUM_SIDES = 6;

	public static final float[] CUBE_VERTICES = { // Vertices of the 6 faces

			// FRONT
			-1.0f, -1.0f, 1.0f, // 0. left-bottom-front
			1.0f, -1.0f, 1.0f, // 1. right-bottom-front
			-1.0f, 1.0f, 1.0f, // 2. left-top-front
			1.0f, 1.0f, 1.0f, // 3. right-top-front

			// BACK
			1.0f, -1.0f, -1.0f, // 6. right-bottom-back
			-1.0f, -1.0f, -1.0f, // 4. left-bottom-back
			1.0f, 1.0f, -1.0f, // 7. right-top-back
			-1.0f, 1.0f, -1.0f, // 5. left-top-back

			// TOP
			-1.0f, 1.0f, 1.0f, // 2. left-top-front
			1.0f, 1.0f, 1.0f, // 3. right-top-front
			-1.0f, 1.0f, -1.0f, // 5. left-top-back
			1.0f, 1.0f, -1.0f, // 7. right-top-back
			// BOTTOM
			-1.0f, -1.0f, -1.0f, // 4. left-bottom-back
			1.0f, -1.0f, -1.0f, // 6. right-bottom-back
			-1.0f, -1.0f, 1.0f, // 0. left-bottom-front
			1.0f, -1.0f, 1.0f, // 1. right-bottom-front
			// LEFT
			-1.0f, -1.0f, -1.0f, // 4. left-bottom-back
			-1.0f, -1.0f, 1.0f, // 0. left-bottom-front
			-1.0f, 1.0f, -1.0f, // 5. left-top-back
			-1.0f, 1.0f, 1.0f, // 2. left-top-front
			// RIGHT
			1.0f, -1.0f, 1.0f, // 1. right-bottom-front
			1.0f, -1.0f, -1.0f, // 6. right-bottom-back
			1.0f, 1.0f, 1.0f, // 3. right-top-front
			1.0f, 1.0f, -1.0f // 7. right-top-back

	};

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

	private Index3d index;

	private RubikPieceSide[] sides;

	private SquaredBitmap inside = null;

	private FloatBuffer textureBuffer;
	private FloatBuffer vertexBuffer;

	public RubikPiece() {
		// Setup vertex-array buffer. Vertices in float. An float has 4 bytes
		ByteBuffer vbb = ByteBuffer.allocateDirect(CUBE_VERTICES.length * 4);
		vbb.order(ByteOrder.nativeOrder()); // Use native byte order
		vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
		vertexBuffer.put(CUBE_VERTICES); // Copy data into buffer
		vertexBuffer.position(0); // Rewind
		inside = new SquaredBitmap(
				RubiKubeRenderer.COLORS[RubiKubeModel.DARKSIDE][0],
				RubiKubeRenderer.COLORS[RubiKubeModel.DARKSIDE][1],
				RubiKubeRenderer.COLORS[RubiKubeModel.DARKSIDE][2]);

		ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);

	}

	public void reset() {
		for (RubikPieceSide side : sides) {
			side.reset();
		}
	}

	public RubikPiece(RubikPieceSide sides[]) {
		this();
		this.sides = sides;
	}

	public RubikPiece(RubikPieceSide side) {
		this();
		this.sides = new RubikPieceSide[] { side };
	}

	public static RubikPiece makeCornerPiece(int x, int y, int z) {
		int p1 = x == 0 ? RubiKubeModel.LEFT : RubiKubeModel.RIGHT;
		int p2 = y == 0 ? RubiKubeModel.BOTTOM : RubiKubeModel.TOP;
		int p3 = z == 0 ? RubiKubeModel.BACK : RubiKubeModel.FRONT;

		RubikPiece ret = new RubikPiece(new RubikPieceSide[] {
				new RubikPieceSide(p1), new RubikPieceSide(p2),
				new RubikPieceSide(p3) });
		return ret;
	}

	public static RubikPiece makeLateralPiece(int x, int y, int z, int sideSize) {
		int S = sideSize - 1;

		int xs = x == 0 ? 0 : x == S ? 1 : -1;
		int ys = y == 0 ? 0 : y == S ? 1 : -1;
		int zs = z == 0 ? 0 : z == S ? 1 : -1;

		RubikPieceSide[] ret = new RubikPieceSide[2];
		int retp = 0;

		if (xs == 0)
			ret[retp++] = new RubikPieceSide(RubiKubeModel.LEFT);
		if (xs == 1)
			ret[retp++] = new RubikPieceSide(RubiKubeModel.RIGHT);
		if (ys == 0)
			ret[retp++] = new RubikPieceSide(RubiKubeModel.BOTTOM);
		if (ys == 1)
			ret[retp++] = new RubikPieceSide(RubiKubeModel.TOP);
		if (zs == 0)
			ret[retp++] = new RubikPieceSide(RubiKubeModel.BACK);
		if (zs == 1)
			ret[retp++] = new RubikPieceSide(RubiKubeModel.FRONT);

		if (retp == 1) {
			return new RubikPiece(ret[0]);
		}
		RubikPiece rets = new RubikPiece(ret);

		return rets;

	}

	public RubikPieceSide[] getSides() {
		return sides;
	}

	public boolean facesTowards(int towards) {
		for (RubikPieceSide side : sides) {
			if (side.getFace() == towards)
				return true;
		}
		return false;
	}

	public int getColorFacingToward(int towards) {
		for (RubikPieceSide side : sides) {
			if (side.getFace() == towards)
				return side.getColor();
		}
		return -1;
	}

	public RubikPieceSide getSideFacingToward(int towards) {
		for (RubikPieceSide side : sides) {
			if (side.getFace() == towards)
				return side;
		}
		return null;
	}

	// side1 and side2 should be parallels (e.g. FRONT/BACK, UP/DOWN or
	// LEFT/RIGHT
	public boolean inBetween(int side1, int side2) {
		for (RubikPieceSide side : sides) {
			if (side.getFace() == side1)
				return false;
			if (side.getFace() == side2)
				return false;
		}

		return true;
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

		// Render all the faces
		for (int face = 0; face < NUM_SIDES; face++) {
			RubikPieceSide side = null;
			for (int sidecount = 0; sidecount < sides.length; sidecount++) {
				if (sides[sidecount].getFace() == face) {
					side = sides[sidecount];
					side.setTexture(gl);// [side.getColor()][3]
					// System.out.println("Side: " + side);
					break;
				}

			}
			if (side == null) {
				inside.setTexture(gl);
			}

			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, face * 4, 4);

		}
		// textureBuffer.rewind();
		// vertexBuffer.rewind();
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_TEXTURE_2D);

	}

	public void loadTextures(GL10 gl) {
		inside.loadGLTexture(gl);
		for (int sidecount = 0; sidecount < sides.length; sidecount++) {
			if (sides[sidecount] != null) {
				sides[sidecount].load(gl);
			}
		}

	}

	public void rotate(int side, int direction) {
		int[] map;
		if (direction == RubiKubeActionSideRotation.ROT_FORWARD)
			map = RubiKubeActionSideRotation.FWD_ROTATION_MAP[side];
		else {
			map = RubiKubeActionSideRotation.REV_ROTATION_MAP[side];
		}
		this.getIndex().rotate(side, direction);

		// for each face of the piece
		for (int sidecount = 0; sidecount < sides.length; sidecount++) {
			RubikPieceSide rside = sides[sidecount];
			// the face becomes the one mapped by the rotation array
			rside.setFace(map[rside.getFace()]);
			
		}

	}

	public boolean isMiddlePiece() {
		return this.sides.length == 1;
	}

	public void setIndex(Index3d index) {
		this.index = index;
	}

	public Index3d getIndex() {
		return index;
	}
}
