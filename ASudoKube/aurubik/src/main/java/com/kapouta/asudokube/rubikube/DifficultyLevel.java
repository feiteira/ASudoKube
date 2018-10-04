package com.kapouta.asudokube.rubikube;

import java.io.Serializable;

public class DifficultyLevel implements Serializable {
	// identifiers for our dialogues
	static final int DIALOG_PROGRESS = 0;
	static final int DIALOG_SUCCESS = 1;
	static final int DIALOG_FAILED = 2;

	private static final long serialVersionUID = 1L;

	public static final int N_LEVELS = 20;

	public static final int RESULT_ONE_GAME_FINISHED = 0;
	public static final int RESULT_LEVEL_FINISHED = 1;

	private int level;
	private int amount_of_successes;

	// new stuff added on v 1.7

	private float best_time = -1f;

	private boolean locked;

	public static int getScrambleFromLevel(int l) {
		int ret = 30;
		return ret;
	}

	public static int getMode(int level) {
		return (level-1) / 3;
	}

	public boolean updateBestTime(float time) {
		if (best_time <= 0) {
			best_time = time;
			return true;
		}
		if (time < best_time) {
			best_time = time;
			return true;
		}
		return false;
	}



	public float getBest_time() {
		return best_time;
	}

	public void setBest_time(float best_time) {
		this.best_time = best_time;
	}

	public boolean levelMastered() {
		int required_sucess = (int) (2 + Math.sqrt(level + level));

		if (amount_of_successes >= required_sucess)
			return true;
		return false;
	}

	// constructor
	public DifficultyLevel(int l, int a) {
		this.level = l;
		this.amount_of_successes = a;
		this.locked = true;
	}

	public void lock() {
		this.locked = true;
	}

	public void unlock() {
		this.locked = false;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public int finishAGame() {
		boolean lf = levelFinished();
		this.amount_of_successes++;
		int ret = RESULT_ONE_GAME_FINISHED;

		if (lf != levelFinished())
			ret = RESULT_LEVEL_FINISHED;

		return ret;
	}

	public boolean levelFinished() {
		if (amount_of_successes >= 1)
			return true;
		return false;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getAmount_of_successes() {
		return amount_of_successes;
	}

	public void setAmount_of_successes(int amount_of_successes) {
		this.amount_of_successes = amount_of_successes;
	}

}
