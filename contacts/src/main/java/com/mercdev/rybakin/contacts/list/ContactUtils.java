package com.mercdev.rybakin.contacts.list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;

import com.mercdev.rybakin.contacts.R;

class ContactUtils {
	static final String[] FIELDS = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI };
	static final String FIELD_TO_SORT_BY = ContactsContract.Contacts.DISPLAY_NAME;

	static final int NO_ASSOCIATED_COLOR = Color.TRANSPARENT;

	private static final int ID_COLUMN_POSITION = 0;
	private static final int NAME_COLUMN_POSITION = 1;
	private static final int PHOTO_COLUMN_POSITION = 2;

	static long getId(Cursor cursor) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(FIELDS[ID_COLUMN_POSITION]));
	}

	static String getName(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndexOrThrow(FIELDS[NAME_COLUMN_POSITION]));
	}

	static Uri getPhotoUri(Cursor cursor) {
		String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(FIELDS[PHOTO_COLUMN_POSITION]));
		if (photoUri != null) {
			return Uri.parse(photoUri);
		} else {
			return Uri.EMPTY;
		}
	}

	@ColorInt
	static int getRandomAssociatedColor(Context context) {
		int[] colorsArray = context.getResources().getIntArray(R.array.contacts_colors);
		return colorsArray[Math.round((float) Math.random() * (colorsArray.length - 1))];
	}
}
