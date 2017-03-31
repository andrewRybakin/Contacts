package com.mercdev.rybakin.contacts.details;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.mercdev.rybakin.contacts.utils.ContactUtils;

class PhoneNumbersAdapter extends RecyclerView.Adapter<PhoneNumbersAdapter.PhoneNumberViewHolder> {
	private final List<ContactUtils.PhoneNumber> phoneNumbers = new ArrayList<>();

	@ColorInt
	private int associatedColor = Color.TRANSPARENT;

	@Override
	public PhoneNumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new PhoneNumberViewHolder(new PhoneNumberView(parent.getContext()));
	}

	@Override
	public void onBindViewHolder(PhoneNumberViewHolder holder, int position) {
		ContactUtils.PhoneNumber phoneNumber = phoneNumbers.get(position);
		holder.bind(phoneNumber.getPhoneNumber(), phoneNumber.getPhoneType(), associatedColor);
	}

	@Override
	public int getItemCount() {
		return phoneNumbers.size();
	}

	void setPhoneNumbers(List<ContactUtils.PhoneNumber> phoneNumbers) {
		this.phoneNumbers.clear();
		this.phoneNumbers.addAll(phoneNumbers);
		notifyDataSetChanged();
	}

	void setAssociatedColor(int color) {
		associatedColor = color;
		notifyDataSetChanged();
	}

	static class PhoneNumberViewHolder extends RecyclerView.ViewHolder {
		PhoneNumberViewHolder(View itemView) {
			super(itemView);
		}

		void bind(String phoneNumber, int phoneType, int associatedColor) {
			String phoneTypeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(itemView.getResources(), phoneType, "Other").toString();
			((PhoneNumberView) itemView).setData(phoneTypeString, phoneNumber);
			((PhoneNumberView) itemView).setAssociatedColor(associatedColor);
		}
	}
}
