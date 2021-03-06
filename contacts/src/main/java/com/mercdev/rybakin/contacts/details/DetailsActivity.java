package com.mercdev.rybakin.contacts.details;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import com.mercdev.rybakin.contacts.BaseActivity;
import com.mercdev.rybakin.contacts.R;
import com.mercdev.rybakin.contacts.utils.ContactUtils;

import static android.view.View.GONE;
import static com.mercdev.rybakin.contacts.utils.ContactUtils.CONTACT_DETAILS_PROJECTION;
import static com.mercdev.rybakin.contacts.utils.ContactUtils.PHONE_NUMBERS_PROJECTION;

public class DetailsActivity extends BaseActivity {
	private static final String CONTACT_ID_EXTRA = "contactId";
	private static final String CONTACT_ASSOCIATED_COLOR_EXTRA = "associatedExtra";
	private static final long CONTACT_NO_ID = -1;

	private final ContentObserver contactObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			updateContact();
		}
	};

	private Cursor contactCursor;
	private Cursor phoneNumbersCursor;

	private ContactDetailsLayout layout;
	private ProgressBar progressView;

	private long contactId;
	private long animationDuration;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_details);
		getWindow().setStatusBarColor(Color.TRANSPARENT);

		progressView = (ProgressBar) findViewById(R.id.progress_placeholder);

		layout = (ContactDetailsLayout) findViewById(R.id.details_layout);
		setSupportActionBar(layout.getToolbar());
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			layout.getToolbar().setNavigationOnClickListener(view -> finish());
		}

		animationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isPermissionsGranted()) {
			contactId = getIntent().getLongExtra(CONTACT_ID_EXTRA, CONTACT_NO_ID);
			if (contactId != CONTACT_NO_ID) {
				loadContactDetails();
			} else {
				Snackbar.make(layout, R.string.no_contact, Snackbar.LENGTH_INDEFINITE).show();
			}
		} else {
			Snackbar.make(layout, R.string.no_permissions, Snackbar.LENGTH_INDEFINITE)
					.setAction(R.string.no_permission_settings, view -> openPermissionsSettings()).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeCursors();
	}

	private void closeCursors() {
		if (contactCursor != null && !contactCursor.isClosed()) {
			contactCursor.unregisterContentObserver(contactObserver);
			contactCursor.close();
		}
		if (phoneNumbersCursor != null && !phoneNumbersCursor.isClosed()) {
			phoneNumbersCursor.unregisterContentObserver(contactObserver);
			phoneNumbersCursor.close();
		}
	}

	private void setAssociatedColor(@ColorInt int color) {
		getWindow().setStatusBarColor(color);
		layout.setAssociatedColor(color);
	}

	private void loadContactDetails() {
		hideLayout();
		showProgress();
		String selection = ContactsContract.Contacts._ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(contactId) };
		ContentResolver contentResolver = getContentResolver();
		closeCursors();
		contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, CONTACT_DETAILS_PROJECTION, selection, selectionArgs, null);
		if (contactCursor != null && contactCursor.moveToFirst()) {
			contactCursor.registerContentObserver(contactObserver);
			selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
			selectionArgs = new String[] { String.valueOf(contactId) };
			phoneNumbersCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONE_NUMBERS_PROJECTION, selection, selectionArgs, null);
			if (phoneNumbersCursor != null) {
				phoneNumbersCursor.registerContentObserver(contactObserver);
			}
		}
		updateContact();
	}

	@SuppressWarnings("deprecation")
	private void updateContact() {
		String name = ContactUtils.getName(contactCursor);
		Uri photoUri = ContactUtils.getPhotoUri(contactCursor);
		List<ContactUtils.PhoneNumber> phoneNumbers = ContactUtils.getPhoneNumbers(phoneNumbersCursor);
		layout.setContactName(name);
		layout.setPhoneNumbers(phoneNumbers);
		if (photoUri != Uri.EMPTY) {
			layout.setContactPhoto(photoUri, new DetectAssociatedColorTransform.OnAssociatedColorDetected() {
				@Override
				void onAssociatedColorDetected(int color) {
					setAssociatedColor(color);
					hideProgress();
					showLayout();
				}
			});
		} else {
			int associatedColor = getIntent().getIntExtra(CONTACT_ASSOCIATED_COLOR_EXTRA, getResources().getColor(R.color.colorPrimary));
			setAssociatedColor(associatedColor);
			layout.showPhotoPlaceholder();
			hideProgress();
			showLayout();
		}
	}

	private void showLayout() {
		layout.animate().cancel();
		layout.setAlpha(0);
		layout.setVisibility(View.VISIBLE);
		layout.animate()
				.alpha(1)
				.setDuration(animationDuration)
				.start();
	}

	private void hideLayout() {
		layout.animate().cancel();
		layout.animate()
				.alpha(0)
				.setDuration(animationDuration)
				.withEndAction(() -> layout.setVisibility(GONE))
				.start();
	}

	private void showProgress() {
		progressView.animate().cancel();
		progressView.setAlpha(0);
		progressView.setVisibility(View.VISIBLE);
		progressView.animate()
				.alpha(1)
				.setDuration(animationDuration)
				.start();
	}

	private void hideProgress() {
		progressView.animate().cancel();
		progressView.animate()
				.alpha(0)
				.setDuration(animationDuration)
				.withEndAction(() -> progressView.setVisibility(GONE))
				.start();
	}

	public static void startMe(Context context, long contactId, @ColorInt int associatedColor) {
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(CONTACT_ID_EXTRA, contactId);
		intent.putExtra(CONTACT_ASSOCIATED_COLOR_EXTRA, associatedColor);
		context.startActivity(intent);
	}
}
