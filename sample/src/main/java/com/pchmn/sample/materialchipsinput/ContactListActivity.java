package com.pchmn.sample.materialchipsinput;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = ContactListActivity.class.toString();
    @BindView(R.id.chips_input) ChipsInput mChipsInput;
    @BindView(R.id.validate) Button mValidateButton;
    @BindView(R.id.chip_list) TextView mChipListText;
    private List<ContactChip> mContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        // butter knife
        ButterKnife.bind(this);
        mContactList = new ArrayList<>();

        // get contact list
        new RxPermissions(this)
                .request(Manifest.permission.READ_CONTACTS)
                .subscribe(granted -> {
                    if(granted && mContactList.size() == 0)
                        getContactList();

                }, err -> {
                    Log.e(TAG, err.getMessage());
                    Toast.makeText(ContactListActivity.this, "Error get contacts, see logs", Toast.LENGTH_LONG).show();
                });

        // chips listener
        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip added, " + newSize);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip removed, " + newSize);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.e(TAG, "text changed: " + text.toString());
            }
        });

        // show selected chips
        mValidateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String listString = "";
                for(ContactChip chip: (List<ContactChip>)  mChipsInput.getSelectedChipList()) {
                    listString += chip.getLabel() + " (" + (chip.getInfo() != null ? chip.getInfo(): "") + ")" + ", ";
                }

                mChipListText.setText(listString);
            }
        });
    }

    /**
     * Get the contacts of the user and add each contact in the mContactList
     * And finally pass the mContactList to the mChipsInput
     */
    private void getContactList() {
        Cursor phones = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);

        // loop over all contacts
        if(phones != null) {
            while (phones.moveToNext()) {
                // get contact info
                String phoneNumber = null;
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String avatarUriString = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Uri avatarUri = null;
                if(avatarUriString != null)
                    avatarUri = Uri.parse(avatarUriString);

                // get phone number
                if (Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

                    while (pCur != null && pCur.moveToNext()) {
                        phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    pCur.close();

                }

                ContactChip contactChip = new ContactChip(id, avatarUri, name, phoneNumber);
                // add contact to the list
                mContactList.add(contactChip);
            }
            phones.close();
        }

        // pass contact list to chips input
        mChipsInput.setFilterableList(mContactList);
    }
}
