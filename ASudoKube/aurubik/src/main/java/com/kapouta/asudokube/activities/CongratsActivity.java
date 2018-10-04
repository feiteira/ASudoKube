package com.kapouta.asudokube.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kapouta.asudokube.ASudoKube;
import com.kapouta.asudokube.R;
import com.kapouta.asudokube.rubikube.DifficultyLevel;
import com.kapouta.aurubik.lib.AurubikApp;
import com.kapouta.katools.KActivity;
import com.scoreloop.client.android.core.controller.TermsOfServiceController;
import com.scoreloop.client.android.core.controller.TermsOfServiceControllerObserver;

public class CongratsActivity extends KActivity {
	private static final int MIN_LEVEL_TO_SHOW_RATE = 5;

	public static final String RESULT_PROCEED = "RESULT_PROCEED";
	public static final String RESULT_MODE = "RESULT_MODE";
	private int level;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_congrats);

		Bundle extras = getIntent().getExtras();

		if (extras != null
				&& extras.getSerializable(SelectDificultyActivity.LEVEL_ID_KEY) != null) {
			int res = (Integer) extras
					.getSerializable(SelectDificultyActivity.RESULT_KEY);
			TextView textView = (TextView) findViewById(R.id.textCongratsMessage);
			TextView textDuration = (TextView) findViewById(R.id.textDuration);

			float duration = extras
					.getFloat(AurubikActivity.RESULT_CUBE_SOLVE_TIME);
			boolean is_new_best_time = extras
					.getBoolean(SelectDificultyActivity.NEW_BEST_TIME_KEY);
			level = extras.getInt(SelectDificultyActivity.LEVEL_ID_KEY);

			if (level < MIN_LEVEL_TO_SHOW_RATE) {
				Button bnt = (Button) findViewById(R.id.button_rate);
				bnt.setVisibility(View.GONE);
			}

			if (is_new_best_time)
				textDuration.setText("New record " + duration + " seconds.");
			else
				textDuration.setText("You took " + duration + " seconds.");

			switch (res) {
			case DifficultyLevel.RESULT_ONE_GAME_FINISHED:
				textView.setText("Congratulations!");
				break;
			case DifficultyLevel.RESULT_LEVEL_FINISHED:
				textView.setText("Congratulations!");
				break;

			default:
				break;
			}
//			notifyTracker(level, (int) (duration * 10));
		}
	}

//	public void notifyTracker(int level, int time) {
//		// Category, Action, Label, Value
//		this.getTracker().trackEvent("Game", "Finished_" + level, "duration",
//				time);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void onContinueToLevelClick(View view) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RESULT_PROCEED, true);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	public void onRepeatLevelClick(View view) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RESULT_PROCEED, false);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	public void onViewScoreListClick(View view) {
		TermsOfServiceControllerObserver observer = new TermsOfServiceControllerObserver() {
			@Override
			public void termsOfServiceControllerDidFinish(
					TermsOfServiceController controller, Boolean accepted) {
//				if (ASudoKube.getInstance().acceptedTOSStatus()) {
//					Intent intent = new Intent(CongratsActivity.this,
//							LeaderboardActivity.class);
//					intent.putExtra(RESULT_MODE, DifficultyLevel.getMode(level));
//					CongratsActivity.this.startActivity(intent);
//				}
			}
		};

		TermsOfServiceController controller = new TermsOfServiceController(
				observer);
		controller.query(this);
	}

	public void onRatingClick(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(AurubikApp.APP_MARKET_URL));
		startActivity(intent);
	}

}
