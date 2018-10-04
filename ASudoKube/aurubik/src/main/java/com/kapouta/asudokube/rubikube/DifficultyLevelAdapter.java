package com.kapouta.asudokube.rubikube;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kapouta.asudokube.R;

public class DifficultyLevelAdapter extends BaseAdapter {

	ArrayList<DifficultyLevel> listdata = new ArrayList<DifficultyLevel>();
	private Context context;

	public static final String GAME_STATS_FILENAME = "aurubik_stats.db";

	public DifficultyLevelAdapter(Context context) {
		this.context = context;
		// reset();
		// save();
		loadFromFile();
		// reset();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		save();
	}

	public DifficultyLevel getLevel(int lvl) {
		// if you ask for very high level, then simple set this level to the
		// highest.
		if (lvl == listdata.size())
			return listdata.get(lvl - 1);
		return listdata.get(lvl);
	}

	@SuppressWarnings("unchecked")
	public void loadFromFile() {
		Activity activity = (Activity) context;

		try {
			FileInputStream fis = activity.openFileInput(GAME_STATS_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			listdata = ((ArrayList<DifficultyLevel>) ois.readObject());
		} catch (FileNotFoundException e) {
			reset();
			save();
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			reset();
			save();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			reset();
			save();
		}

	}

	public void save() {
		Activity activity = (Activity) context;

		try {
			FileOutputStream fos = activity.openFileOutput(GAME_STATS_FILENAME,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(listdata);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void reset() {
		listdata.clear();
		for (int cnt = 0; cnt < DifficultyLevel.N_LEVELS; cnt++) {
			listdata.add(new DifficultyLevel(cnt + 1, 0));
		}
		listdata.get(0).unlock();
	}

	public void updateValues() {
		for (int cnt = 0; cnt < DifficultyLevel.N_LEVELS - 1; cnt++) {
			if (listdata.get(cnt).levelFinished()) {
				listdata.get(cnt + 1).unlock();
			}
		}

	}

	@Override
	public int getCount() {
		return DifficultyLevel.N_LEVELS;
	}

	@Override
	public Object getItem(int arg0) {
		return listdata.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		updateValues();
		DifficultyLevel leveldata = listdata.get(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.dificulty_button_view, parent,
				false);

		TextView textView = (TextView) rowView
				.findViewById(R.id.btnDifficulty_text);
		TextView textViewNote = (TextView) rowView
				.findViewById(R.id.btnDifficulty_second_line);
		TextView textViewTime = (TextView) rowView
				.findViewById(R.id.btnDifficulty_showBestTime);
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.btnDifficulty_Icon);

		textView.setText("Level " + (leveldata.getLevel()));
		imageView.setImageResource(R.drawable.ic_unlocked);
		if (leveldata.levelMastered()) {
			textViewNote.setText("Level mastered.");
		} else if (leveldata.levelFinished()) {

			textViewNote.setText("Level unlocked.");
		} else {
			textViewNote.setText("Finish to unlock next level.");
		}

		if (leveldata.isLocked()) {
			textViewNote.setText("Level locked.");
			imageView.setImageResource(R.drawable.ic_locked);
		}

		// update best time
		if (leveldata.getBest_time() <= 0) {
			textViewTime.setText("");
		} else {
			textViewTime.setText(leveldata.getBest_time() + " s ");
		}

		rowView.setTag(leveldata);

		return rowView;
	}
}
