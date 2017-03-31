package com.mercdev.rybakin.contacts.details;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercdev.rybakin.contacts.R;

public class PhoneNumberView extends FrameLayout {
	private static final int RIPPLE_ALPHA = 0x88;
	private final TextView phoneTypeView;
	private final TextView phoneNumberView;
	private final ImageView phoneMessageView;
	private final ImageView phoneView;
	private final CardView cardView;

	public PhoneNumberView(Context context) {
		this(context, null);
	}

	public PhoneNumberView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressWarnings("deprecation")
	public PhoneNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(context, R.layout.v_phone_number, this);

		cardView = (CardView) findViewById(R.id.phone_card);

		phoneTypeView = (TextView) findViewById(R.id.phone_number_type);
		phoneNumberView = (TextView) findViewById(R.id.phone_number);
		phoneMessageView = (ImageView) findViewById(R.id.phone_message);
		phoneView = (ImageView) findViewById(R.id.phone);
	}

	public void setData(String phoneType, final String phoneNumber) {
		phoneTypeView.setText(phoneType);
		phoneNumberView.setText(phoneNumber);
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

	public void setAssociatedColor(@ColorInt int color) {
		phoneMessageView.setImageTintList(ColorStateList.valueOf(color));
		phoneView.setImageTintList(ColorStateList.valueOf(color));
		cardView.setForeground(new RippleDrawable(ColorStateList.valueOf(ColorUtils.setAlphaComponent(color, RIPPLE_ALPHA)), getForeground(), null));
		phoneMessageView.setBackground(new RippleDrawable(ColorStateList.valueOf(ColorUtils.setAlphaComponent(color, RIPPLE_ALPHA)), getForeground(), null));
	}
}
