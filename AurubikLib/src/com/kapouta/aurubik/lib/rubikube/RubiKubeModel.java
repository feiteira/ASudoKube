package com.kapouta.aurubik.lib.rubikube;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.kapouta.aurubik.lib.rubikube.actions.RubiKubeAction;
import com.kapouta.aurubik.lib.rubikube.actions.RubiKubeActionSideRotation;
import com.kapouta.aurubik.lib.rubikube.piece.RubikPiece;
import com.kapouta.aurubik.lib.rubikube.tools.Index3d;

public class RubiKubeModel {

	/*
	 * X axis: LEFT / RIGHT Y axis: BOTTOM / TOP Z axis: FRONT / BACK *
	 */
	public static final int NSIDES = 6;

	public static final int FRONT = 0;
	public static final int BACK = 1;
	public static final int TOP = 2;
	public static final int BOTTOM = 3;
	public static final int LEFT = 4;
	public static final int RIGHT = 5;
	public static final int DARKSIDE = 6;

	private RubiKubeAction action;

	public static final int CUBE_WIDTH = 2;

	// A normal rubik cube has three RubikPiece per side (3x3)
	private int sideSize;

	private RubikPiece pieces[][][];

	private boolean isDrawing = true;

	private boolean texturesLoaded;

	private ArrayList<RubiKubeAction> undoActionArray = new ArrayList<RubiKubeAction>();;
	ArrayList<CubeListener> cubelisteners = new ArrayList<CubeListener>();

	private Random randomizer;

	public RubiKubeModel() {
		this(3);// default is a 3 x 3 x 3
	}

	public RubiKubeModel(int sideSize) {
		this.randomizer = new Random(System.currentTimeMillis());
		this.sideSize = sideSize;
		// this.pieces = new RubikPiece[sideSize][sideSize][sideSize];
	
		this.pieces = new RubikPiece[sideSize][sideSize][sideSize];
	
		int S = sideSize - 1;
		pieces[0][0][0] = RubikPiece.makeCornerPiece(0, 0, 0);
		pieces[0][0][S] = RubikPiece.makeCornerPiece(0, 0, S);
		pieces[0][S][0] = RubikPiece.makeCornerPiece(0, S, 0);
		pieces[0][S][S] = RubikPiece.makeCornerPiece(0, S, S);
		pieces[S][0][0] = RubikPiece.makeCornerPiece(S, 0, 0);
		pieces[S][0][S] = RubikPiece.makeCornerPiece(S, 0, S);
		pieces[S][S][0] = RubikPiece.makeCornerPiece(S, S, 0);
		pieces[S][S][S] = RubikPiece.makeCornerPiece(S, S, S);
	
		// for each dimention, start the RubikPiece
		for (int d1 = 0; d1 < sideSize; d1++) {
			for (int d2 = 0; d2 < sideSize; d2++) {
				for (int d3 = 0; d3 < sideSize; d3++) {
					// if it's null, its not a corner
					if (pieces[d1][d2][d3] == null) {
						// it's lateral
						if (d1 == 0 || d2 == 0 || d3 == 0 || d1 == S || d2 == S
								|| d3 == S) {
							pieces[d1][d2][d3] = RubikPiece.makeLateralPiece(
									d1, d2, d3, sideSize);
						}
					}
				}
			}
		}
		startPiecePositions();
		this.action = RubiKubeAction.noAction();
	}
	
	public void setRandomizerSeed(long seed){
		this.randomizer = new Random(seed);
	}
	
	public float random(){
		return this.randomizer.nextFloat();
	}

	private void startPiecePositions() {
		for (int d1 = 0; d1 < sideSize; d1++) {
			for (int d2 = 0; d2 < sideSize; d2++) {
				for (int d3 = 0; d3 < sideSize; d3++) {
					// if it's null, its not a corner
					if (pieces[d1][d2][d3] != null) {
						pieces[d1][d2][d3].setIndex(new Index3d(d1 - 1, d2 - 1,
								d3 - 1));
					}
				}
			}
		}
	}

	public void clearUndoLog() {
		undoActionArray.clear();
	}

	public RubiKubeAction makeRandomRotation() {
		if (this.action != null && this.action.isRunning())
			this.action.finish();

		action = new RubiKubeActionSideRotation(this,
				(int) (this.random() * NSIDES), this.random() < 0.5f ? -1 : 1);
		return action;
	}
	
	

	public boolean isActionRunning() {
		if (action == null)
			return false;

		return action.isRunning();
	}

	public void actionStarted(RubiKubeAction action) {
		this.action = action;
	}

	public void actionFinished(RubiKubeAction action) {
		if (this.action != action) {
			this.action.finish();
		}

		if (undoActionArray.size() != 0
				&& this.undoActionArray.get(undoActionArray.size() - 1).equals(
						this.action)) {
			this.undoActionArray.remove(undoActionArray.size() - 1);
		} else
			undoActionArray.add(action);

		this.action.updateModel();

		if (this.isFinished()) {
			this.notifyCubeListeners(CubeListener.CUBE_FINISHED);
		}

	}

	public void reset() {
		for (int d1 = 0; d1 < sideSize; d1++) {
			for (int d2 = 0; d2 < sideSize; d2++) {
				for (int d3 = 0; d3 < sideSize; d3++) {
					// if it's null, its not a corner
					if (pieces[d1][d2][d3] != null) {
						pieces[d1][d2][d3].reset();
						pieces[d1][d2][d3].setIndex(new Index3d(d1 - 1, d2 - 1,
								d3 - 1));
					}
				}
			}
		}
	}

	public void scrambleFast(int n) {
		RubiKubeActionSideRotation o_act = (RubiKubeActionSideRotation) makeRandomRotation();
		float dir = this.random() < 0.5f ? -1 : 1;

		o_act.setDirection((int) dir);
		this.action.finish();

		RubiKubeActionSideRotation n_act = null;

		for (int cnt = 1; cnt < n; cnt++) {

			n_act = (RubiKubeActionSideRotation) makeRandomRotation();
			while (RubiKubeActionSideRotation.areParalelSides(o_act.getSide(),
					n_act.getSide())) {

				n_act = (RubiKubeActionSideRotation) makeRandomRotation();
			}
			n_act.setDirection((int) dir);

			this.action.finish();

		}
	}

	public void waitAllActionsFinished() {

		try {
			Thread.sleep(30);

			while (!action.isNoAction() || action.isRunning()) {

				while (!action.isNoAction() || action.isRunning()) {
					Thread.sleep(25);
				}

				// After it finishes, it waits to see if there is another one
				// starting any time soon
				// this is mostly due to the scramble() function.
				Thread.sleep(20);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void undo() {
		if (undoActionArray.size() == 0)
			return;

		if (this.action.isRunning())
			this.action.finish();

		if (undoActionArray.size() == 0)
			return;

		RubiKubeAction last = this.undoActionArray
				.get(undoActionArray.size() - 1);
		if (last instanceof RubiKubeActionSideRotation) {
			RubiKubeActionSideRotation tact = (RubiKubeActionSideRotation) last;
			tact.undo();
			this.setAction(tact);
			this.action.start();
		}
	}

	public int getSideSize() {
		return sideSize;
	}

	public void load(GL10 gl) {

		for (int d1 = 0; d1 < sideSize; d1++) {
			for (int d2 = 0; d2 < sideSize; d2++) {
				for (int d3 = 0; d3 < sideSize; d3++) {
					if (this.pieces[d1][d2][d3] != null) {
						this.pieces[d1][d2][d3].loadTextures(gl);
					}
				}
			}
		}
		this.texturesLoaded = true;
	}

	public void draw(GL10 gl) {
		if (!this.isTexturesLoaded())
			load(gl);

		if (this.isDrawing) {
			for (int d1 = 0; d1 < sideSize; d1++) {
				for (int d2 = 0; d2 < sideSize; d2++) {
					for (int d3 = 0; d3 < sideSize; d3++) {
						if (this.pieces[d1][d2][d3] != null) {
							gl.glPushMatrix();
							action.transform(gl, pieces[d1][d2][d3]);

							Index3d i = pieces[d1][d2][d3].getIndex();
							gl.glTranslatef(CUBE_WIDTH * i.getX(), CUBE_WIDTH
									* i.getY(), CUBE_WIDTH * i.getZ());

							pieces[d1][d2][d3].draw(gl);
							gl.glPopMatrix();
						}
					}
				}
			}
		}
	}

	public void step(){
		if(this.action != null){
			action.step();
		}
	}
	
	public RubikPiece[] getSide(int side) {
		RubikPiece[] ret = new RubikPiece[sideSize * sideSize];

		int dc = 0;
		for (int d1 = 0; d1 < sideSize; d1++) {
			for (int d2 = 0; d2 < sideSize; d2++) {
				for (int d3 = 0; d3 < sideSize; d3++) {
					if (this.pieces[d1][d2][d3] != null
							&& this.pieces[d1][d2][d3].facesTowards(side)) {
						ret[dc] = this.pieces[d1][d2][d3];
						dc++;
					}
				}
			}
		}
		return ret;
	}

	public RubikPiece[] getBetweenSides(int side1, int side2) {
		RubikPiece[] tmp = new RubikPiece[sideSize * sideSize];

		int dc = 0;
		for (int d1 = 0; d1 < sideSize; d1++) {
			for (int d2 = 0; d2 < sideSize; d2++) {
				for (int d3 = 0; d3 < sideSize; d3++) {
					if (this.pieces[d1][d2][d3] != null)
						if (!this.pieces[d1][d2][d3].facesTowards(side1)
								&& !this.pieces[d1][d2][d3].facesTowards(side2)) {
							tmp[dc] = this.pieces[d1][d2][d3];
							dc++;
						}
				}
			}
		}

		RubikPiece[] ret = new RubikPiece[dc + 1];

		for (int d = 0; d < ret.length; d++) {
			ret[d] = tmp[d];
		}

		return ret;
	}

	public RubikPiece getPiece(int d1, int d2, int d3) {
		return pieces[d1][d2][d3];
	}

	public void setPiece(int d1, int d2, int d3, RubikPiece piece) {
		pieces[d1][d2][d3] = piece;
	}

	public RubiKubeAction getAction() {
		return action;
	}

	public void setAction(RubiKubeAction action) {
		this.action = action;
	}

	private boolean isTexturesLoaded() {
		return texturesLoaded;
	}

	public boolean isFinished() {
		for (int side_counter = 0; side_counter < NSIDES; side_counter++) {
			RubikPiece[] tmp_side_array = getSide(side_counter);
			int color = tmp_side_array[0].getColorFacingToward(side_counter);
			for (RubikPiece tmp_piece : tmp_side_array) {
				if (tmp_piece.getColorFacingToward(side_counter) != color)
					return false;
			}
		}

		return true;

	}

	public void addCubeListener(CubeListener cl) {
		cubelisteners.add(cl);
	}

	public void removeCubeListener(CubeListener cl) {
		cubelisteners.remove(cl);
	}

	public void notifyCubeListeners(int state) {
		for (CubeListener cl : cubelisteners) {
			cl.cubeStateChanged(state);
		}
	}

}
