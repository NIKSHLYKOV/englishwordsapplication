package ru.nikshlykov.englishwordsapp.ui.flowfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import ru.nikshlykov.englishwordsapp.R;

public class GroupsAndWordsFlowFragment extends Fragment implements OnChildFragmentInteractionListener {

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.flow_fragment_groups_and_words, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NavHostFragment navHostFragment =
                (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.flow_fragment_groups_and_words___nav_host);
        navController = navHostFragment.getNavController();
    }

    @Override
    public void onChildFragmentInteraction(NavDirections navDirections) {
        switch (navDirections.getActionId()) {
            // TODO Привести id к одному виду.
            case R.id.action_global_subgroupDataFragment:
            case R.id.action_groups_dest_to_subgroupFragment:
            case R.id.action_subgroupFragment_to_wordFragment:
                navController.navigate(navDirections);
                break;
            case R.id.action_subgroupDataFragment_to_groups_dest:
            case R.id.action_subgroupFragment_to_groups_dest:
            case R.id.action_wordFragment_to_subgroupFragment:
                navController.popBackStack();
                break;
        }
    }

    // TODO перехватывать нажатие кнопки назад, если на экране не GroupsFragment. Иначе передавать
    //  нажатие обратно в MainActivity.
}
