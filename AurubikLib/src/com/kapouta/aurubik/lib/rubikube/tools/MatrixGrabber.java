package com.kapouta.aurubik.lib.rubikube.tools;


// http://code.google.com/p/androbuntu/source/browse/trunk/android/src/com/googlecode/androbuntu/Turntable3D/MatrixGrabber.java?spec=svn80&r=80
// http://code.google.com/p/androbuntu/source/browse/trunk/android/src/com/googlecode/androbuntu/Turntable3D/
import javax.microedition.khronos.opengles.GL10;

public class MatrixGrabber {
    public MatrixGrabber() {
        mModelView = new float[16];
        mProjection = new float[16];
    }

    /**
     * Record the current modelView and projection matrix state.
     * Has the side effect of setting the current matrix state to GL_MODELVIEW
     * @param gl
     */
    public void getCurrentState(GL10 gl) {
        getCurrentProjection(gl);
        getCurrentModelView(gl);
    }

    /**
     * Record the current modelView matrix state. Has the side effect of
     * setting the current matrix state to GL_MODELVIEW
     * @param gl
     */
    public void getCurrentModelView(GL10 gl) {
        getMatrix(gl, GL10.GL_MODELVIEW, mModelView);
    }

    /**
     * Record the current projection matrix state. Has the side effect of
     * setting the current matrix state to GL_PROJECTION
     * @param gl
     */
    public void getCurrentProjection(GL10 gl) {
        getMatrix(gl, GL10.GL_PROJECTION, mProjection);
    }

    private void getMatrix(GL10 gl, int mode, float[] mat) {
        MatrixTrackingGL gl2 = new MatrixTrackingGL(gl);
        gl2.glMatrixMode(mode);
        gl2.getMatrix(mat, 0);
    }

    public float[] mModelView;
    public float[] mProjection;
}
