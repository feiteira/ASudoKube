package com.kapouta.aurubik.lib.rubikube.actions;

import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.picker.PickerToMouseCoordinateMap;

public class RubiKubeActionPickerRotation extends RubiKubeActionSideRotation {

	/*
	 * Now, in here we get three coordinates: x,y and side
	 * 
	 * We need to convert this to cube rotation coordinates.
	 * 
	 * First we need to get the direction of the side we'll be rotating.
	 * 
	 * From the x,y,side coordinates we can see the rotation.
	 * 
	 * e.g.
	 * 
	 * IF ( FACE = FRONT AND DIRECTION = to right
	 * 
	 * THEN ROTATION => LEFT
	 * 
	 * 
	 * The table FACE_AND_DIRECTION_TO_ROTATION indicates the target rotation side for each combination.
	 */

	public static final int DIRECTION_UP = 0;
	public static final int DIRECTION_DOWN = 1;
	public static final int DIRECTION_LEFT = 2;
	public static final int DIRECTION_RIGHT = 3;

	public static final int N_DIRECTIONS = 4;

	public static final int FACE_AND_DIRECTION_TO_ROTATION[][];

	public static final float MIN_PERCENT_DONE_BEFORE_AUTOFINISH = 0.4f;

	// UP (1, 0.5), DOWN (0, 0.5), LEFT (0.5, 0), RIGHT (0.5, 1)
	public static final PickerToMouseCoordinateMap DIRECTION_MARKERS[] = { new PickerToMouseCoordinateMap(1f, 0.5f),
			new PickerToMouseCoordinateMap(0f, 0.5f), new PickerToMouseCoordinateMap(0.5f, 0f),
			new PickerToMouseCoordinateMap(0.5f, 1f) };

	public static final PickerToMouseCoordinateMap CENTER = new PickerToMouseCoordinateMap(0.5f, 0.5f);
	/*
	 * FRONT = 0; BACK = 1; TOP = 2; BOTTOM = 3; LEFT = 4; RIGHT = 5;
	 */

	// DIRECTION MODIFIERS:
	// We can obtain the direction vector from both points (DV = Point2 -
	// Point1)
	// The we decide if it's UP, DOWN or LEFT, RIGHT depending on Dv.
	// The highest axis value determins if it's LEFT/RIGHT (when DVx > DVy ) or
	// UP/DOWN
	//
	// The signal of the DVx or DVy will determin if it's UP or DOWN
	// However some cube OPENGL coordinates are in different orders (I copied
	// them from the web)
	//
	// It's a quick fix, but with this direction modifier I can Fix the
	// direction without changing the cube 3d GL coordinates.
	public static final int DIRECTION_MODIFIER_X[] = { 1, -1, 1, 1, -1, 1 };
	public static final int DIRECTION_MODIFIER_Y[] = { 1, 1, 1, -1, 1, 1 };

	// When converted from 0 - 1.0f to 0,1 or 2 (3 rows as in the cube)
	public static final int X_TOP = 0;
	public static final int X_MID = 1;
	public static final int X_BOTTOM = 2;
	public static final int Y_LEFT = 0;
	public static final int Y_MID = 1;
	public static final int Y_RIGHT = 2;
	public static final float DIST_TO_PERFORMED_ACTION_RATIO = 2.5f;

	static {
		FACE_AND_DIRECTION_TO_ROTATION = new int[][] {// int[RubiKubeModel.NSIDES][N_DIRECTIONS]{
		{// # FRONT
				RubiKubeModel.TOP, // DIRECTION_UP
						RubiKubeModel.BOTTOM, // DIRECTION_DOWN
						RubiKubeModel.LEFT, // DIRECTION_LEFT
						RubiKubeModel.RIGHT // DIRECTION_RIGHT
				}, {// # BACK
				RubiKubeModel.TOP, // DIRECTION_UP
						RubiKubeModel.BOTTOM, // DIRECTION_DOWN
						RubiKubeModel.RIGHT, // DIRECTION_LEFT
						RubiKubeModel.LEFT // DIRECTION_RIGHT
				}, { // # TOP
				RubiKubeModel.BACK,// up
						RubiKubeModel.FRONT, // down
						RubiKubeModel.LEFT, // left
						RubiKubeModel.RIGHT // right
				}, { // # BOTTOM
				RubiKubeModel.FRONT, // up
						RubiKubeModel.BACK,// down
						RubiKubeModel.LEFT, // left
						RubiKubeModel.RIGHT // right
				}, { // # LEFT
				RubiKubeModel.TOP, // up
						RubiKubeModel.BOTTOM,// down
						RubiKubeModel.BACK,// left
						RubiKubeModel.FRONT // right
				}, { // # RIGHT
				RubiKubeModel.TOP, // up
						RubiKubeModel.BOTTOM,// down
						RubiKubeModel.FRONT,// left
						RubiKubeModel.BACK // right
				} };
	}

	private PickerToMouseCoordinateMap p1;
	private PickerToMouseCoordinateMap p2;
	private boolean horizontal_rotation;
	private int direction_modifer;

	public RubiKubeActionPickerRotation(RubiKubeModel model, int side, int direction) {
		super(model, side, direction);

	}

	public PickerToMouseCoordinateMap getStartLocation() {
		return this.p1;
	}

	public int getSideToRotate(PickerToMouseCoordinateMap p1) {
		// goes thorough the markers and sees which Marker is closer
		int ret = 0;
		float mindist = DIRECTION_MARKERS[0].pickDist(p1);

		for (int cnt = 1; cnt < DIRECTION_MARKERS.length; cnt++)
			if (mindist > DIRECTION_MARKERS[cnt].pickDist(p1)) {
				mindist = DIRECTION_MARKERS[cnt].pickDist(p1);
				ret = cnt;
			}

		return ret;
	}

	public RubiKubeActionPickerRotation(RubiKubeModel model, PickerToMouseCoordinateMap p1, PickerToMouseCoordinateMap p2) {
		super(model);

		this.p1 = p1;
		this.p2 = p2;

		recalculate();
	}

	private void recalculate() {
		if (p1.getSide() != p2.getSide()) {
			// this should never happen -
			System.out.println("Finish: " + this.getClass());
			finish();
			return;
		}
		int x1 = (int) (p1.getPickX() * 3); // converts from 0..1.0f to 0,1 or 2 (3
		// rows as in the cube)
		int y1 = (int) (p1.getPickY() * 3); // converts from 0..1.0f to 0,1 or 2 (3
		// rows as in the cube)
		// int x2 = (int) (p1.getX() * 3); // converts from 0..1.0f to 0,1 or 2
		// (3 rows as in the cube)
		// int y2 = (int) (p1.getY() * 3); // converts from 0..1.0f to 0,1 or 2
		// (3 rows as in the cube)
		//

		float dx = p2.getPickX() - p1.getPickX();
		float dy = p2.getPickY() - p1.getPickY();

		this.side = p1.getSide();

		/*
		 * UP (1, 0.5) DOWN (0, 0.5) LEFT (0.5, 0) RIGHT (0.5, 1)
		 */
		if (mod(dx) > mod(dy)) { // horizontal rotation
			this.horizontal_rotation = true;
			this.direction_modifer = DIRECTION_MODIFIER_X[this.side];
			this.direction = dx > 0 ? 1 : -1;
			this.direction *= this.direction_modifer;
			switch (y1) {
			case Y_MID: // MIDDLE ROTATION
				this.middlerotation = true;
				this.leftside = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_LEFT];
				this.rightside = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_RIGHT];
				break;

			case Y_LEFT:
				this.side = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_LEFT];
				break;

			case Y_RIGHT:
				this.side = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_RIGHT];
				break;
			}
			this.setPercentDone(mod(dx));

		} else {// vertical rotation
			this.horizontal_rotation = false;
			this.direction = dy > 0 ? -1 : 1;
			this.direction_modifer = DIRECTION_MODIFIER_Y[this.side];
			this.direction *= this.direction_modifer;

			switch (x1) {
			case X_MID: // MIDDLE ROTATION
				this.middlerotation = true;
				this.leftside = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_UP];
				this.rightside = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_DOWN];
				break;
			case X_BOTTOM:
				this.side = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_UP];
				break;

			case X_TOP:
				this.side = FACE_AND_DIRECTION_TO_ROTATION[side][DIRECTION_DOWN];
				break;

			}
			this.setPercentDone(mod(dy));

		}
	}

	public void recalculatePercentDone() {
		float dx = p2.getPickX() - p1.getPickX();
		float dy = p2.getPickY() - p1.getPickY();

		if (this.horizontal_rotation) {
			this.direction = dx > 0 ? 1 : -1;
			this.direction *= this.direction_modifer;
			this.setPercentDone(mod(dx));
		} else {
			this.direction = dy > 0 ? -1 : 1;
			this.direction *= this.direction_modifer;
			this.setPercentDone(mod(dy));
		}

	}

	private float mod(float f) {
		if (f < 0)
			return -f;
		return f;
	}

	public void setPoint2(PickerToMouseCoordinateMap pickxy2) {
		if (this.p2.getSide() == pickxy2.getSide()) {
			this.p2 = pickxy2;
		} else {
			PickerToMouseCoordinateMap.inferPickerPointFromVector(this.p1, this.p2, pickxy2);
			this.p2 = pickxy2;
		}

		recalculatePercentDone();

		//
		// if (this.getPercentDone() > MIN_PERCENT_DONE_BEFORE_AUTOFINISH)
		// this.resume();

	}

}
