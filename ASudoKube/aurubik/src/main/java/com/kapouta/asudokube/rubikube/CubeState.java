package com.kapouta.asudokube.rubikube;

public class CubeState {

	public static final int STATE_FREEPLAY_MODE = 0;
	public static final int STATE_LEVEL_MODE = 1;

	
	private int playmode;

	private float duration;
	private DifficultyLevel level;

	public CubeState(){}
	
	public CubeState(int playmode) {
		this.playmode = playmode;
	}
	
	public CubeState(int playmode, float duration, DifficultyLevel level){
		this.playmode = playmode;
		this.duration = duration;
		this.level = level;
	}
	
	public static CubeState makeFreePlayModeInstance(){
		return new CubeState(CubeState.STATE_FREEPLAY_MODE);
	}

	public static CubeState makeLevelModeInstance(float duration, DifficultyLevel level){
		return new CubeState(CubeState.STATE_FREEPLAY_MODE);
	}
	
	
	public int getPlaymode() {
		return playmode;
	}

	public void setPlaymode(int playmode) {
		this.playmode = playmode;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public DifficultyLevel getLevel() {
		return level;
	}

	public void setLevel(DifficultyLevel level) {
		this.level = level;
	}

}
