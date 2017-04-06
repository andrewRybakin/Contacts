package com.mercdev.rybakin.contacts.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mercdev.rybakin.contacts.R;

public class ContactUtils {
	public static final int NO_ASSOCIATED_COLOR = Color.TRANSPARENT;
	public static final String[] CONTACT_LIST_PROJECTION = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI };
	public static final String CONTACT_LIST_FIELD_SORT_BY = ContactsContract.Contacts.DISPLAY_NAME;
	public static final String[] CONTACT_DETAILS_PROJECTION = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.PHOTO_URI,
	};
	public static final String[] PHONE_NUMBERS_PROJECTION = new String[] {
			ContactsContract.CommonDataKinds.Phone.TYPE,
			ContactsContract.CommonDataKinds.Phone.NUMBER
	};

	private static final String TAG = "ContactUtils";

	private ContactUtils() {
	}

	public static long getId(Cursor cursor) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
	}

	public static String getName(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
	}

	public static Uri getPhotoThumbnailUri(Cursor cursor) {
		String photoUriString = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
		Uri photoUri = Uri.EMPTY;
		if (photoUriString != null) {
			photoUri = Uri.parse(photoUriString);
		}
		return photoUri;
	}

	public static Uri getPhotoUri(Cursor cursor) {
		String photoUriString = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI));
		Uri photoUri = Uri.EMPTY;
		if (photoUriString != null) {
			photoUri = Uri.parse(photoUriString);
		}
		return photoUri;
	}

	public static List<PhoneNumber> getPhoneNumbers(Cursor cursor) {
		ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
		int typeColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE);
		int numberColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
		if (!cursor.isBeforeFirst()) {
			cursor.moveToPosition(-1);
		}
		while (cursor.moveToNext()) {
			int type = cursor.getInt(typeColumn);
			String number = cursor.getString(numberColumn);
			try {
				String formattedNumber = PhoneNumberUtils.formatNumber(number, Locale.getDefault().getISO3Country());
				if (formattedNumber != null) {
					number = formattedNumber;
				}
			} catch (Exception e) {
				Log.d(TAG, "getPhoneNumbers: Malformed phoneNumber! Skip formatting.");
			}
			phoneNumbers.add(new PhoneNumber(type, number));
		}
		return phoneNumbers;
	}

	@ColorInt
	public static int getRandomAssociatedColor(Context context) {
		int[] colorsArray = context.getResources().getIntArray(R.array.contacts_colors);
		return colorsArray[Math.round((float) Math.random() * (colorsArray.length - 1))];
	}

	public static class PhoneNumber {
		private final int phoneType;
		private final String phoneNumber;

		public PhoneNumber(int phoneType, String phoneNumber) {
			this.phoneType = phoneType;
			this.phoneNumber = phoneNumber;
		}

		public int getPhoneType() {
			return phoneType;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}
	}
}
