package com.kapouta.aurubik.lib.rubikube.tools;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class NumberSquaredBitmap extends SquaredBitmap {
	public static final int TEXT_SIZE = 48;
	public static final float NUMBER_BACKGROUND_COLOR[] = { 1.0f, 1.0f, 1.0f };
	public static final int NUMBER_FOREGROUND_COLOR[] = { 0x0, 0x0, 0x0 };

	Paint paint;

	public NumberSquaredBitmap() {
		super(1, 1, 0);

		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
		paint.setTextSize(TEXT_SIZE);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setTypeface(Typeface.MONOSPACE);

		Canvas canvas = new Canvas(bitmap);
		String txt = "X";

		int centerX = (int) ((int) (canvas.getWidth() / 2) - paint
				.measureText(txt) / 2);
		int centerY = (int) (canvas.getHeight() / 2 - (paint.descent() + paint
				.ascent()) / 2);

		canvas.drawText(txt, centerX, centerY, paint);
		bitmap.prepareToDraw();

	}

	public NumberSquaredBitmap(float r, float g, float b, int value) {
		super(r, g, b);

		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(TEXT_SIZE);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setTypeface(Typeface.MONOSPACE);

		Canvas canvas = new Canvas(bitmap);
		String txt = "" + value;

		int centerX = (int) ((int) (canvas.getWidth() / 2) - paint
				.measureText(txt) / 2);
		int centerY = (int) (canvas.getHeight() / 2 - (paint.descent() + paint
				.ascent()) / 2);

		canvas.drawText(txt, centerX, centerY, paint);
		bitmap.prepareToDraw();
	}

	public NumberSquaredBitmap(int value) {
		super(NUMBER_BACKGROUND_COLOR[0], NUMBER_BACKGROUND_COLOR[1],
				NUMBER_BACKGROUND_COLOR[2]);

		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(NUMBER_FOREGROUND_COLOR[0],
				NUMBER_FOREGROUND_COLOR[1], NUMBER_FOREGROUND_COLOR[2]));
		paint.setTextSize(TEXT_SIZE);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setTypeface(Typeface.MONOSPACE);
		if(value == 9 || value == 6){
			paint.setUnderlineText(true);
		}

		Canvas canvas = new Canvas(bitmap);
		String txt = "" + value;

		int centerX = (int) ((int) (canvas.getWidth() / 2) - paint
				.measureText(txt) / 2);
		int centerY = (int) (canvas.getHeight() / 2 - (paint.descent() + paint
				.ascent()) / 2);

		canvas.drawText(txt, centerX, centerY, paint);
		bitmap.prepareToDraw();
	}

}
