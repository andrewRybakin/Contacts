package com.mercdev.rybakin.contacts.list;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.mercdev.rybakin.contacts.common.RecyclerViewCursorAdapter;
import com.mercdev.rybakin.contacts.details.DetailsActivity;
import com.mercdev.rybakin.contacts.utils.ContactUtils;

class ContactsAdapter extends RecyclerViewCursorAdapter<ContactsAdapter.ContactViewHolder> {
	private final SparseIntArray colors = new SparseIntArray();

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ContactViewHolder(new ContactView(parent.getContext()));
	}

	@Override
	protected void onBindViewHolder(ContactViewHolder holder, Cursor cursor) {
		long id = ContactUtils.getId(cursor);
		String name = ContactUtils.getName(cursor);
		Uri photoUri = ContactUtils.getPhotoThumbnailUri(cursor);

		int position = cursor.getPosition();
		int color = colors.get(position, ContactUtils.NO_ASSOCIATED_COLOR);
		if (color == ContactUtils.NO_ASSOCIATED_COLOR) {
			color = ContactUtils.getRandomAssociatedColor(holder.itemView.getContext());
			colors.put(position, color);
		}

		holder.bind(id, name, photoUri, color);
	}

	static class ContactViewHolder extends RecyclerView.ViewHolder {
		ContactViewHolder(View itemView) {
			super(itemView);
		}

		void bind(long id, String name, Uri photoUri, int color) {
			if (itemView instanceof ContactView) {
				ContactView contactView = ((ContactView) itemView);
				contactView.setName(name);
				contactView.setPhoto(photoUri);
				contactView.setAssociatedColor(color);
				itemView.setOnClickListener(view -> DetailsActivity.startMe(itemView.getContext(), id, color));
			}
		}
	}
}



