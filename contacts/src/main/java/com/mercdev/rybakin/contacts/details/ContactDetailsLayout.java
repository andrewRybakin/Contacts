package com.mercdev.rybakin.contacts.details;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.mercdev.rybakin.contacts.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ContactDetailsLayout extends CoordinatorLayout {
	private final Toolbar toolbar;
	private final CollapsingToolbarLayout collapsing;

	private final ImageView contactPhotoView;
	private final RecyclerView phoneNumbers;

	public ContactDetailsLayout(Context context) {
		this(context, null);
	}

	public ContactDetailsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactDetailsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(context, R.layout.v_contact_details, this);
		setFitsSystemWindows(true);

		toolbar = (Toolbar) findViewById(R.id.details_toolbar);
		collapsing = (CollapsingToolbarLayout) findViewById(R.id.details_collapsing);

		contactPhotoView = (ImageView) findViewById(R.id.details_contact_photo);
		phoneNumbers = (RecyclerView) findViewById(R.id.details_phone_numbers);
	}

	public void setAssociatedColor(@ColorInt int color) {
		collapsing.setContentScrimColor(color);
		collapsing.setStatusBarScrimColor(color);
		collapsing.setBackgroundColor(color);
	}

	public void setContact(ContactDetailsModel model) {
		toolbar.setTitle(model.getName());
		Picasso.with(getContext())
				.load(model.getPhotoUri())
				.placeholder(R.drawable.contact_placeholder)
				.into(contactPhotoView);
	}

	public void setContact(ContactDetailsModel model, DetectAssociatedColorTransform.OnAssociatedColorDetected detectionEndListener) {
		toolbar.setTitle(model.getName());
		Picasso.with(getContext())
				.load(model.getPhotoUri())
				.placeholder(R.drawable.contact_placeholder)
				.transform(new DetectAssociatedColorTransform(detectionEndListener))
				.memoryPolicy(MemoryPolicy.NO_CACHE)
				.into(contactPhotoView);
	}

	public void setPhoneNumbersAdapter(RecyclerView.Adapter<PhoneNumberViewHolder> adapter) {
		phoneNumbers.setAdapter(adapter);
	}

	static class PhoneNumberViewHolder extends RecyclerView.ViewHolder {
		PhoneNumberViewHolder(View itemView) {
			super(itemView);
		}

		void bind(ContactDetailsModel.PhoneNumber phoneNumber, int associatedColor) {
			CharSequence phoneTypeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(itemView.getResources(), phoneNumber.getType(), "Other");
			((PhoneNumberView) itemView).setData(phoneTypeString.toString(), phoneNumber.getNumber());
			((PhoneNumberView) itemView).setAssociatedColor(associatedColor);
		}
	}
}
