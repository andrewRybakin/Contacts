package com.mercdev.rybakin.contacts.details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import com.mercdev.rybakin.contacts.R;

public class PhoneNumberView extends FrameLayout {
	private final TextView phoneTypeView;
	private final TextView phoneNumberView;
	private final ImageView phoneMessageView;

	public PhoneNumberView(Context context) {
		this(context, null);
	}

	public PhoneNumberView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PhoneNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(context, R.layout.v_phone_number, this);

		phoneTypeView = (TextView) findViewById(R.id.phone_number_type);
		phoneNumberView = (TextView) findViewById(R.id.phone_number);
		phoneMessageView = (ImageView) findViewById(R.id.phone_message);
	}

	public void setData(String phoneType, final String phoneNumber) {
		phoneTypeView.setText(phoneType);
		phoneNumberView.setText(PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getISO3Country()));
		setOnClickListener(view -> {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + phoneNumber));
			getContext().startActivity(intent);
		});
		phoneMessageView.setOnClickListener(v -> {
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("vnd.android-dir/mms-sms");
			intent.setData(Uri.parse("sms:" + phoneNumber));
			getContext().startActivity(intent);
		});
	}
}
