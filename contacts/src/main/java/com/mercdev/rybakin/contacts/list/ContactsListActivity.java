package com.mercdev.rybakin.contacts.list;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mercdev.rybakin.contacts.R;

public class ContactsListActivity extends AppCompatActivity {
	private ContactsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_contacts);
		Toolbar toolbar = (Toolbar) findViewById(R.id.contacts_toolbar);
		setSupportActionBar(toolbar);

		adapter = new ContactsAdapter();
		((RecyclerView) findViewById(R.id.contacts_list)).setAdapter(adapter);

		Bundle loaderArgs = new Bundle();
		loaderArgs.putStringArray(ContactsLoaderCallback.PROJECTION_KEY, ContactModel.FIELDS);
		loaderArgs.putString(ContactsLoaderCallback.SORT_ORDER_KEY, ContactModel.SORT_BY_FIELD);
		getLoaderManager().initLoader(0, loaderArgs, new ContactsLoaderCallback());

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.contacts_add_button);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.m_contacts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private class ContactsLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
		static final String PROJECTION_KEY = "projection";
		static final String SELECTION_KEY = "selection";
		static final String SELECTION_ARGS_KEY = "selectionArgs";
		static final String SORT_ORDER_KEY = "sortOrder";

		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
			if (loaderId == 0) {
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
			holder.bind(ContactModel.build(cursor));
		}
	}

	private static class ContactViewHolder extends RecyclerView.ViewHolder {
		ContactViewHolder(View itemView) {
			super(itemView);
		}

		void bind(ContactModel contact) {
			if (itemView instanceof ContactView) {
				((ContactView) itemView).setContact(contact);
				itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// Open DetailsActivity
					}
				});
			}
		}
	}
}
