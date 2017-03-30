package com.mercdev.rybakin.contacts.details;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mercdev.rybakin.contacts.BaseActivity;
import com.mercdev.rybakin.contacts.R;
import com.mercdev.rybakin.contacts.utils.RecyclerViewCursorAdapter;

import static android.view.View.GONE;

public class DetailsActivity extends BaseActivity {
	private static final String TAG = "DetailsActivity";

	private static final String CONTACT_ID_EXTRA = "contactId";
	private static final String CONTACT_ASSOCIATED_COLOR_EXTRA = "associatedExtra";
	private static final int CONTACT_LOADER_ID = 0;
	private static final int PHONE_NUMBERS_LOADER_ID = 1;
	private static final long CONTACT_NO_ID = -1;

	private final ContactLoaderCallback loaderCallback = new ContactLoaderCallback();

	private ContactDetailsLayout layout;
	private ProgressBar progressView;

	private PhoneNumbersAdapter adapter;
	private long contactId;
	private long animationDuration;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_details);
		int associatedColor = getIntent().getIntExtra(CONTACT_ASSOCIATED_COLOR_EXTRA, getResources().getColor(R.color.colorPrimary));
		adapter = new PhoneNumbersAdapter();

		progressView = (ProgressBar) findViewById(R.id.progress_placeholder);

		layout = (ContactDetailsLayout) findViewById(R.id.details_layout);
		layout.setPhoneNumbersAdapter(adapter);
		setAssociatedColor(associatedColor);

		((RecyclerView) findViewById(R.id.details_phone_numbers)).setAdapter(adapter);

		animationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
		hideLayout();
		showProgress();
	}

	@Override
	protected void onPermissionGranted() {
		contactId = getIntent().getLongExtra(CONTACT_ID_EXTRA, CONTACT_NO_ID);
		if (contactId != CONTACT_NO_ID) {
			initContactLoader();
			initPhoneNumbersLoader();
		} else {
			Snackbar.make(findViewById(R.id.contacts_list), R.string.no_permissions, Snackbar.LENGTH_INDEFINITE).show();
		}
	}

	@Override
	protected void onPermissionDeclined() {
		Snackbar.make(findViewById(R.id.contacts_list), R.string.no_permissions, Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.no_permission_settings, view -> openPermissionsSettings()).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getLoaderManager().destroyLoader(CONTACT_LOADER_ID);
		getLoaderManager().destroyLoader(PHONE_NUMBERS_LOADER_ID);
	}

	private void setAssociatedColor(@ColorInt int color) {
		getWindow().setStatusBarColor(color);
		layout.setAssociatedColor(color);
		adapter.setAssociatedColor(color);
	}

	private void initContactLoader() {
		Bundle loaderArgs = new Bundle();
		loaderArgs.putStringArray(ContactLoaderCallback.PROJECTION_KEY, ContactDetailsModel.CONTACT_PROJECTION);
		loaderArgs.putString(ContactLoaderCallback.SELECTION_KEY, ContactsContract.Contacts._ID + " = ?");
		loaderArgs.putStringArray(ContactLoaderCallback.SELECTION_ARGS_KEY, new String[] { String.valueOf(contactId) });
		getLoaderManager().initLoader(CONTACT_LOADER_ID, loaderArgs, loaderCallback);
	}

	private void initPhoneNumbersLoader() {
		Bundle loaderArgs = new Bundle();
		loaderArgs.putStringArray(ContactLoaderCallback.PROJECTION_KEY, ContactDetailsModel.PhoneNumber.PHONE_NUMBERS_PROJECTION);
		loaderArgs.putString(ContactLoaderCallback.SELECTION_KEY, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?");
		loaderArgs.putStringArray(ContactLoaderCallback.SELECTION_ARGS_KEY, new String[] { String.valueOf(contactId) });
		getLoaderManager().initLoader(PHONE_NUMBERS_LOADER_ID, loaderArgs, loaderCallback);
	}

	private void updateContact(ContactDetailsModel model) {
		if (model.getPhotoUri() != Uri.EMPTY) {
			layout.setContact(model, new DetectAssociatedColorTransform.OnAssociatedColorDetected() {
				@Override
				void onAssociatedColorDetected(int color) {
					setAssociatedColor(color);
					hideProgress();
					showLayout();
				}
			});
		} else {
			layout.setContact(model);
			hideProgress();
			showLayout();
		}
	}

	private void showLayout() {
		if (layout.getVisibility() != View.VISIBLE) {
			layout.setAlpha(0);
			layout.setVisibility(View.VISIBLE);
			layout.animate()
					.alpha(1)
					.setDuration(animationDuration)
					.start();
		}
	}

	private void hideLayout() {
		if (layout.getVisibility() == View.VISIBLE) {
			layout.animate()
					.alpha(0)
					.setDuration(animationDuration)
					.withEndAction(() -> layout.setVisibility(GONE))
					.start();
		}
	}

	private void showProgress() {
		if (progressView.getVisibility() != View.VISIBLE) {
			progressView.setAlpha(0);
			progressView.setVisibility(View.VISIBLE);
			progressView.animate()
					.alpha(1)
					.setDuration(animationDuration)
					.start();
		}
	}

	private void hideProgress() {
		if (progressView.getVisibility() == View.VISIBLE) {
			progressView.animate()
					.alpha(0)
					.setDuration(animationDuration)
					.withEndAction(() -> progressView.setVisibility(GONE))
					.start();
		}
	}

	private class ContactLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
		static final String PROJECTION_KEY = "projection";
		static final String SELECTION_KEY = "selection";
		static final String SELECTION_ARGS_KEY = "selectionArgs";
		static final String SORT_ORDER_KEY = "sortOrder";

		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
			String[] projection = args.getStringArray(PROJECTION_KEY);
			String selection = args.getString(SELECTION_KEY);
			String[] selectionArgs = args.getStringArray(SELECTION_ARGS_KEY);
			String sortOrder = args.getString(SORT_ORDER_KEY);
			switch (loaderId) {
				case 0:
					return new CursorLoader(DetailsActivity.this, ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				case 1:
					return new CursorLoader(DetailsActivity.this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				default:
					return null;
			}
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			switch (loader.getId()) {
				case 0:
					updateContact(ContactDetailsModel.build(cursor));
					break;
				case 1:
					adapter.swapCursor(cursor);
					break;
				default:
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			adapter.swapCursor(null);
		}
	}

	private class PhoneNumbersAdapter extends RecyclerViewCursorAdapter<ContactDetailsLayout.PhoneNumberViewHolder> {
		@ColorInt
		private int associatedColor = Color.TRANSPARENT;

		@Override
		public ContactDetailsLayout.PhoneNumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ContactDetailsLayout.PhoneNumberViewHolder(new PhoneNumberView(DetailsActivity.this));
		}

		@Override
		protected void onBindViewHolder(ContactDetailsLayout.PhoneNumberViewHolder holder, Cursor cursor) {
			holder.bind(ContactDetailsModel.PhoneNumber.build(cursor), associatedColor);
		}

		void setAssociatedColor(int color) {
			associatedColor = color;
			notifyDataSetChanged();
		}
	}

	public static void startMe(Context context, long contactId, @ColorInt int associatedColor) {
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(CONTACT_ID_EXTRA, contactId);
		intent.putExtra(CONTACT_ASSOCIATED_COLOR_EXTRA, associatedColor);
		context.startActivity(intent);
	}
}
