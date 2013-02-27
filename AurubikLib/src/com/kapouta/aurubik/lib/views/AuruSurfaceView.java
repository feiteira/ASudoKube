package com.kapouta.aurubik.lib.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import com.kapouta.aurubik.lib.rubikube.RubiKubeRenderer;

public class AuruSurfaceView extends GLSurfaceView {
	private RubiKubeRenderer render;
	
	public AuruSurfaceView(Context context) {
		super(context);

		// This below is required by the color picking
		setEGLConfigChooser(8, 8, 8, 8, 16, 4);
		getHolder().setFormat(PixelFormat.RGBA_8888);

	}

	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
		this.render = (RubiKubeRenderer) renderer;
		this.setOnTouchListener(this.render);
		
	}

}
