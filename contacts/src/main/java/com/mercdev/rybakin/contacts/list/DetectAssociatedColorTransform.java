package com.mercdev.rybakin.contacts.list;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Target;

import com.squareup.picasso.Transformation;

class DetectAssociatedColorTransform implements Transformation {
	private static final String KEY = "DetectAssociatedColor";

	private final ContactModel contact;

	DetectAssociatedColorTransform(ContactModel contact) {
		this.contact = contact;
	}

	@Override
	public Bitmap transform(Bitmap source) {
		if (contact.getPhotoUri() != Uri.EMPTY) {
			Palette.from(source)
					.addTarget(Target.MUTED)
					.addTarget(Target.VIBRANT)
					.generate(palette -> {
						Palette.Swatch muted = palette.getMutedSwatch();
						Palette.Swatch vibrant = palette.getVibrantSwatch();
						if (vibrant != null) {
							contact.setAssociatedColor(vibrant.getRgb());
						} else if (muted != null) {
							contact.setAssociatedColor(muted.getRgb());
						} else {
							contact.setAssociatedColor(Color.TRANSPARENT);
						}
					});
		}
		return source;
	}

	@Override
	public String key() {
		return KEY;
	}
}
