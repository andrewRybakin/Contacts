package com.mercdev.rybakin.contacts.details;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercdev.rybakin.contacts.BaseActivity;
import com.mercdev.rybakin.contacts.R;
import com.mercdev.rybakin.contacts.utils.RecyclerViewCursorAdapter;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends BaseActivity {
	private static final String CONTACT_ID_EXTRA = "contactId";
	private static final int CONTACT_LOADER_ID = 0;
	private static final int PHONE_NUMBERS_LOADER_ID = 1;
	private static final long CONTACT_NO_ID = -1;

	private final ContactLoaderCallback loaderCallback = new ContactLoaderCallback();

	private long contactId;

	private TextView nameView;
	private AppCompatImageView contactPhotoView;
	private PhoneNumbersAdapter adapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_details);
		Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		nameView = (TextView) findViewById(R.id.details_name);
		contactPhotoView = (AppCompatImageView) findViewById(R.id.details_photo);
		adapter = new PhoneNumbersAdapter();

		((RecyclerView) findViewById(R.id.details_phone_numbers)).setAdapter(adapter);
	}

	@Override
	protected void onPermissionGranted() {
		contactId = getIntent().getLongExtra(CONTACT_ID_EXTRA, CONTACT_NO_ID);
		if (contactId != CONTACT_NO_ID) {
			initContactLoader();
		}
	}

	@Override
	protected void onPermissionDeclined() {
		Snackbar.make(findViewById(R.id.contacts_list), R.string.no_permissions, Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.no_permission_settings, new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						openPermissionsSettings();
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getLoaderManager().destroyLoader(CONTACT_LOADER_ID);
		getLoaderManager().destroyLoader(PHONE_NUMBERS_LOADER_ID);
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
		nameView.setText(model.getName());
		Picasso.with(contactPhotoView.getContext())
				.load(model.getPhotoUri())
				.placeholder(R.drawable.contact_placeholder)
				.into(contactPhotoView);
		if (model.isHasPhoneNumber()) {
			initPhoneNumbersLoader();
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

	public static void startMe(Context context, long contactId) {
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(CONTACT_ID_EXTRA, contactId);
		context.startActivity(intent);
	}

	private class PhoneNumbersAdapter extends RecyclerViewCursorAdapter<PhoneNumberViewHolder> {

		@Override
		public PhoneNumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new PhoneNumberViewHolder(new PhoneNumberView(DetailsActivity.this));
		}

		@Override
		protected void onBindViewHolder(PhoneNumberViewHolder holder, Cursor cursor) {
			holder.bind(ContactDetailsModel.PhoneNumber.build(cursor));
		}
	}

	private class PhoneNumberViewHolder extends RecyclerView.ViewHolder {
		PhoneNumberViewHolder(View itemView) {
			super(itemView);
		}

		void bind(ContactDetailsModel.PhoneNumber phoneNumber) {
			CharSequence phoneTypeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), phoneNumber.getType(), "Other");
			((PhoneNumberView) itemView).setData(phoneTypeString.toString(), phoneNumber.getNumber());
		}
	}
}
