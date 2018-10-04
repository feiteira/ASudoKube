package com.kapouta.aurubik.lib.rubikube.actions;

import javax.microedition.khronos.opengles.GL10;

import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.piece.RubikPiece;

public abstract class RubiKubeAction {
	public static final float DEFAULT_ACTION_SPEED = 0.1f;
	public static RubiKubeAction noAction() {
		RubiKubeAction ret = new RubiKubeAction(null) {

			@Override
			public boolean isRunning() {
				return false;
			}

			@Override
			public void transform(GL10 gl, RubikPiece piece) {
			}

			@Override
			public void updateModel() {
			}

		};
		ret.running = false;
		ret.finished = true;

		return ret;
	}

	private boolean finished;
	protected RubiKubeModel model;
	private float number_of_rotations = 1.0f;
	private float percent_done;

	private boolean running = false;

	private float speed = DEFAULT_ACTION_SPEED;

	public RubiKubeAction(RubiKubeModel model) {
		this.model = model;
		number_of_rotations = 1.0f;
		this.finished = false;
	}

	public void finish() {
		this.running = false;
		model.actionFinished(this);
		percent_done = 0.0f;
		this.finished = true;
	}

	public float getNumber_of_rotations() {
		return number_of_rotations;
	}

	public float getPercentDone() {
		return percent_done;
	}

	/*
	 * Used for display: p = 0.0f, action not yet started p = 0.5f, action 50%
	 * executed p = 1.0f, action finished, update model
	 */

	public boolean isFinished() {
		return finished;
	}

	public boolean isNoAction() {
		return model == null;
	}

	public boolean isRunning() {
		return running;
	}

	public void pause() {
		this.running = false;
	}

	public void reset() {
		this.percent_done = 0f;
		this.finished = false;
		this.running = false;
	}

	public void resume() {
		if (this.finished)
			return;
		this.running = true;
	}

	public void setNumber_of_rotations(float number_of_rotations) {
		this.number_of_rotations = number_of_rotations;
	}

	public void setPercentDone(float percent_done) {
		this.percent_done = percent_done;
		this.number_of_rotations = Math.round(percent_done);
		if(number_of_rotations < 1.0f)
			number_of_rotations = 1.0f;
	}

	public void setRunning(boolean running) {
		if (this.finished)
			return;

		this.running = running;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
		if (this.speed <= 0.0f)
			throw new RuntimeException(
					"This will not work, speed needs to be > 0.");
	}

	public void start() {
		if (this.finished)
			return;
		this.running = true;
		model.actionStarted(this);
		percent_done = 0.0f;
	}

	public boolean step() {
		if (this.finished)
			return false;

		if (!this.running)
			return false;

		if (this.speed <= 0)
			throw new RuntimeException("Run speed not set, use "
					+ this.getClass().getName() + ".setSpeed()");
		//
		// if (percent_done >= 1.0f)
		// return false;

		percent_done += speed;

		if (percent_done > this.number_of_rotations)
			percent_done = this.number_of_rotations;

		if (percent_done == this.number_of_rotations)
			finish();

		return true;
	}

	public void stop() {
		if (this.finished)
			return;

		this.running = false;
		model.actionFinished(this);
		percent_done = 100.0f;
	}

	public abstract void transform(GL10 gl, RubikPiece piece);

	public abstract void updateModel();

}
