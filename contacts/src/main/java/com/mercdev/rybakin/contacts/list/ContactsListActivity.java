package com.mercdev.rybakin.contacts.list;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
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
		loaderArgs.putStringArray(ContactsLoaderCallback.PROJECTION_KEY, ContactUtils.FIELDS);
		loaderArgs.putString(ContactsLoaderCallback.SORT_ORDER_KEY, ContactUtils.FIELD_TO_SORT_BY);
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
		private final SparseIntArray colors = new SparseIntArray();

		@Override
		public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ContactViewHolder(new ContactView(ContactsListActivity.this));
		}

		@Override
		protected void onBindViewHolder(ContactViewHolder holder, Cursor cursor) {
			long id = ContactUtils.getId(cursor);
			String name = ContactUtils.getName(cursor);
			Uri photoUri = ContactUtils.getPhotoUri(cursor);

			int position = cursor.getPosition();
			int color = colors.get(position, ContactUtils.NO_ASSOCIATED_COLOR);
			if (color == ContactUtils.NO_ASSOCIATED_COLOR) {
				color = ContactUtils.getRandomAssociatedColor(ContactsListActivity.this);
				colors.put(position, color);
			}

			holder.bind(id, name, photoUri, color);
		}
	}

	private class ContactViewHolder extends RecyclerView.ViewHolder {
		ContactViewHolder(View itemView) {
			super(itemView);
		}

		void bind(long id, String name, Uri photoUri, int color) {
			if (itemView instanceof ContactView) {
				ContactView contactView = ((ContactView) itemView);
				contactView.setName(name);
				contactView.setPhoto(photoUri);
				contactView.setAssociatedColor(color);
				itemView.setOnClickListener(view -> DetailsActivity.startMe(ContactsListActivity.this, id, color));
			}
		}
	}
}
