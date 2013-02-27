package com.kapouta.asudokube.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.kapouta.asudokube.R;
import com.kapouta.asudokube.ScoreLoopSudokube;
import com.kapouta.asudokube.rubikube.DifficultyLevel;
import com.kapouta.asudokube.rubikube.DifficultyLevelAdapter;
import com.kapouta.katools.KActivity;

public class SelectDificultyActivity extends KActivity {

	public static final int RESULT_WHEN_CUBE_FINISHED = 0;
	public static final int RESULT_PROCEED_TO_NEXT_LEVEL = 1;

	public static final String RESULT_KEY = "result";
	public static final String NEW_BEST_TIME_KEY = "new best time";
	public static final String LEVEL_ID_KEY = "ScrambleN";

	private DifficultyLevel current_level;

	private DifficultyLevelAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_dificulty);

		adapter = new DifficultyLevelAdapter(this);
		ListView listView = (ListView) findViewById(R.id.listSelectDifficulty);
		listView.setAdapter(adapter);
		
		

	}

	public void onDifficultyClick(View view) {
		current_level = (DifficultyLevel) view.getTag();

		if (current_level.isLocked())
			return;

		Intent myIntent = new Intent(SelectDificultyActivity.this,
				AurubikActivity.class);
		myIntent.putExtra(LEVEL_ID_KEY, current_level.getLevel());
		SelectDificultyActivity.this.startActivityForResult(myIntent,
				RESULT_WHEN_CUBE_FINISHED);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		// congratulations!
		case (RESULT_WHEN_CUBE_FINISHED): {
			// show congratulations and wait for activity to return
			// RESULT_PROCEED_TO_NEXT_LEVEL

			if (resultCode == Activity.RESULT_OK) {

				// this is necessary, because resume() is called immediately
				// after this function
				// but we don't even get to see this activity
				skipTrackingThisOnce();

				boolean cubefinished = data.getBooleanExtra(
						AurubikActivity.RESULT_CUBE_FINISHED, false);
				if (cubefinished) {
					float duration = data.getFloatExtra(
							AurubikActivity.RESULT_CUBE_SOLVE_TIME, 99999f);

					// DURATION
					ScoreLoopSudokube.submitScore(this, duration,
							this.current_level.getLevel());

					boolean new_best_time = this.current_level
							.updateBestTime(duration);

					Intent myIntent = new Intent(SelectDificultyActivity.this,
							CongratsActivity.class);
					int res = this.current_level.finishAGame();

					myIntent.putExtra(RESULT_KEY, res);
					myIntent.putExtra(NEW_BEST_TIME_KEY, new_best_time);
					myIntent.putExtra(LEVEL_ID_KEY, current_level.getLevel());

					myIntent.putExtra(AurubikActivity.RESULT_CUBE_SOLVE_TIME,
							duration);
					adapter.notifyDataSetChanged();

					SelectDificultyActivity.this.startActivityForResult(
							myIntent, RESULT_PROCEED_TO_NEXT_LEVEL);
				}
			}
			break;
		}
		case (RESULT_PROCEED_TO_NEXT_LEVEL):

			// loads the new level activity
			if (resultCode == Activity.RESULT_OK) {
				// this is necessary, because resume() is called immediately
				// after this function
				// but we don't even get to see this activity
				skipTrackingThisOnce();
				boolean proceed = data.getBooleanExtra(
						CongratsActivity.RESULT_PROCEED, false);

				this.current_level.finishAGame();
				adapter.notifyDataSetChanged();
				adapter.updateValues();
				if (proceed) {
					this.current_level = adapter.getLevel(this.current_level
							.getLevel());
				}
				Intent myIntent = new Intent(SelectDificultyActivity.this,
						AurubikActivity.class);
				myIntent.putExtra(LEVEL_ID_KEY, current_level.getLevel());

				SelectDificultyActivity.this.startActivityForResult(myIntent,
						RESULT_WHEN_CUBE_FINISHED);

			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_select_dificulty, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_reset_data:
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						adapter.reset();
						adapter.notifyDataSetChanged();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
