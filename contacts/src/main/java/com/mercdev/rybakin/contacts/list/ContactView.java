package com.mercdev.rybakin.contacts.list;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mercdev.rybakin.contacts.R;
import com.squareup.picasso.Picasso;

public class ContactView extends RelativeLayout {
	private final TextView contactNameView;
	private final ContactPhotoThumbnailView contactPhotoView;

	public ContactView(Context context) {
		this(context, null);
	}

	public ContactView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, R.style.Contacts_ContactView);
	}

	public ContactView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		inflate(getContext(), R.layout.v_contact, this);
		contactNameView = (TextView) findViewById(R.id.contact_name);
		contactPhotoView = (ContactPhotoThumbnailView) findViewById(R.id.contact_photo);
	}

	public void setName(String name) {
		contactNameView.setText(name);
	}

	public void setPhoto(Uri photoUri) {
		Picasso.with(contactPhotoView.getContext())
				.load(photoUri)
				.placeholder(R.drawable.contact_thumbnail_placeholder)
				.into(contactPhotoView);
	}

	public void setAssociatedColor(int color) {
		contactPhotoView.setAssociatedColor(color);
	}
}
