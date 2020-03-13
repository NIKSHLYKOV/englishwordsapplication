package ru.nikshlykov.englishwordsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private final static String LOG_TAG = "ProfileFragment";
    private Context context;

    // View элементы.
    private Button settings;
    private Button modes;
    private Button statistics;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_profile, null);
        viewElementsFinding(view);
        modes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModesActivity.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void viewElementsFinding(View view) {
        settings = view.findViewById(R.id.fragment_profile___Button___settings);
        modes = view.findViewById(R.id.fragment_profile___Button___toModes);
        statistics = view.findViewById(R.id.fragment_profile___Button___toStatistics);
    }
}
