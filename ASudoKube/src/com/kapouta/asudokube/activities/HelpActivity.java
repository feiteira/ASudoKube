package com.kapouta.asudokube.activities;

import android.os.Bundle;
import android.view.Menu;

import com.kapouta.asudokube.R;
import com.kapouta.katools.KActivity;

public class HelpActivity extends KActivity  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_help, menu);
		return true;
	}


}
