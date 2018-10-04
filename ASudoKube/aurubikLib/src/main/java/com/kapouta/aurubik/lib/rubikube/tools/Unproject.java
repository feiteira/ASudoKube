package com.kapouta.aurubik.lib.rubikube.tools;

public class Unproject {
	
	public static float[] unproject(float rx, float ry, float rz,float viewport[]) {
	    MatrixGrabber mg = new MatrixGrabber();

	    float[] modelInv = new float[16];
	    if (!android.opengl.Matrix.invertM(modelInv, 0, mg.mModelView, 0))
	        throw new IllegalArgumentException("ModelView is not invertible.");
	    float[] projInv = new float[16];
	    if (!android.opengl.Matrix.invertM(projInv, 0, mg.mProjection, 0))
	        throw new IllegalArgumentException("Projection is not invertible.");
	    
	    float[] combo = new float[16];
	    android.opengl.Matrix.multiplyMM(combo, 0, modelInv, 0, projInv, 0);
	    float[] result = new float[4];
	    float vx = viewport[0];
	    float vy = viewport[1];
	    float vw = viewport[2];
	    float vh = viewport[3];
	    float[] rhsVec = {((2*(rx-vx))/vw)-1,((2*(ry-vy))/vh)-1,2*rz-1,1};
	    android.opengl.Matrix.multiplyMV(result, 0, combo, 0, rhsVec, 0);
	    float d = 1 / result[3];
	    float[] endResult = {result[0] * d, result[1] * d, result[2] * d};
	    return endResult;
	}

	public static float distanceToDepth(float distance,float fNear,float fFar) {
	    return ((1/fNear) - (1/distance))/((1/fNear) - (1/fFar));
	}
}
