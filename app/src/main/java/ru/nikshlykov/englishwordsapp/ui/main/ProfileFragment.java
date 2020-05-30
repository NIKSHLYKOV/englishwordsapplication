package ru.nikshlykov.englishwordsapp.ui.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.settings.SettingsActivity;

public class ProfileFragment extends Fragment {
    private final static String LOG_TAG = "ProfileFragment";
    private Context context;

    // View элементы.
    private MaterialButton settings;
    private MaterialButton modes;

    private ProfileFragmentReportListener reportListener;

    public interface ProfileFragmentReportListener{
        void reportOpenModesActivity();
        void reportOpenSettingsActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        reportListener = (ProfileFragmentReportListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_profile, null);
        findViews(view);
        modes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportListener.reportOpenModesActivity();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportListener.reportOpenSettingsActivity();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        reportListener = null;
    }

    private void findViews(View view) {
        settings = view.findViewById(R.id.fragment_profile___material_button___settings);
        modes = view.findViewById(R.id.fragment_profile___material_button___modes);
    }
}

