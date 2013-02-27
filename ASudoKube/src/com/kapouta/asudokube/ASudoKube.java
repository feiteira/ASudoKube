package com.kapouta.asudokube;

import com.kapouta.aurubik.lib.AurubikApp;
import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.SudoKubeModel;

public class ASudoKube extends AurubikApp {
	public static final String GA_UA_CODE = "UA-36839068-1";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public static ASudoKube getInstance() {
		return (ASudoKube) self;
	}

	@Override
	public String getAnalyticsUACode() {
		return GA_UA_CODE;
		// return this.getString(R.string.ga_api_key);
	}

	public String getScoreLoopSecret() {
	//TODO FIX!
		return "***REMOVED***";// <-------------------- REMOVED
		
	}

	public RubiKubeModel createModelCube() {
		return new SudoKubeModel();
	}

}
