package com.pchmn.sample.materialchipsinput;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.toString();
    @BindView(R.id.contacts_button) Button mContactListButton;
    @BindView(R.id.custom_chips_button) Button mCustomChipsButton;
    private int mStackLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // butter knife
        ButterKnife.bind(this);

        mContactListButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ContactListActivity.class));
        });

        mCustomChipsButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ChipExamplesActivity.class));
        });
    }

    @OnClick(R.id.dialog_fragment)
    public void showDialog() {
        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        MyDialogFragment newFragment = MyDialogFragment.newInstance(mStackLevel);
        newFragment.show(ft, "ok");
    }
}
