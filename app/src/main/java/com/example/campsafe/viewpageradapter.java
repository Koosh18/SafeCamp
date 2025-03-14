package com.example.campsafe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class viewpageradapter extends FragmentStateAdapter {

    public viewpageradapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new new_visitor();
            case 1:
                return new Notified();
            case 2:
                return new pre_bookings();
            default:
                return new Fragment(); // Default case
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public CharSequence getTitlePage(int position) {
        switch (position) {
            case 0:
                return "New Visitor";
            case 1:
                return "Notified";
            case 2:
                return "Pre bookings";
            default:
                return null;
        }
    }
}
