package com.kapouta.aurubik.lib.rubikube.actions;

import javax.microedition.khronos.opengles.GL10;

import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.piece.RubikPiece;

public class RubiKubeActionSideRotation extends RubiKubeAction {
	public static final int ROT_FORWARD = 1;
	public static final int ROT_BACKWARD = -1;

	// These array represent the rotation order around each axis, e.g.
	// If you rotate the X axis and your side is "BACK" then it will become
	// "BOTTOM" (and vice-versa if you rotate backwards)
	// Note: Some sides remain the same and are not represented here (e.g. LEFT
	// and RIGHT remain if you rotate on the X axis)
	private static final int[] MAPRotateX = new int[] { RubiKubeModel.BACK,
			RubiKubeModel.BOTTOM, RubiKubeModel.FRONT, RubiKubeModel.TOP };
	private static final int[] MAPRotateY = new int[] { RubiKubeModel.BACK,
			RubiKubeModel.RIGHT, RubiKubeModel.FRONT, RubiKubeModel.LEFT };
	private static final int[] MAPRotateZ = new int[] { RubiKubeModel.TOP,
			RubiKubeModel.LEFT, RubiKubeModel.BOTTOM, RubiKubeModel.RIGHT };

	// These array are updated in the static section of the class (ran a
	// classload time)
	// The rotation maps are used to update all the information on all sides of
	// a piece.
	// e.g. if you apply a forward rotation to the TOP of the cube anda piece
	// also has side pointing 0towards LEFT, then - after rotation - it will be
	// turning BACK.
	//
	// this can be seen as:
	// FWD_ROTATION_MAP[TOP][LEFT] == BACK
	public static final int[][] FWD_ROTATION_MAP;
	public static final int[][] REV_ROTATION_MAP;// reverse

	protected int direction;
	protected int side;
	protected boolean middlerotation;
	protected int leftside;
	protected int rightside;

	static {
		FWD_ROTATION_MAP = new int[RubiKubeModel.NSIDES][RubiKubeModel.NSIDES];
		REV_ROTATION_MAP = new int[RubiKubeModel.NSIDES][RubiKubeModel.NSIDES];

		for (int cnt = 0; cnt < RubiKubeModel.NSIDES; cnt++) {
			switch (cnt) {
			case RubiKubeModel.RIGHT:
			case RubiKubeModel.LEFT:
				FWD_ROTATION_MAP[cnt] = makeRotationMap(MAPRotateX, ROT_FORWARD);
				REV_ROTATION_MAP[cnt] = makeRotationMap(MAPRotateX,
						ROT_BACKWARD);
				break;

			case RubiKubeModel.TOP:
			case RubiKubeModel.BOTTOM:
				FWD_ROTATION_MAP[cnt] = makeRotationMap(MAPRotateY, ROT_FORWARD);
				REV_ROTATION_MAP[cnt] = makeRotationMap(MAPRotateY,
						ROT_BACKWARD);
				break;

			case RubiKubeModel.BACK:
			case RubiKubeModel.FRONT:
				FWD_ROTATION_MAP[cnt] = makeRotationMap(MAPRotateZ, ROT_FORWARD);
				REV_ROTATION_MAP[cnt] = makeRotationMap(MAPRotateZ,
						ROT_BACKWARD);
				break;
			}
		}
	}

	public static boolean areParalelSides(int s1, int s2) {
		if (s1 == s2)
			return true;
		// FRONT = 0;
		// BACK = 1;
		// TOP = 2;
		// BOTTOM = 3;
		// LEFT = 4;
		// RIGHT = 5;

		if (s1 % 2 == 0 && (s1 + 1) == s2)
			return true;

		if (s2 % 2 == 0 && (s2 + 1) == s1)
			return true;

		return false;
	}

	public RubiKubeActionSideRotation(RubiKubeModel model, int side,
			int direction) {
		super(model);
		this.direction = direction;
		this.side = side;
	}

	public RubiKubeActionSideRotation(RubiKubeModel model, int side,
			int direction, int left, int right) {
		this(model, side, direction);
		this.middlerotation = true;
		this.leftside = left;
		this.rightside = right;
	}

	protected RubiKubeActionSideRotation(RubiKubeModel model) {
		super(model);
	}

	protected int[] getRotAxisFromRotationSide() {
		return getRotAxis(side, direction);
	}

	protected int[] getRotAxisFromRotationSide(int ss) {
		return getRotAxis(ss, direction);
	}

	public static int[] getRotAxis(int s_side, int s_direction) {
		int ret[] = new int[3];
		switch (s_side) {// yes we switch sides easily :P
		case RubiKubeModel.RIGHT:
		case RubiKubeModel.LEFT:
			ret[0] = -1 * s_direction;
			ret[1] = 0;
			ret[2] = 0;
			break;

		case RubiKubeModel.TOP:
		case RubiKubeModel.BOTTOM:
			ret[0] = 0;
			ret[1] = -1 * s_direction;
			ret[2] = 0;
			break;

		case RubiKubeModel.BACK:
		case RubiKubeModel.FRONT:
			ret[0] = 0;
			ret[1] = 0;
			ret[2] = 1 * s_direction;
			break;
		}
		return ret;
	}

	private static int[] makeRotationMap(int[] rotateminimap, int direction) {
		int[] ret = new int[RubiKubeModel.NSIDES];
		/*
		 * The mini map indicates for each side what would be the next after
		 * rotation, if it's omitted, then the side remains after rotation
		 * 
		 * this funtion returns a full rotation map, for each side what will be
		 * the next (or previous)
		 * 
		 * now, for each side, update it
		 */
		for (int cside = 0; cside < RubiKubeModel.NSIDES; cside++) {
			// is there a mapping for this side?
			for (int rcnt = 0; rcnt < rotateminimap.length; rcnt++) {
				if (direction == ROT_FORWARD) {
					if (rotateminimap[rcnt] == cside) {// yes
						ret[cside] = rotateminimap[(1 + rcnt)
								% rotateminimap.length];
						break;
					}
					ret[cside] = cside;

				} else {// rotate backwards

					if (rotateminimap[rcnt] == cside) {
						int idx = (rcnt - 1);
						if (idx < 0)
							idx = rotateminimap.length - 1;
						ret[cside] = rotateminimap[idx];
						break;
					}
					ret[cside] = cside;
				}
			}
		}
		return ret;
	}

	@Override
	public void transform(GL10 gl, RubikPiece piece) {
		if (this.middlerotation) {
			if (!piece.facesTowards(leftside) && !piece.facesTowards(rightside)) {
				int[] axis = getRotAxisFromRotationSide(leftside);
				gl.glRotatef(this.getPercentDone() * 90.0f, axis[0], axis[1],
						axis[2]);
			}
		} else {
			if (piece.facesTowards(side)) {
				int[] axis = getRotAxisFromRotationSide();
				gl.glRotatef(this.getPercentDone() * 90.0f, axis[0], axis[1],
						axis[2]);
			}
		}
	}

	@Override
	public void updateModel() {
		for (int cnt = 0; cnt <this.getNumber_of_rotations(); cnt++) {
			this.updateModelOnce();
		}
	}

	private void updateModelOnce() {
		if (this.middlerotation) {
			RubikPiece[] pieces = this.model.getBetweenSides(leftside,
					rightside);

			for (int p_cnt = 0; p_cnt < pieces.length; p_cnt++) {
				if (pieces[p_cnt] != null) {
					pieces[p_cnt].rotate(leftside, direction);

				}
			}
		} else {
			RubikPiece[] pieces = this.model.getSide(side);
			for (int p_cnt = 0; p_cnt < pieces.length; p_cnt++) {
				if (pieces[p_cnt] != null) {
					pieces[p_cnt].rotate(side, direction);

				}
			}
		}

	}

	public void undo() {
		this.reset();
		this.direction = -this.direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getSide() {
		return side;
	}

	public boolean isMiddlerotation() {
		return middlerotation;
	}

}
