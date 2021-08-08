package ru.nikshlykov.englishwordsapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener;
import ru.nikshlykov.englishwordsapp.ui.viewmodels.SubgroupDataViewModel;

public class SubgroupDataFragment extends DaggerFragment {
    // TODO сделать colorPicker/iconPicker для фона подгруппы.

    // View элементы.
    private MaterialButton confirmButton;
    private TextInputEditText subgroupNameEditText;

    private long subgroupId = 0L;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    private SubgroupDataViewModel subgroupDataViewModel;

    private OnChildFragmentInteractionListener onChildFragmentInteractionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getParentFragment().getParentFragment() instanceof OnChildFragmentInteractionListener) {
            onChildFragmentInteractionListener =
                    (OnChildFragmentInteractionListener) getParentFragment().getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().getParentFragment().toString() + " must implement OnChildFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subgroupDataViewModel = viewModelFactory.create(SubgroupDataViewModel.class);

        Bundle extras = getArguments();
        if (extras != null) {
            subgroupId = SubgroupDataFragmentArgs.fromBundle(extras).getSubgroupId();
            if (subgroupId != 0L) {
                subgroupDataViewModel.setSubgroup(subgroupId);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subgroup_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);

        if (subgroupId != 0L) {
            confirmButton.setText(R.string.to_save);
        }
        setConfirmButtonClickListener();

        subgroupDataViewModel.getSubgroup().observe(getViewLifecycleOwner(), new Observer<Subgroup>() {
            @Override
            public void onChanged(Subgroup subgroup) {
                if (subgroup != null) {
                    subgroupNameEditText.setText(subgroup.name);
                }
            }
        });
    }

    private void setConfirmButtonClickListener() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subgroupName = SubgroupDataFragment.this.subgroupNameEditText.getText().toString().trim();
                // Проверяем, что поле названия группы не пустое.
                if (!subgroupName.isEmpty()) {
                    subgroupDataViewModel.getSubgroupIsInsertedOrUpdated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean subgroupIsInserted) {
                            if (subgroupIsInserted) {
                                Toast.makeText(getContext(), "Группа сохранена", Toast.LENGTH_SHORT).show();
                                // TODO есть проблема в том, что мы используем NavDirections для
                                //  popBackStack(), но, как я понимаю, можно это просто делать по
                                //  нажатию на кнопку назад в FlowFragment, который будет это перехватывать.
                                //  И 'нажимать' её в коде, когда нам нужно после каких-то действий
                                //  переместиться назад.
                                NavDirections navDirections = SubgroupDataFragmentDirections.actionSubgroupDataDestToGroupsDest();
                                onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
                            }
                        }
                    });
                    subgroupDataViewModel.insertOrUpdateSubgroup(subgroupName);
                }
                // Выводим Toast о том, что поле названия не должно быть пустым.
                else {
                    Toast.makeText(getContext(), R.string.error_new_subgroup_empty_name,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void findViews(View view) {
        subgroupNameEditText = view.findViewById(R.id.fragment_subgroup_data___text_input_edit_text___subgroup_name);
        confirmButton = view.findViewById(R.id.fragment_subgroup_data___material_button___confirm_group);
    }
}
