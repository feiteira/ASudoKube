package com.kapouta.aurubik.lib.rubikube;

public interface CubeListener {
	public static final int CUBE_FINISHED = 0;

	public void cubeStateChanged(int what);
}
