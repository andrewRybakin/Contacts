package com.mercdev.rybakin.contacts.list;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.mercdev.rybakin.contacts.BaseActivity;
import com.mercdev.rybakin.contacts.R;
import com.mercdev.rybakin.contacts.details.DetailsActivity;
import com.mercdev.rybakin.contacts.utils.RecyclerViewCursorAdapter;

public class ContactsListActivity extends BaseActivity {
	private static final int CONTACTS_LOADER_ID = 0;

	private ContactsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_contacts);
		Toolbar toolbar = (Toolbar) findViewById(R.id.contacts_toolbar);
		setSupportActionBar(toolbar);

		adapter = new ContactsAdapter();
		((RecyclerView) findViewById(R.id.contacts_list)).setAdapter(adapter);
	}

	@Override
	protected void onPermissionGranted() {
		initContactLoader();
	}

	@Override
	protected void onPermissionDeclined() {
		Snackbar.make(findViewById(R.id.contacts_list), R.string.no_permissions, Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.no_permission_settings, view -> openPermissionsSettings()).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getLoaderManager().destroyLoader(CONTACTS_LOADER_ID);
	}

	private void initContactLoader() {
		Bundle loaderArgs = new Bundle();
		loaderArgs.putStringArray(ContactsLoaderCallback.PROJECTION_KEY, ContactModel.FIELDS);
		loaderArgs.putString(ContactsLoaderCallback.SORT_ORDER_KEY, ContactModel.FIELD_TO_SORT_BY);
		getLoaderManager().initLoader(CONTACTS_LOADER_ID, loaderArgs, new ContactsLoaderCallback());
	}

	private class ContactsLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
		static final String PROJECTION_KEY = "projection";
		static final String SELECTION_KEY = "selection";
		static final String SELECTION_ARGS_KEY = "selectionArgs";
		static final String SORT_ORDER_KEY = "sortOrder";

		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
			if (loaderId == CONTACTS_LOADER_ID) {
				String[] projection = args.getStringArray(PROJECTION_KEY);
				String selection = args.getString(SELECTION_KEY);
				String[] selectionArgs = args.getStringArray(SELECTION_ARGS_KEY);
				String sortOrder = args.getString(SORT_ORDER_KEY);
				return new CursorLoader(ContactsListActivity.this, ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
			}
			return null;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			adapter.swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			adapter.swapCursor(null);
		}
	}

	private class ContactsAdapter extends RecyclerViewCursorAdapter<ContactViewHolder> {
		@Override
		public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ContactViewHolder(new ContactView(ContactsListActivity.this));
		}

		@Override
		protected void onBindViewHolder(ContactViewHolder holder, Cursor cursor) {
			holder.bind(ContactModel.build(ContactsListActivity.this, cursor));
		}
	}

	private class ContactViewHolder extends RecyclerView.ViewHolder {
		ContactViewHolder(View itemView) {
			super(itemView);
		}

		void bind(final ContactModel contact) {
			if (itemView instanceof ContactView) {
				((ContactView) itemView).setContact(contact);
				itemView.setOnClickListener(view -> DetailsActivity.startMe(ContactsListActivity.this, contact.getId(), contact.getAssociatedColor()));
			}
		}
	}
}
