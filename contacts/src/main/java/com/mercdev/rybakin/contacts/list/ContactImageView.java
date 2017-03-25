package com.mercdev.rybakin.contacts.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.mercdev.rybakin.contacts.R;

public class ContactImageView extends AppCompatImageView {
	private boolean isRounded;

	Drawable roundedDrawable;

	public ContactImageView(Context context) {
		this(context, null);
	}

	public ContactImageView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ContactImageView, defStyleAttr, 0);
		isRounded = a.getBoolean(R.styleable.ContactImageView_rounded, false);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isRounded) {
			super.onDraw(canvas);
		} else {
			roundedDrawable.draw(canvas);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
}
