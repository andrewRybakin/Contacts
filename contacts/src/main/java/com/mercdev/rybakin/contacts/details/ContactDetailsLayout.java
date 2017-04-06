package com.mercdev.rybakin.contacts.details;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.List;

import com.mercdev.rybakin.contacts.R;
import com.mercdev.rybakin.contacts.utils.ContactUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ContactDetailsLayout extends CoordinatorLayout {
	private final Toolbar toolbar;
	private final CollapsingToolbarLayout collapsing;

	private final ImageView contactPhotoView;
	private final PhoneNumbersAdapter phoneNumbersAdapter;

	public ContactDetailsLayout(Context context) {
		this(context, null);
	}

	public ContactDetailsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactDetailsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(context, R.layout.v_contact_details, this);
		setFitsSystemWindows(true);

		toolbar = (Toolbar) findViewById(R.id.details_toolbar);
		collapsing = (CollapsingToolbarLayout) findViewById(R.id.details_collapsing);

		contactPhotoView = (ImageView) findViewById(R.id.details_contact_photo);

		phoneNumbersAdapter = new PhoneNumbersAdapter();
		((RecyclerView) findViewById(R.id.details_phone_numbers)).setAdapter(phoneNumbersAdapter);
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setAssociatedColor(@ColorInt int color) {
		collapsing.setContentScrimColor(color);
		collapsing.setStatusBarScrimColor(color);
		collapsing.setBackgroundColor(color);
		phoneNumbersAdapter.setAssociatedColor(color);
	}

	public void setContactName(String name) {
		toolbar.setTitle(name);
	}

	public void setContactPhoto(Uri photoUri, DetectAssociatedColorTransform.OnAssociatedColorDetected detectionEndListener) {
		Picasso.with(getContext())
				.load(photoUri)
				.transform(new DetectAssociatedColorTransform(detectionEndListener))
				.memoryPolicy(MemoryPolicy.NO_CACHE)
				.into(contactPhotoView);
	}

	@SuppressWarnings("deprecation")
	public void showPhotoPlaceholder() {
		contactPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.contact_placeholder));
	}

	public void setPhoneNumbers(List<ContactUtils.PhoneNumber> phoneNumbers) {
		phoneNumbersAdapter.setPhoneNumbers(phoneNumbers);
	}
}
