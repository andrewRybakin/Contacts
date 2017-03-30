package com.mercdev.rybakin.contacts.details;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Target;

import com.squareup.picasso.Transformation;

class DetectAssociatedColorTransform implements Transformation {
	private static final String KEY = "DetectAssociatedColor";

	private final OnAssociatedColorDetected detectedListener;

	DetectAssociatedColorTransform(OnAssociatedColorDetected detectedListener) {
		this.detectedListener = detectedListener;
	}

	@Override
	public Bitmap transform(Bitmap source) {
		//noinspection Convert2Lambda
		Palette.from(source)
				.addTarget(Target.MUTED)
				.addTarget(Target.VIBRANT)
				.generate(new Palette.PaletteAsyncListener() {
					@Override
					public void onGenerated(Palette palette) {
						Palette.Swatch muted = palette.getMutedSwatch();
						Palette.Swatch vibrant = palette.getVibrantSwatch();
						if (vibrant != null) {
							detectedListener.onAssociatedColorDetected(vibrant.getRgb());
						} else if (muted != null) {
							detectedListener.onAssociatedColorDetected(muted.getRgb());
						} else {
							detectedListener.onAssociatedColorDetected(Color.TRANSPARENT);
						}
					}
				});
		return source;
	}

	@Override
	public String key() {
		return KEY;
	}

	static abstract class OnAssociatedColorDetected {
		abstract void onAssociatedColorDetected(int color);
	}
}
