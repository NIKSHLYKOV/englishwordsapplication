package ru.nikshlykov.englishwordsapp;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GroupsFragment extends Fragment {
    // View компоненты фрагмента.
    private ExpandableListView expandableListView;

    // Контекст, передаваемый при прикреплении фрагмента.
    private Context context;

    private String LOG_TAG = "GroupsFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(LOG_TAG, "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, null);
    }


}
