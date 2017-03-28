package com.mercdev.rybakin.contacts.details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mercdev.rybakin.contacts.R;

public class PhoneNumberView extends RelativeLayout {
	private final TextView phoneTypeView;
	private final TextView phoneNumberView;

	public PhoneNumberView(Context context) {
		this(context, null);
	}

	public PhoneNumberView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PhoneNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}

	public PhoneNumberView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		inflate(context, R.layout.v_phone_number, this);

		phoneTypeView = (TextView) findViewById(R.id.phone_number_type);
		phoneNumberView = (TextView) findViewById(R.id.phone_number);
	}

	public void setData(String phoneType, final String phoneNumber) {
		phoneTypeView.setText(phoneType);
		phoneNumberView.setText(phoneNumber);
		setOnClickListener(view -> {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + phoneNumber));
			getContext().startActivity(intent);
		});
	}
}
