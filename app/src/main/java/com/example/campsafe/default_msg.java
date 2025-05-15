package com.example.campsafe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DefaultMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefaultMessageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView messageTextView;
    private String messageToShow;

    public DefaultMessageFragment() {
        // Required empty public constructor
    }

    public static DefaultMessageFragment newInstance(String param1, String param2) {
        DefaultMessageFragment fragment = new DefaultMessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            messageToShow = mParam1;  // Initialize message with param1, adjust if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_default_msg, container, false);
        messageTextView = rootView.findViewById(R.id.txtt);

        if (messageToShow != null) {
            messageTextView.setText(messageToShow);
        }

        return rootView;
    }

    public void updateText(String text) {
        messageToShow = text;
        if (messageTextView != null) {
            messageTextView.setText(text);
        }
    }
}
