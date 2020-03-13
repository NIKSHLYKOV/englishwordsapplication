package ru.nikshlykov.englishwordsapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfoFragment extends Fragment {

    static final String EXTRA_SUBGROUPSARENOTCHOSEN = "SubgroupsAreNotChosen";
    static final String EXTRA_MODESARENOTCHOSEN = "ModesAreNotChosen";

    private boolean subgroupsAreNotChosen = false;
    private boolean modesAreNotChosen = false;

    private TextView infoText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("InfoFragment", "onCreate");
        Bundle arguments = getArguments();
        try {
            subgroupsAreNotChosen = arguments.getBoolean(EXTRA_SUBGROUPSARENOTCHOSEN);
        }
        catch (NullPointerException ex){
            subgroupsAreNotChosen = false;
            ex.getMessage();
        }
        try {
            modesAreNotChosen = arguments.getBoolean(EXTRA_MODESARENOTCHOSEN);
        }
        catch (NullPointerException ex){
            modesAreNotChosen = false;
            ex.getMessage();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("InfoFragment", "onCreateView");

        View v = inflater.inflate(R.layout.fragment_info, null);

        infoText = v.findViewById(R.id.fragment_info___text_view___info);

        if (subgroupsAreNotChosen)
            infoText.setText("Для того, чтобы изучать слова, необходимо выбрать группы слов. Сделать это вы можете перейдя во вкладку \"Группы\"");
        else if (modesAreNotChosen)
            infoText.setText("Для того, чтобы изучать слова, необходимо выбрать режимы изучения. Сделать это вы можете перейдя во вкладку \"Режимы\"");
        else
            infoText.setText("Произошла ошибка! Это фрагмент не должен был открываться.");
        return v;
    }
}