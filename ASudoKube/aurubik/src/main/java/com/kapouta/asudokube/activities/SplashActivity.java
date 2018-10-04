package com.kapouta.asudokube.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.kapouta.asudokube.R;

public class SplashActivity extends Activity {

	private Thread splashTread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// final SplashActivity sPlashScreen = this;

		// thread for displaying the SplashScreen
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {

						// System.out.println("ACT: starting instance");
						// AurubikApp.getInstance();
					}

				} finally {
					finish();
					Intent myIntent = new Intent(SplashActivity.this, StartActivity.class);
					SplashActivity.this.startActivityForResult(myIntent, 0);
				}
			}
		};

		splashTread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_splash, menu);
		return false;
	}

}
