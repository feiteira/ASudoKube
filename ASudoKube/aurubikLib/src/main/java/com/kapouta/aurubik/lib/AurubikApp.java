package com.kapouta.aurubik.lib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import android.content.Context;

import com.kapouta.aurubik.lib.rubikube.CubeListener;
import com.kapouta.aurubik.lib.rubikube.RubiKubeModel;
import com.kapouta.aurubik.lib.rubikube.picker.RubiKubeCoordPicker;
import com.kapouta.katools.KApplication;

public abstract class AurubikApp extends KApplication implements CubeListener {

	public static final String APP_MARKET_URL = "market://details?id=com.kapouta.aurubik";
	public static final String SUDOKUBE_APP_MARKET_URL = "market://details?id=com.kapouta.asudokube";


	public static final String AurubikInfoFileName = "aurubik.inf";

	private RubiKubeModel modelcube;
	private RubiKubeCoordPicker pickercube;

	private HashMap<String, Object> info = null;

	public abstract String getScoreLoopSecret();
	public abstract RubiKubeModel createModelCube();
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		modelcube = createModelCube();
		pickercube = new RubiKubeCoordPicker();
		String secret = this.getScoreLoopSecret();
		modelcube.addCubeListener(this);
		loadFromFile();
	}

	public RubiKubeModel getModelCube() {
		return modelcube;
	}

	public RubiKubeCoordPicker getPickerCube() {
		return pickercube;
	}

//	public void startTracker(Context context) {
//		tracker = GoogleAnalyticsTracker.getInstance();
//		tracker.startNewSession("UA-34622256-1", context);
//	}
//
//	public void visit(String activityName) {
//		tracker.trackPageView("/" + activityName);
//	}
//
//	public void disptach() {
//		tracker.dispatch();
//	}

//	public boolean acceptedTOSStatus() {
//		// check status of Scoreloop TOS
//		TermsOfService.Status termsStatus = Session.getCurrentSession()
//				.getUsersTermsOfService().getStatus();
//		if (termsStatus == Status.ACCEPTED) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	public void cubeStateChanged(int what) {
	}

	public boolean doneAtLeastOnce(String key) {
		if (this.info.get(key) == null) {
			this.info.put(key, new Integer(0));
			save();
			return false;
		}
		return true;
	}

	public boolean neverDone(String key) {
		return !doneAtLeastOnce(key);
	}

	@SuppressWarnings("unchecked")
	public void loadFromFile() {

		try {
			FileInputStream fis = this.openFileInput(AurubikInfoFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.info = ((HashMap<String, Object>) ois.readObject());
		} catch (FileNotFoundException e) {
			this.info = new HashMap<String, Object>();
			save();
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			this.info = new HashMap<String, Object>();
			save();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			save();
		}

	}

	public void save() {

		try {
			FileOutputStream fos = this.openFileOutput(AurubikInfoFileName,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(this.info);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
