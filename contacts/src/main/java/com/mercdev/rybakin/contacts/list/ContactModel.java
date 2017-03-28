package com.mercdev.rybakin.contacts.list;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;

import com.mercdev.rybakin.contacts.R;

class ContactModel {
	static final String[] FIELDS = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI };

	static final String FIELD_TO_SORT_BY = ContactsContract.Contacts.DISPLAY_NAME;

	private static final int ID_COLUMN_POSITION = 0;
	private static final int NAME_COLUMN_POSITION = 1;
	private static final int PHOTO_COLUMN_POSITION = 2;

	private final long id;
	private final String name;
	private final Uri photoUri;
	@ColorInt
	private final int associatedColor;

	private ContactModel(Context context, long id, String name, String photoUri) {
		this.id = id;
		this.name = name;
		if (photoUri != null) {
			this.photoUri = Uri.parse(photoUri);
		} else {
			this.photoUri = Uri.EMPTY;
		}
		int[] colorsArray = context.getResources().getIntArray(R.array.contacts_colors);
		associatedColor = colorsArray[Math.round((float) Math.random() * (colorsArray.length - 1))];
	}

	long getId() {
		return id;
	}

	String getName() {
		return name;
	}

	Uri getPhotoUri() {
		return photoUri;
	}

	@ColorInt
	int getAssociatedColor() {
		return associatedColor;
	}

	static ContactModel build(Context context, Cursor cursor) {
		int[] columns = new int[ContactModel.FIELDS.length];
		for (int i = 0; i < ContactModel.FIELDS.length; i++) {
			columns[i] = cursor.getColumnIndexOrThrow(ContactModel.FIELDS[i]);
		}
		long id = cursor.getLong(columns[ID_COLUMN_POSITION]);
		String name = cursor.getString(columns[NAME_COLUMN_POSITION]);
		String photoUri = cursor.getString(columns[PHOTO_COLUMN_POSITION]);
		return new ContactModel(context, id, name, photoUri);
	}
}
