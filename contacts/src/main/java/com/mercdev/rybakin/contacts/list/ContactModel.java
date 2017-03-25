package com.mercdev.rybakin.contacts.list;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

class ContactModel {
	static final String[] FIELDS = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI };

	static final String SORT_BY_FIELD = ContactsContract.Contacts.DISPLAY_NAME;

	private static final int ID_COLUMN_POSITION = 0;
	private static final int NAME_COLUMN_POSITION = 1;
	private static final int PHOTO_COLUMN_POSITION = 2;

	private long id;
	private String name;
	private Uri photoUri;

	private ContactModel(long id, String name, String photoUri) {
		this.id = id;
		this.name = name;
		if (photoUri != null) {
			this.photoUri = Uri.parse(photoUri);
		} else {
			this.photoUri = Uri.EMPTY;
		}
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	Uri getPhotoUri() {
		return photoUri;
	}

	static ContactModel build(Cursor cursor) {
		int[] columns = new int[ContactModel.FIELDS.length];
		for (int i = 0; i < ContactModel.FIELDS.length; i++) {
			columns[i] = cursor.getColumnIndexOrThrow(ContactModel.FIELDS[i]);
		}
		long id = cursor.getLong(columns[ID_COLUMN_POSITION]);
		String name = cursor.getString(columns[NAME_COLUMN_POSITION]);
		String photoUri = cursor.getString(columns[PHOTO_COLUMN_POSITION]);
		return new ContactModel(id, name, photoUri);
	}
}
