package com.mercdev.rybakin.contacts.list;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
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
			return;
		}
		if (!cursor.moveToPosition(position)) {
			Log.w(TAG, "onBindViewHolder: Couldn't move the cursor! ViewHolder will not be bind");
			return;
		}
		onBindViewHolder(holder, cursor);
	}

	@Override
	public int getItemCount() {
		if (cursor != null && isDataValid) {
			return cursor.getCount();
		} else {
			return 0;
		}
	}

	@Override
	public long getItemId(int position) {
		if (cursor != null && isDataValid && cursor.moveToPosition(position)) {
			return cursor.getLong(rowIDColumn);
		}
		return RecyclerView.NO_ID;
	}

	void swapCursor(Cursor newCursor) {
		if (newCursor == cursor) {
			return;
		}
		Cursor oldCursor = cursor;
		if (oldCursor != null) {
			oldCursor.unregisterDataSetObserver(dataSetObserver);
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
