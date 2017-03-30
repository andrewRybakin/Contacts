package com.mercdev.rybakin.contacts.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.mercdev.rybakin.contacts.R;

public class ContactPhotoThumbnailView extends AppCompatImageView {
	@ColorInt
	private int associatedColor = Color.TRANSPARENT;

	public ContactPhotoThumbnailView(Context context) {
		this(context, null);
	}

	public ContactPhotoThumbnailView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactPhotoThumbnailView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setAssociatedColor(@ColorInt int color) {
		this.associatedColor = color;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() != null) {
			if (getDrawable() instanceof BitmapDrawable) {
				RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), ((BitmapDrawable) getDrawable()).getBitmap());
				roundedDrawable.setBounds(0, 0, getWidth(), getHeight());
				roundedDrawable.setCircular(true);
				roundedDrawable.draw(canvas);
				return;
			} else if (getDrawable() instanceof LayerDrawable) {
				LayerDrawable drawable = (LayerDrawable) getDrawable();
				Drawable layer = drawable.findDrawableByLayerId(R.id.contact_placeholder_background);
				layer.setTint(associatedColor);
				drawable.draw(canvas);
				return;
			}
		}
		super.onDraw(canvas);
	}
}
