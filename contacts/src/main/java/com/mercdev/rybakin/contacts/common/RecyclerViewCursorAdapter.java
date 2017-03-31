package com.mercdev.rybakin.contacts.common;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
	private static final String TAG = "RVCursorAdapter";

	private final DataSetObserver dataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			isDataValid = true;
			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			isDataValid = false;
			notifyDataSetChanged();
		}
	};

	private Cursor cursor;
	private boolean isDataValid;
	private int rowIDColumn;

	@Override
	public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

	@Override
	public void onBindViewHolder(VH holder, int position) {
		if (!isDataValid) {
			Log.w(TAG, "onBindViewHolder: The data is invalid! Cannot bind view");
		} else if (!cursor.moveToPosition(position)) {
			Log.w(TAG, "onBindViewHolder: Couldn't move the cursor! ViewHolder will not be bind");
		} else {
			onBindViewHolder(holder, cursor);
		}
	}

	@Override
	public int getItemCount() {
		return cursor != null && isDataValid ? cursor.getCount() : 0;
	}

	@Override
	public long getItemId(int position) {
		long itemId = RecyclerView.NO_ID;
		if (cursor != null && isDataValid && cursor.moveToPosition(position)) {
			itemId = cursor.getLong(rowIDColumn);
		}
		return itemId;
	}

	public void swapCursor(Cursor newCursor) {
		if (newCursor == cursor) {
			return;
		}
		Cursor oldCursor = cursor;
		if (oldCursor != null) {
			oldCursor.unregisterDataSetObserver(dataSetObserver);
			oldCursor.close();
		}
		cursor = newCursor;
		if (cursor != null) {
			cursor.registerDataSetObserver(dataSetObserver);
			rowIDColumn = newCursor.getColumnIndex("_id");
			isDataValid = true;
			notifyDataSetChanged();
		} else {
			rowIDColumn = -1;
			isDataValid = false;
			notifyDataSetChanged();
		}
	}

	protected abstract void onBindViewHolder(VH holder, Cursor cursor);
}
