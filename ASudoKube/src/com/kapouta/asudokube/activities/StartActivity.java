package com.kapouta.asudokube.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.kapouta.asudokube.ASudoKube;
import com.kapouta.asudokube.R;
import com.kapouta.asudokube.rubikube.DifficultyLevel;
import com.kapouta.aurubik.lib.AurubikApp;
import com.kapouta.katools.KActivity;
import com.scoreloop.client.android.core.controller.TermsOfServiceController;
import com.scoreloop.client.android.core.controller.TermsOfServiceControllerObserver;

public class StartActivity extends KActivity {

	public static final String KEY_SHOW_SCORELOOP_TOS_ON_START = "KEY_SHOW_SCORELOOP_TOS_ON_START";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

	}

	@Override
	public void onResume() {
		super.onResume();
		if (((AurubikApp) this.getApplication())
				.neverDone(KEY_SHOW_SCORELOOP_TOS_ON_START)) {
			TermsOfServiceControllerObserver observer = new TermsOfServiceControllerObserver() {
				@Override
				public void termsOfServiceControllerDidFinish(
						TermsOfServiceController controller, Boolean accepted) {
				}
			};

			TermsOfServiceController controller = new TermsOfServiceController(
					observer);
			controller.query(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	public void onFreePlayClick(View view) {
		Intent myIntent = new Intent(StartActivity.this, AurubikActivity.class);
		StartActivity.this.startActivity(myIntent);
	}

	public void onLearnModeClick(View view) {
		Intent myIntent = new Intent(StartActivity.this,
				SelectDificultyActivity.class);
		StartActivity.this.startActivity(myIntent);
	}

	public void onHelpClick(View view) {
		Intent myIntent = new Intent(StartActivity.this, HelpActivity.class);
		StartActivity.this.startActivity(myIntent);
	}

	public void onViewScoreListClick(View view) {
		TermsOfServiceControllerObserver observer = new TermsOfServiceControllerObserver() {
			@Override
			public void termsOfServiceControllerDidFinish(
					TermsOfServiceController controller, Boolean accepted) {
				if (ASudoKube.getInstance().acceptedTOSStatus()) {
					Intent intent = new Intent(StartActivity.this,
							LeaderboardActivity.class);
					intent.putExtra(CongratsActivity.RESULT_MODE, DifficultyLevel.getMode(0));
					StartActivity.this.startActivity(intent);
				}
			}
		};

		TermsOfServiceController controller = new TermsOfServiceController(
				observer);
		controller.query(this);
	}
	
	public void onAurubikClick(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(AurubikApp.APP_MARKET_URL));
		startActivity(intent);
	}
	
}
