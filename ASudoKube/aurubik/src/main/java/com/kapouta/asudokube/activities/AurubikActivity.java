package com.kapouta.asudokube.activities;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kapouta.asudokube.R;
import com.kapouta.asudokube.rubikube.DifficultyLevel;
import com.kapouta.aurubik.lib.matrixtracking.MatrixTrackingGL;
import com.kapouta.aurubik.lib.rubikube.CubeListener;
import com.kapouta.aurubik.lib.rubikube.RubiKubeRenderer;
import com.kapouta.aurubik.lib.views.AuruSurfaceView;

public class AurubikActivity extends com.kapouta.katools.KActivity implements
		CubeListener {

	public static final String RESULT_CUBE_FINISHED = "Cube Finished!";
	public static final String RESULT_CUBE_SOLVE_TIME = "RESULT_CUBE_SOLVE_TIME";

	private AuruSurfaceView glView; // Use GLSurfaceView
	private RubiKubeRenderer render;

	private int scambleN;

	private TextView txtCubeRunTime;

	private long startTime;
	private long pauseTime;

	private Timer gameTimer;
	private boolean runningInLevelMode;

	public static final int DEFAULT_SCRAMBLE_N = 30;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.runningInLevelMode = false;

		setContentView(R.layout.main);

		// add the GLSurfaceView to the frame
		FrameLayout glFrameLayout = (FrameLayout) findViewById(R.id.glframelayout);
		glView = new AuruSurfaceView(this); // Allocate a GLSurfaceView

		render = new RubiKubeRenderer(this);
		glView.setRenderer(render); // Use a custom renderer

		glView.setGLWrapper(new GLSurfaceView.GLWrapper() {
			@Override
			public GL wrap(GL gl) {
				return new MatrixTrackingGL(gl);
			}
		});

		glFrameLayout.addView(glView);

		Bundle extras = getIntent().getExtras();
		scambleN = DEFAULT_SCRAMBLE_N;

		// it's a level, not free play mode
		if (extras != null
				&& extras.getSerializable(SelectDificultyActivity.LEVEL_ID_KEY) != null) {
			this.runningInLevelMode = true;

			int lev = (Integer) extras
					.getSerializable(SelectDificultyActivity.LEVEL_ID_KEY);
			this.scambleN = lev;
			startLevel(lev);

			render.getCube().addCubeListener(this);

			txtCubeRunTime = (TextView) findViewById(R.id.textCubeRunTime);
			this.startTime = 0;
			this.pauseTime = 0;

			// startGameTimer(); -- automatically done by onResume()

		} else {
			hideLevelLabel();
			hideTimeLabel();
		}

		return;
	}

	private void startGameTimer() {
		long t = System.currentTimeMillis();

		this.startTime = t - (this.pauseTime - this.startTime);

		gameTimer = new Timer();

		gameTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

				try {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							txtCubeRunTime.setText(getDurationTime() + "");
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 20, 100);
	}

	public float getDurationTime() {
		long t = System.currentTimeMillis() - startTime;
		final float tf = FloatMath.floor(t / 10) / 100.0f;

		return tf;
	}

	public void startLevel(int l) {
		// scambleN = DifficultyLevel.getScrambleFromLevel(l);
		render.getCube().setRandomizerSeed(l);

		hideResetButton();
		hideScrambleButton();
		updateLevelLabel(l);
		scramble();
		if (this.render.getCube().isFinished())
			scramble();

		this.render.getCube().clearUndoLog();
	}

	@Override
	public void onStop() {
		super.onStop();
		return;
	}

	@Override
	public void finish() {
		super.finish();
		return;
	}

	// Call back when the activity is going into the background
	@Override
	protected void onPause() {
		super.onPause();

		if (this.runningInLevelMode) {
			gameTimer.cancel();
			this.pauseTime = System.currentTimeMillis();
		}

		if (glView != null) {
			glView.onPause();
		}
	}

	// Call back after onPause()
	@Override
	public void onResume() {
		super.onResume();

		if (this.runningInLevelMode) {
			startGameTimer();
		}

		if (glView != null) {
			glView.onResume();
		}
	}

	public void onResetClick(View view) {
		this.render.getCube().reset();
	}

	public void hideResetButton() {
		Button btnReset = (Button) findViewById(R.id.btnReset);
		btnReset.setVisibility(View.GONE);
	}

	public void updateLevelLabel(int l) {
		TextView txt = (TextView) findViewById(R.id.textCurrentLevel);
		txt.setText(" " + l);
	}

	public void onScrambleClick(View view) {
		scramble();
	}

	public void scramble() {
		this.render.getCube().scrambleFast(scambleN);
	}

	public void hideScrambleButton() {
		Button btnReset = (Button) findViewById(R.id.btnScramble);
		btnReset.setVisibility(View.GONE);
	}

	public void hideLevelLabel() {
		TextView txt = (TextView) findViewById(R.id.textCurrentLevel);
		txt.setVisibility(View.GONE);
	}

	public void hideTimeLabel() {
		TextView txt = (TextView) findViewById(R.id.textCubeRunTime);
		txt.setVisibility(View.GONE);
	}

	public void onUndoClick(View view) {
		this.render.getCube().undo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.aurubikactivity, menu);
		// return true;
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode != 0) {
			return;
		}
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if ((extras != null) && extras.containsKey("something")) {
				// do the colouring of the cube here, depending on the results?
			}
		}
		return;
	}

	@Override
	public void cubeStateChanged(int what) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RESULT_CUBE_FINISHED, true);
		resultIntent.putExtra(RESULT_CUBE_SOLVE_TIME, getDurationTime());
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}