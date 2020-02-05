package ru.nikshlykov.englishwordsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SubgroupActivity extends AppCompatActivity {

    private String EXTRA_SUBGROUP_ID = "SubgroupId";

    private final static String LOG_TAG = "SubgroupActivity"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subgroup);

        Log.d(LOG_TAG, "OnCreate");
    }
}
