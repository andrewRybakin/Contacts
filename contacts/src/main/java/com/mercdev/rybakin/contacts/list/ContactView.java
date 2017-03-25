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
	private final TextView contactName;
	private final ImageView contactImage;

	public ContactView(Context context) {
		this(context, null);
	}

	public ContactView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(getContext(), R.layout.v_contact, this);
		contactName = (TextView) findViewById(R.id.contact_name);
		contactImage = (ImageView) findViewById(R.id.contact_photo);
	}

	public void setContact(ContactModel contact) {
		contactName.setText(contact.getName());
		Picasso.with(contactImage.getContext())
				.load(contact.getPhotoUri())
				.placeholder(R.drawable.contact_placeholder)
				.into(contactImage);
	}
}
