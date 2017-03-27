package com.mercdev.rybakin.contacts.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mercdev.rybakin.contacts.R;
import com.squareup.picasso.Picasso;

public class ContactView extends RelativeLayout {
	private final TextView contactNameView;
	private final ImageView contactPhotoView;

	public ContactView(Context context) {
		this(context, null);
	}

	public ContactView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(getContext(), R.layout.v_contact, this);
		contactNameView = (TextView) findViewById(R.id.contact_name);
		contactPhotoView = (ImageView) findViewById(R.id.contact_photo);
	}

	public void setContact(ContactModel contact) {
		contactNameView.setText(contact.getName());
		Picasso.with(contactPhotoView.getContext())
				.load(contact.getPhotoUri())
				.placeholder(R.drawable.contact_placeholder)
				.into(contactPhotoView);
	}
}
