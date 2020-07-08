package ru.nikshlykov.englishwordsapp.ui.main;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.statistics.StatisticsFragment;

public class ProfileFragment extends Fragment {
    private final static String LOG_TAG = "ProfileFragment";
    private Context context;

    // View элементы.
    private MaterialButton settingsMaterialButton;
    private MaterialButton modesMaterialButton;

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
        modesMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportListener.reportOpenModesActivity();
            }
        });
        settingsMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportListener.reportOpenSettingsActivity();
            }
        });

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        fragmentTransaction.replace(R.id.fragment_profile___linear_layout___statistics, statisticsFragment);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        reportListener = null;
    }

    private void findViews(View view) {
        settingsMaterialButton = view.findViewById(R.id.fragment_profile___material_button___settings);
        modesMaterialButton = view.findViewById(R.id.fragment_profile___material_button___modes);
    }
}

