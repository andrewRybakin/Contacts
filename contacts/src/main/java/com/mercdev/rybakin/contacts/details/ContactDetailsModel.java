package com.mercdev.rybakin.contacts.details;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

class ContactDetailsModel {
	static final String[] CONTACT_PROJECTION = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.PHOTO_URI,
			ContactsContract.Contacts.HAS_PHONE_NUMBER
	};

	private static final int ID_COLUMN_POSITION = 0;
	private static final int NAME_COLUMN_POSITION = 1;
	private static final int PHOTO_COLUMN_POSITION = 2;
	private static final int HAS_PHONE_NUMBER_COLUMN_POSITION = 3;

	private final long id;
	private final String name;
	private final Uri photoUri;
	private final boolean hasPhoneNumber;

	private ContactDetailsModel(long id, String name, Uri photoUri, boolean hasPhoneNumber) {
		this.id = id;
		this.name = name;
		this.photoUri = photoUri;
		this.hasPhoneNumber = hasPhoneNumber;
	}

	public long getId() {
		return id;
	}

	String getName() {
		return name;
	}

	Uri getPhotoUri() {
		return photoUri;
	}

	boolean isHasPhoneNumber() {
		return hasPhoneNumber;
	}

	static ContactDetailsModel build(Cursor cursor) {
		int[] columns = new int[CONTACT_PROJECTION.length];
		for (int i = 0; i < CONTACT_PROJECTION.length; i++) {
			columns[i] = cursor.getColumnIndexOrThrow(CONTACT_PROJECTION[i]);
		}
		cursor.moveToNext();
		long id = cursor.getLong(columns[ID_COLUMN_POSITION]);
		String name = cursor.getString(columns[NAME_COLUMN_POSITION]);
		String photoUriString = cursor.getString(columns[PHOTO_COLUMN_POSITION]);
		Uri photoUri;
		if (photoUriString != null) {
			photoUri = Uri.parse(photoUriString);
		} else {
			photoUri = Uri.EMPTY;
		}
		boolean hasPhoneNumber = cursor.getInt(columns[HAS_PHONE_NUMBER_COLUMN_POSITION]) != 0;
		return new ContactDetailsModel(id, name, photoUri, hasPhoneNumber);
	}

	static class PhoneNumber {
		static final String[] PHONE_NUMBERS_PROJECTION = new String[] {
				ContactsContract.CommonDataKinds.Phone.TYPE,
				ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
		};

		private static final int PHONE_TYPE_COLUMN_POSITION = 0;
		private static final int PHONE_NUMBER_COLUMN_POSITION = 1;

		private final int type;
		private final String number;

		private PhoneNumber(int type, String number) {
			this.type = type;
			this.number = number;
		}

		int getType() {
			return type;
		}

		String getNumber() {
			return number;
		}

		static PhoneNumber build(Cursor cursor) {
			int[] columns = new int[PHONE_NUMBERS_PROJECTION.length];
			for (int i = 0; i < PHONE_NUMBERS_PROJECTION.length; i++) {
				columns[i] = cursor.getColumnIndexOrThrow(PHONE_NUMBERS_PROJECTION[i]);
			}
			int type = cursor.getInt(columns[PHONE_TYPE_COLUMN_POSITION]);
			String number = cursor.getString(columns[PHONE_NUMBER_COLUMN_POSITION]);
			return new PhoneNumber(type, number);
		}
	}
}
