package com.pchmn.sample.materialchipsinput;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyDialogFragment extends AppCompatDialogFragment {

    public static MyDialogFragment newInstance(int num) {
        MyDialogFragment f = new MyDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.framgnet_dialog, container, false);
    }
}