package com.mercdev.rybakin.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

	private static final int CONTACTS_PERMISSION_REQUEST_CODE = 0;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (checkContactsPermission()) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CONTACTS }, CONTACTS_PERMISSION_REQUEST_CODE);
		} else {
			onPermissionGranted();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == CONTACTS_PERMISSION_REQUEST_CODE && grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			onPermissionGranted();
		} else {
			onPermissionDeclined();
		}
	}

	protected void openPermissionsSettings() {
		Intent i = new Intent();
		i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		i.setData(Uri.parse("package:" + getPackageName()));
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(i);
	}

	protected abstract void onPermissionGranted();

	protected abstract void onPermissionDeclined();

	private boolean checkContactsPermission() {
		return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED;
	}
}
