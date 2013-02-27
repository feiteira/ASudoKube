package com.kapouta.asudokube;

import android.app.Activity;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.ScoreController;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.ScoreSubmitException;

public class ScoreLoopSudokube {

	public static void submitScore(final Activity activity, float time, int level) {
		Score score = new Score((double) time, null);
		score.setMode(level);

		// show a progress dialog while we are submitting
		// activity.showDialog(DIALOG_PROGRESS);

		// ordinary game - no challenge
		// set up an observer for our request
		RequestControllerObserver scoreControllerObserver = new RequestControllerObserver() {
			// private String dialogSuccessMessage;

			@Override
			public void requestControllerDidFail(RequestController controller, Exception exception) {
				// something went wrong... possibly no internet connection
				// activity.dismissDialog(DIALOG_PROGRESS);

				if (exception instanceof ScoreSubmitException) {
					// score could not be submitted but was stored locally
					// instead
					// show the success dialog
					// dialogSuccessMessage = activity
					// .getString(R.string.score_stored_locally);
					// activity.showDialog(DIALOG_SUCCESS);
				} else {
					// activity.showDialog(DIALOG_FAILED);
				}
			}

			// this method is called when the request succeeds
			@Override
			public void requestControllerDidReceiveResponse(RequestController controller) {

				// remove the progress dialog
				// activity.dismissDialog(DIALOG_PROGRESS);

				// show the success dialog
				// dialogSuccessMessage =
				// activity.getString(R.string.score_submitted);

				// activity.showDialog(DIALOG_SUCCESS);
				// this Dialog will finish this activity and
				// return to the main activity
			}
		};

		if (ASudoKube.getInstance().acceptedTOSStatus()) {
			ScoreController scoreController = new ScoreController(scoreControllerObserver);
			scoreController.submitScore(score);
		}
	}
}
