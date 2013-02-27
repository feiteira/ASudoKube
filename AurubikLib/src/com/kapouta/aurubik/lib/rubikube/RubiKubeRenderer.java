package com.kapouta.aurubik.lib.rubikube;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLU;

import com.kapouta.aurubik.lib.AurubikApp;
import com.kapouta.aurubik.lib.arcball.ArcBall;
import com.kapouta.aurubik.lib.arcball.Matrix4f;
import com.kapouta.aurubik.lib.arcball.Quat4f;
import com.kapouta.aurubik.lib.matrixtracking.MatrixTrackingGL;
import com.kapouta.aurubik.lib.rubikube.actions.RubiKubeAction;
import com.kapouta.aurubik.lib.rubikube.actions.RubiKubeActionPickerRotation;
import com.kapouta.aurubik.lib.rubikube.picker.PickerToMouseCoordinateMap;
import com.kapouta.aurubik.lib.rubikube.picker.RubiKubeCoordPicker;
import com.kapouta.aurubik.lib.rubikube.tools.Utils;
import com.kapouta.katools.KTouchQueuedRenderer;

public class RubiKubeRenderer extends KTouchQueuedRenderer {

	// private static float[] BACKGROUND_COLOR = new float[] { 0.9f, 0.9f, 0.9f,
	// 1.0f };

	public static final int CLICK_OUTSIDE_CUBE = 255;

	public static final float[][] COLORS = { // Colors of the 6 faces
	{ 1.0f, 0.0f, 0.0f, 1.0f }, // 0. red - front
			{ 1.0f, 0.5f, 0.0f, 1.0f }, // 1. orange - back
			{ 1.0f, 1.0f, 1.0f, 1.0f }, // 2. white - up
			{ 1.0f, 1.0f, 0.0f, 1.0f }, // 3. yellow - down
			{ 0.0f, 1.0f, 0.0f, 1.0f }, // 4. green - left
			{ 0.0f, 0.0f, 1.0f, 1.0f }, // 5. blue - right
			{ 0.35f, 0.35f, 0.35f, 1.0f } // 6. Dark side
	};
	private static float[] CUBE_BACKGROUND_COLOR = new float[] { 0.85f, 0.85f,
			0.85f, 1.0f };

	public static final float MAX_DIST = 30;

	public static final float MIN_DIST_FOR_ACTION = 0.05f;
	public static final float MAX_DIST_FOR_ACTION = 0.4f;

	public static final float MAX_ROTATION = 27;

	private static final float MIN_ACTION_COMPLETED_BY_USER_FOR_AUTOCOMPLETE = 0.35f;
	public static final float MIN_DIST = 13.5f;

	private static int NSEGMENTS = 36 * 2;
	private ArcBall arcBall;

	private float aspect;
	private Activity context;
	private RubiKubeModel cube;
	private float cubedist = 14f;
	private Matrix4f DefaultRot = new Matrix4f();
	public float[] lastModelView = new float[16];

	// projection, model view and viewport matrixes
	public float[] lastProjection = new float[16];

	private Matrix4f LastRot = new Matrix4f();

	private float[] matrix = new float[16];
	private RubiKubeCoordPicker pickercube;

	private boolean picking;
	private PickerToMouseCoordinateMap pickxy1;
	private float pinch_dist, dist_before_pinch;

	// private GLRectangle[] rects = new GLRectangle[4];

	float[] squareVertices = new float[] { 1, 1, -1, 1, -1, -1, 1, -1 };

	private Boolean syncMouseEvent;

	private Matrix4f ThisRot = new Matrix4f();
	float[][] vertices = new float[4][NSEGMENTS * 2 / 4 + 4];
	public int[] viewport = new int[4];
	private int width, height;

	private AurubikApp app;

	public RubiKubeRenderer(Activity context) {
		this.context = context;

		DefaultRot.setIdentity(); // Reset Default Rotation

		this.app = (AurubikApp) context.getApplication();

		this.cube = app.getModelCube();
		this.pickercube = app.getPickerCube();
		this.cube.reset();

		syncMouseEvent = new Boolean(false);
	}

	private void cleanUpAfterGetMousePixel(GL10 gl) {
		// clean up
		gl.glClearColor(CUBE_BACKGROUND_COLOR[0], CUBE_BACKGROUND_COLOR[1],
				CUBE_BACKGROUND_COLOR[2], CUBE_BACKGROUND_COLOR[3]);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL10.GL_DEPTH_TEST);

	}

	public void drag(float x, float y) {
		y = y_CoordToArcBall(y);

		if (y < 0)
			return;

		Quat4f ThisQuat = new Quat4f();
		Point p = new Point((int) x, (int) y);
		arcBall.drag(p, ThisQuat);

		ThisRot.setRotation(ThisQuat); // Convert Quaternion Into Matrix3fT
		ThisRot.mul(ThisRot, LastRot); // Accumulate Last Rotation Into This

		// System.out.println("AU] DRAG: " + x + " , " + y);
	}

	private void drawCube(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		{
			gl.glLoadIdentity(); // Reset projection matrix
			ThisRot.get(matrix);

			// gl.glRotatef(pinchDegrees, 0.f, 0.f, 1.f);
			gl.glTranslatef(0.0f, 0.0f, -1 * cubedist);
			gl.glPushMatrix(); // NEW: Prepare Dynamic Transform
			gl.glMultMatrixf(matrix, 0);
			cube.draw(gl);
			// pickercube.draw(gl);
			gl.glPopMatrix(); // NEW: Unapply Dynamic Transform
		}
		gl.glPopMatrix();
	}

	private void drawPickerCube(GL10 gl) {

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glDisable(GL10.GL_DITHER);

		gl.glPushMatrix();
		{
			gl.glLoadIdentity(); // Reset projection matrix
			ThisRot.get(matrix);

			// gl.glRotatef(pinchDegrees, 0.f, 0.f, 1.f);
			gl.glTranslatef(0.0f, 0.0f, -1 * cubedist);
			gl.glPushMatrix(); // NEW: Prepare Dynamic Transform
			gl.glMultMatrixf(matrix, 0);
			// cube.draw(gl);
			pickercube.draw(gl);
			gl.glPopMatrix(); // NEW: Unapply Dynamic Transform
		}

		gl.glEnable(GL10.GL_DITHER);
		gl.glPopMatrix();
	}

	public Context getContext() {
		return context;
	}

	public RubiKubeModel getCube() {
		return cube;
	}

	public int getHeight() {
		return height;
	}

	private void getMatrix(GL10 gl, int mode, float[] mat) {
		MatrixTrackingGL gl2 = (MatrixTrackingGL) gl;
		gl2.glMatrixMode(mode);
		gl2.getMatrix(mat, 0);
	}

	private PickerToMouseCoordinateMap getMousePickerPixel(GL10 gl, float x,
			float y) {
		// int x = pixel.get(0) & 0xFF;
		// int y = pixel.get(1) & 0xFF;
		// int face = pixel.get(2) & 0xFF;

		PickerToMouseCoordinateMap ret = null;

		ByteBuffer pixel = ByteBuffer.allocate(4);
		// glReadPixel work differently than Android events, they have
		// reversed Y axis.
		gl.glClearColor(0.0f, 0.0f, 1.0f, 0.0f); // Set color's clear-value
													// to
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// System.out.println("AU] DXY = " + downx + " , " + downy);
		drawPickerCube(gl);
		gl.glReadPixels((int) x, this.height - (int) y, 1, 1, GL10.GL_RGBA,
				GL10.GL_UNSIGNED_BYTE, pixel);

		ret = pickercube.getPickerCoordinateFromPixel(pixel);

		ret.setMouse(x, this.height - y);

		cleanUpAfterGetMousePixel(gl);

		return ret;
	}

	public Boolean getSyncMouseEventObject() {
		return syncMouseEvent;
	}

	public int getWidth() {
		return width;
	}

	private void initFrame(GL10 gl) {
		// Set the viewport (display area) to cover the entire window
		gl.glClearColor(CUBE_BACKGROUND_COLOR[0], CUBE_BACKGROUND_COLOR[1],
				CUBE_BACKGROUND_COLOR[2], CUBE_BACKGROUND_COLOR[3]);

		// gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shadi

		gl.glViewport(0, height / 2 - width / 2, width, width);
		gl.glScissor(0, 0, width, height);// set sci

		gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
		gl.glLoadIdentity(); // Reset projection matrix
		// Use perspective projection
		GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	public boolean isPicking() {
		return picking;
	}

	public boolean isSyncMouseEvent() {
		synchronized (this.syncMouseEvent) {
			return syncMouseEvent;
		}
	}

	public boolean mouseReseted(PickerToMouseCoordinateMap p) {
		if (p.getMouseX() == 1.1f)
			return true;
		return false;
	}

	@Override
	public void onTouchDown(GL10 gl, float x, float y) {
		super.onTouchDown(gl, x, y);
		this.setPicking(false);

		// close any on-going action
		if (!this.cube.getAction().isNoAction()
				&& !this.cube.getAction().isFinished())
			this.cube.getAction().finish();

		PickerToMouseCoordinateMap now = getMousePickerPixel(gl, x, y);
		if (now.getSide() != CLICK_OUTSIDE_CUBE) {
			this.startPick(now);
		} else
			this.startDrag(x, y);
	}

	@Override
	public void onTouchMove(GL10 gl, float x, float y) {
		super.onTouchMove(gl, x, y);

		if (this.isPicking()) {
			this.pick(gl,x,y);
		} else
			this.drag(x, y);
	}

	@Override
	public void onTouchUp(GL10 gl, float x, float y) {
		super.onTouchUp(gl, x, y);
		this.smartResumeOrResetAction();
		this.setPicking(false);
		// this.drag(x, y);
	}

	@Override
	public void onDualTouchDown(GL10 gl, float x1, float y1, float x2, float y2) {
		super.onDualTouchDown(gl, x1, y1, x2, y2);
		this.startDrag(x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2);
	}

	@Override
	public void onDualTouchMove(GL10 gl, float x1, float y1, float x2, float y2) {
		super.onDualTouchMove(gl, x1, y1, x2, y2);
		this.drag(x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2);
	}

	@Override
	public void onDualTouchUp(GL10 gl, float x1, float y1, float x2, float y2) {
		super.onDualTouchUp(gl, x1, y1, x2, y2);
		this.drag(x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		super.onDrawFrame(gl);
		initFrame(gl);

		drawCube(gl);

		this.cube.step();

		// store the projection matrix
		getMatrix(gl, GL10.GL_MODELVIEW, lastModelView);
		getMatrix(gl, GL10.GL_PROJECTION, lastProjection);
		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select model-view matrix
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0)
			height = 1; // To prevent divide by zero
		// aspect = (float) width / height;

		cube.load(gl);
		pickercube.load(gl);

		aspect = 1;
		this.width = width;
		this.height = height;

		// Set the viewport (display area) to cover the entire window
		viewport[0] = 0;
		viewport[1] = height - width / 2;
		viewport[2] = width;
		viewport[3] = width;
		gl.glViewport(0, height - width / 2, width, width);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
		gl.glLoadIdentity(); // Reset projection matrix
		// Use perspective projection
		GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);
		gl.glTranslatef(0.0f, 0.0f, -10.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select model-view matrix
		gl.glLoadIdentity(); // Reset

		// You OpenGL|ES display re-sizing code here

		// LastRot.setIdentity(); // Reset Rotation
		// ThisRot.set(DefaultRot); // Reset Rotation
		ThisRot.get(matrix);

		arcBall = new ArcBall(width, width);
		this.startDrag(width / 2.0f, height / 2.0f);
		this.drag(width * 3 / 3.5f, height * 3 / 5.0f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Set color's clear-value to
		// black
		gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden
		// surface removal
		gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // nice

		gl.glShadeModel(GL10.GL_SMOOTH); // Enable smooth shading of color
		gl.glDisable(GL10.GL_DITHER); // Disable dithering for better

		gl.glEnable(GL10.GL_SCISSOR_TEST);
		cube.load(gl);
		pickercube.load(gl);
	}

	public void pick(GL10 gl, float x, float y) {
		// if the action is running, wait until it finishes.
		if (this.cube.getAction().isRunning()) {
			return;
		}

		PickerToMouseCoordinateMap pickxy2 = getMousePickerPixel(gl, x, y);

		float pickdist = pickxy1.pickDist(pickxy2);

		// this is strange, if you use the edge of the cube it since it's
		// different sides
		// the distance method stops working
		// the condition below, checks this and worksaround by reseting the
		// starting point.
		if (pickxy2.getSide() != pickxy1.getSide()
				&& this.cube.getAction().isFinished()) {
			pickxy1 = pickxy2;
			return;
		}

		// System.out.println("SIDES: " + pickxy1.getSide() + " :: "
		// + pickxy2.getSide());

		// System.out.println("AU] DIST: " + pickdist);
		if (pickdist > MIN_DIST_FOR_ACTION) {
			Object action = this.cube.getAction();

			// IF
			// (its a picker action but starts in a different place (i.e. not
			// the exiting one))
			// THEN
			// make a new action
			if (!(action instanceof RubiKubeActionPickerRotation)
					|| !(((RubiKubeActionPickerRotation) action)
							.getStartLocation().equals(pickxy1))) {

				this.cube.actionStarted(new RubiKubeActionPickerRotation(
						this.cube, pickxy1, pickxy2));
			} else {
				RubiKubeActionPickerRotation pickaction = ((RubiKubeActionPickerRotation) action);
				pickaction.setPoint2(pickxy2);
			}
		}
	}

	public void pinch(float vect_x, float vect_y) {
		this.cubedist = (float) (dist_before_pinch * (pinch_dist / Utils.dist(
				vect_x, vect_y)));
		if (this.cubedist > MAX_DIST)
			this.cubedist = MAX_DIST;
		if (this.cubedist < MIN_DIST)
			this.cubedist = MIN_DIST;
		// Point2f vect = new Point2f(vect_x, vect_y);
		// this.pinchDegrees = pinch_vect.angleWith(vect);
	}

	public void resetAction() {
		// if it´s running let it finish
		if (!this.cube.getAction().isRunning()) {
			this.cube.setAction(RubiKubeAction.noAction());
		}
	}

	public void setPicking(boolean picking) {
		this.picking = picking;
	}

	public void setSyncMouseEvent(boolean syncMouseEvent) {
		synchronized (this.syncMouseEvent) {
			this.syncMouseEvent = syncMouseEvent;
		}
	}

	public void smartResumeOrResetAction() {
		if (!this.cube.getAction().isRunning()) {
			if (this.cube.getAction().getPercentDone() > MIN_ACTION_COMPLETED_BY_USER_FOR_AUTOCOMPLETE)
				this.cube.getAction().resume();
			else
				resetAction();
		}
	}

	public void startDrag(float x, float y) {
		y = y_CoordToArcBall(y);

		if (y < 0)
			return;

		LastRot.set(ThisRot); // Set Last Static Rotation To Last Dynamic
		Point p = new Point((int) x, (int) y);
		arcBall.click(p);
		// System.out.println("AU] start DRAG: " + x + " , " + y);

	}

	public void startPick(PickerToMouseCoordinateMap pcord) {
		this.picking = true;
		this.pickxy1 = pcord;
		this.setPicking(true);
		// System.out.println("AU] Start Pick " + pcord);
	}

	// User interface related functions
	public float y_CoordToArcBall(float y) {
		if (y > height / 2 - width / 2 && y < height / 2 + width / 2)
			return y - (height / 2 - width / 2);

		else if (y <= height / 2 - width / 2)
			return 0;

		else if (y >= height / 2 + width / 2)
			return height / 2 + width / 2;

		return -1;
	}

}
