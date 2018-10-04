package com.kapouta.aurubik.lib;

import android.app.Application;

import com.kapouta.aurubik.lib.rubikube.CubeListener;
import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.picker.RubiKubeCoordPicker;

public class AurubikSimpleApp extends Application implements CubeListener {
	private RubiKubeModel modelcube;
	private RubiKubeCoordPicker pickercube;
	
	@Override
	public void onCreate() {
		super.onCreate();
		modelcube = new RubiKubeModel();
		pickercube = new RubiKubeCoordPicker();
		
		modelcube.addCubeListener(this);
		
	}
	
	public RubiKubeModel getModelCube() {
		return modelcube;
	}

	public RubiKubeCoordPicker getPickerCube() {
		return pickercube;
	}
	
	public void cubeStateChanged(int what) {
		// TODO Auto-generated method stub

	}

}
