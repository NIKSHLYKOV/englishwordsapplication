package ru.nikshlykov.englishwordsapp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.DaggerFragment;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.activities.WordActivity;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener;
import ru.nikshlykov.englishwordsapp.ui.fragments.DeleteSubgroupDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.SortWordsDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.viewmodels.SubgroupViewModel;
import ru.nikshlykov.englishwordsapp.ui.adapters.WordsRecyclerViewAdapter;
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.ResetProgressDialogFragment;

public class SubgroupFragment extends DaggerFragment
        implements SortWordsDialogFragment.SortWordsListener,
        ResetProgressDialogFragment.ResetProgressListener,
        DeleteSubgroupDialogFragment.DeleteSubgroupListener {

    // TODO сделать свою view для отображения прогресса по слову.
    //  Лучше базу брать из той, которая в WordActivity.

    // TODO надо будет отсюда убрать onActivityResult (уже убрал). При этом надо учесть, что
    //  группа (название) должно обновиться. По этому надо подтягивать группу из БД
    //  через LiveData. Могут быть проблемы при удалении подгруппы, проверить этот момент.

    // Ключи для получения аргументов.
    public static final String EXTRA_SUBGROUP_OBJECT = "SubgroupObject";
    public static final String EXTRA_SUBGROUP_ID = "SubgroupId";
    public static final String EXTRA_SUBGROUP_IS_CREATED_BY_USER = "SubgroupIsCreatedByUser";
    public static final String EXTRA_SUBGROUP_IS_STUDIED = "SubgroupIsStudied";

    // TODO Проверить, что при возвращении на GroupsFragment обновляются подгруппы. Особенно
    //  при удалении текущей.
/*
    // Ключи для передачи аргументов.
    public static final String EXTRA_DELETE_SUBGROUP = "DeleteSubgroup";*/

    // Коды запросов для startActivityForResult().
    private static final int REQUEST_CODE_EDIT_EXISTING_WORD = 1;
    private static final int REQUEST_CODE_CREATE_NEW_WORD = 2;
    private static final int REQUEST_CODE_EDIT_SUBGROUP = 3;

    // Тег для логирования.
    private static final String LOG_TAG = "SubgroupFragment";

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_SORT_WORDS = "SortWordsDialogFragment";
    private static final String DIALOG_RESET_WORDS_PROGRESS = "ResetWordsProgressDialogFragment";
    private static final String DIALOG_DELETE_SUBGROUP = "DeleteSubgroupDialogFragment";
    private static final String DIALOG_LINK_OR_DELETE_WORD = "LinkOrDeleteWordDialogFragment";

    // View элементы.
    private FloatingActionButton createWordFAB;
    private Toolbar toolbar;
    private TextView infoTextView;
    private ImageView subgroupImageView;

    private RecyclerView recyclerView;
    private WordsRecyclerViewAdapter adapter;
    // Иконки для свайпа слова в recyclerView.
    private Drawable deleteIcon;
    private Drawable linkIcon;

    private SubgroupViewModel subgroupViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    private OnChildFragmentInteractionListener onChildFragmentInteractionListener;

    // TODO разобраться с этим объектом. Можно будет просто id передавать, если
    //  через LiveData будем следить, т.к. он сейчас используется для хранения до передачи в ViewModel.
    private Subgroup subgroup;
    private long subgroupId;
    private boolean subgroupIsStudied;
    private boolean subgroupIsCreatedByUser;

    private boolean deleteFlag;

    private Observer<ArrayList<Subgroup>> availableSubgroupsObserver;

    // параметр сортировки слов в подгруппе.
    private int sortParam;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subgroupViewModel = viewModelFactory.create(SubgroupViewModel.class);

        // Получаем id подгруппы из Intent.
        getBundleArguments();

        sortParam = getSortParam();

        subgroupViewModel.setLiveDataSubgroup(subgroup, sortParam);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subgroup, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим наши View.
        findViews(view);

        // Устанавливаем наш toolbar.
        ((DaggerAppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initCreateWordFAB();

        subgroupViewModel.getSubgroupMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Subgroup>() {
            @Override
            public void onChanged(Subgroup subgroup) {
                if (subgroup != null) {
                    Log.i(LOG_TAG, "subgroup onChanged()");

                    CollapsingToolbarLayout toolbarLayout = view.findViewById(
                            R.id.fragment_subgroup___collapsing_toolbar_layout);
                    toolbarLayout.setTitle(subgroup.name);
                    toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingToolbarCollapseTitle);
                    toolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsingToolbarExpandedTitle);

                    setSubgroupImage(subgroup.isCreatedByUser(), subgroup.imageURL);

                    // Создаём вещи для свайпа слов.
                    initSwipeIcons();
                    new ItemTouchHelper(createMySimpleCallbackBySubgroup(subgroup))
                            .attachToRecyclerView(recyclerView);
                }
            }
        });

        // Создаём Recycler и адаптер для него.
        initRecyclerView();
        initRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        // Закидываем данные в адаптер, подписывая его на изменения в LiveData.
        subgroupViewModel.getWords().observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                Log.i(LOG_TAG, "words onChanged()");
                if (words != null) {
                    if (!deleteFlag) {
                        // Если слов нет, то скрываем CheckBox изучения подгруппы.
                        if (words.isEmpty()) {
                            if (!subgroupIsCreatedByUser) {
                                infoTextView.setText("Здесь скоро появятся слова. Пожалуйста, подождите обновления!");
                            } else {
                                //learnSubgroupCheckBox.setVisibility(View.GONE);
                                /*Toast.makeText(SubgroupActivity.this,
                                    R.string.error_subgroup_is_empty, Toast.LENGTH_LONG)
                                    .show();*/
                            }
                        } else {
                            //learnSubgroupCheckBox.setVisibility(View.VISIBLE);
                        }
                        adapter.setWords(words);
                    }
                }
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause()");
        if (!deleteFlag) {
            subgroupViewModel.updateSubgroup();
            saveSortParam(sortParam);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy()");
    }


    // Toolbar.

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // TODO Не работает (не появляются пункты меню в тулбаре), разобраться.

        Log.d(LOG_TAG, "onCreateOptionsMenu()");
        inflater.inflate(R.menu.activity_subgroup_toolbar_menu, menu);

        if (subgroupIsStudied) {
            menu.findItem(R.id.activity_subgroup___action___learn)
                    .setChecked(true)
                    .setIcon(getContext().getDrawable(R.drawable.ic_brain_selected_yellow));
        }
        // Скрываем действия, доступные только для созданных пользователем подгрупп.
        if (!subgroupIsCreatedByUser) {
            menu.findItem(R.id.activity_subgroup___action___delete_subgroup).setVisible(false);
            menu.findItem(R.id.activity_subgroup___action___edit_subgroup).setVisible(false);
        }

        /*ToggleButton learnToggleButton = (ToggleButton) menu.findItem(R.id.activity_subgroup___action___learn)
                .getActionView();
        learnToggleButton.setLayoutParams(new Toolbar.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_width),
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_height)));
        learnToggleButton.setText("");
        learnToggleButton.setTextOn("");
        learnToggleButton.setTextOff("");
        learnToggleButton.setBackgroundDrawable(getDrawable(R.drawable.background_brain_icon));*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.activity_subgroup_toolbar_menu, menu);

        if (subgroupIsStudied) {
            menu.findItem(R.id.activity_subgroup___action___learn)
                    .setChecked(true)
                    .setIcon(getDrawable(R.drawable.ic_brain_selected_yellow));
        }
        // Скрываем действия, доступные только для созданных пользователем подгрупп.
        if (!subgroupIsCreatedByUser) {
            menu.findItem(R.id.activity_subgroup___action___delete_subgroup).setVisible(false);
            menu.findItem(R.id.activity_subgroup___action___edit_subgroup).setVisible(false);
        }

        *//*ToggleButton learnToggleButton = (ToggleButton) menu.findItem(R.id.activity_subgroup___action___learn)
                .getActionView();
        learnToggleButton.setLayoutParams(new Toolbar.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_width),
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_height)));
        learnToggleButton.setText("");
        learnToggleButton.setTextOn("");
        learnToggleButton.setTextOff("");
        learnToggleButton.setBackgroundDrawable(getDrawable(R.drawable.background_brain_icon));*//*

        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getActivity().getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.activity_subgroup___action___learn:
                if (item.isChecked()) {
                    item.setChecked(false);
                    item.setIcon(getContext().getDrawable(R.drawable.ic_brain_not_selected));
                    subgroupViewModel.setIsStudied(false);
                } else {
                    item.setChecked(true);
                    item.setIcon(getContext().getDrawable(R.drawable.ic_brain_selected_yellow));
                    subgroupViewModel.setIsStudied(true);
                }
                return true;

            // Сортировка слов по алфавиту или сложности.
            case R.id.activity_subgroup___action___sort:
                Log.d(LOG_TAG, "sort words");
                SortWordsDialogFragment sortWordsDialogFragment = new SortWordsDialogFragment();
                Bundle sortWordsDialogArguments = new Bundle();
                sortWordsDialogArguments.putInt(SortWordsDialogFragment.EXTRA_SORT_PARAM, sortParam);
                sortWordsDialogFragment.setArguments(sortWordsDialogArguments);
                sortWordsDialogFragment.show(manager, DIALOG_SORT_WORDS);
                return true;

            // Редактирование подгруппы.
            case R.id.activity_subgroup___action___edit_subgroup:
                Log.d(LOG_TAG, "edit subgroup");
                NavDirections navDirections = SubgroupFragmentDirections
                        .actionGlobalSubgroupDataFragment()
                        .setSubgroupId(subgroupId);
                onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
                return true;

            // Сбрасывание прогресса слов данной подгруппы.
            case R.id.activity_subgroup___action___reset_words_progress:
                Log.d(LOG_TAG, "Reset words progress");
                ResetProgressDialogFragment resetProgressDialogFragment =
                        new ResetProgressDialogFragment();
                Bundle resetProgressDialogArguments = new Bundle();
                resetProgressDialogArguments.putInt(ResetProgressDialogFragment.EXTRA_FLAG,
                        ResetProgressDialogFragment.FOR_SUBGROUP);
                resetProgressDialogFragment.setArguments(resetProgressDialogArguments);
                resetProgressDialogFragment.show(manager, DIALOG_RESET_WORDS_PROGRESS);
                return true;

            // Удаление подгруппы.
            case R.id.activity_subgroup___action___delete_subgroup:
                Log.d(LOG_TAG, "Delete subgroup");
                DeleteSubgroupDialogFragment deleteSubgroupDialogFragment = new DeleteSubgroupDialogFragment();
                deleteSubgroupDialogFragment.show(manager, DIALOG_DELETE_SUBGROUP);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSubgroupImage(boolean subgroupIsCreatedByUser, String imageResourceId) {
        if (subgroupIsCreatedByUser) {
            Drawable imageColor = getContext().getDrawable(R.drawable.user_subgroups_default_color);
            subgroupImageView.setImageDrawable(imageColor);
        } else {
            Glide.with(this)
                    .load(GroupsRepository.PATH_TO_HIGH_SUBGROUP_IMAGES + imageResourceId)
                    .placeholder(R.drawable.shape_load_picture)
                    .error(Glide.with(this).load(GroupsRepository.PATH_TO_SUBGROUP_IMAGES + imageResourceId))
                    .into(subgroupImageView);
        }
    }


    // Параметр сортировки слова.

    /**
     * Обрабатывает результат работы SortWordsDialogFragment.
     *
     * @param sortParam параметр сортировки, полученный из фрагмента.
     */
    @Override
    public void sort(int sortParam) {
        Log.i(LOG_TAG, "sortParam from SortWordsDialogFragment = " + sortParam);
        // Проверяем, изменился ли вообще параметр, чтобы не делать лишней работы.
        if (this.sortParam != sortParam) {
            // Устанавливаем новый параметр сортировки.
            this.sortParam = sortParam;
            // Сообщаем модели о том, что необходимо отсортировать слова.
            subgroupViewModel.sortWords(sortParam);
        }
    }

    /**
     * Достаёт параметр сортировки слов в подгруппе.
     *
     * @return параметр сортировки.
     */
    private int getSortParam() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        return Integer.parseInt(sharedPreferences.getString(getString(R.string.preference_key___sort_words_in_subgroups),
                String.valueOf(SortWordsDialogFragment.BY_ALPHABET)));
    }

    /**
     * Сохраняет параметр сортировки слов в подгруппе.
     *
     * @param sortParam последний выставленный параметр сортировки.
     */
    private void saveSortParam(int sortParam) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(getString(R.string.preference_key___sort_words_in_subgroups), String.valueOf(sortParam));
        editor.apply();
    }


    // Создание Activity и View.

    /**
     * Получает из Extras id подгруппы и флаг того, создана ли она пользователем.
     */
    private void getBundleArguments() {
        if (getArguments() != null) {
            subgroup = SubgroupFragmentArgs.fromBundle(getArguments()).getSubgroup();
            subgroupIsCreatedByUser = subgroup.isCreatedByUser();
            subgroupId = subgroup.id;
            subgroupIsStudied = subgroup.isStudied == 1;
        } else {
            NavDirections navDirections = SubgroupFragmentDirections.actionSubgroupFragmentToGroupsDest();
            onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
        }
    }

    /**
     * Находит View в разметке.
     */
    private void findViews(View view) {
        createWordFAB = view.findViewById(R.id.fragment_subgroup___floating_action_button___new_word);
        toolbar = view.findViewById(R.id.fragment_subgroup___toolbar);
        recyclerView = view.findViewById(R.id.fragment_subgroup___recycler_view___words);
        infoTextView = view.findViewById(R.id.fragment_subgroup___text_view___info);
        subgroupImageView = view.findViewById(R.id.fragment_subgroup___image_view___subgroup_image);
    }

    private void initCreateWordFAB() {
        // Проверяем, что подгруппа создана пользователем.
        if (subgroupIsCreatedByUser) {
            // Присваиваем обработчик кнопке для создания нового слова.
            createWordFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent createNewWordIntent = new Intent(getContext(),
                            WordActivity.class);
                    createNewWordIntent.putExtra(EXTRA_SUBGROUP_ID, subgroupId);
                    createNewWordIntent.putExtra(WordActivity.EXTRA_START_TO, WordActivity.START_TO_CREATE_WORD);
                    startActivityForResult(createNewWordIntent, REQUEST_CODE_CREATE_NEW_WORD);*/
                    // TODO Скорее всего, будем разделять WordFragment на два: один фрагмент
                    //  добавляет новое слово, другой - редактирует существующее слово. Тогда
                    //  и уберём этот код.
                    NavDirections navDirections = SubgroupFragmentDirections
                            .actionSubgroupFragmentToWordFragment(new Word("a", "a", "a"))
                            .setStartTo(WordFragment.START_TO_CREATE_WORD);
                    onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
                }
            });
        } else {
            // Скрываем fab для создания нового слова.
            createWordFAB.setClickable(false);
            createWordFAB.setVisibility(View.GONE);
        }
    }


    // RecyclerView.

    private void initRecyclerView() {
        // Создаём вспомогательные вещи для RecyclerView и соединяем их с ним.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        if (subgroupIsCreatedByUser) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        createWordFAB.hide();
                    } else if (dy < 0) {
                        createWordFAB.show();
                    }
                }
            });
        }
    }

    private void initRecyclerViewAdapter() {
        // Создаём adapter для RecyclerView.
        adapter = new WordsRecyclerViewAdapter(getContext());

        // Присваиваем обработчик нажатия на элемент в RecyclerView (слово).
        adapter.setOnEntryClickListener(new WordsRecyclerViewAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                final Word currentWord = adapter.getWords().get(position);
                /*Intent editExistingWordIntent = new Intent(getContext(),
                        WordActivity.class);
                editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_OBJECT, currentWord);
                editExistingWordIntent.putExtra(WordActivity.EXTRA_START_TO, WordActivity.START_TO_EDIT_WORD);
                //editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_ID, currentWord.id);
                startActivityForResult(editExistingWordIntent, REQUEST_CODE_EDIT_EXISTING_WORD);*/
                NavDirections navDirections = SubgroupFragmentDirections
                        .actionSubgroupFragmentToWordFragment(currentWord)
                        .setStartTo(WordFragment.START_TO_EDIT_WORD);
                onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
            }
        });
    }

    private void initSwipeIcons() {
        // Находим иконки, изображаемые при свайпе.
        linkIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_link_white_24dp);
        deleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_white_24dp);
    }

    public MySimpleCallback createMySimpleCallbackBySubgroup(Subgroup subgroup) {
        if (subgroup.isCreatedByUser()) {
            return new MySimpleCallback(0, ItemTouchHelper.LEFT
                    | ItemTouchHelper.RIGHT);
        } else {
            return new MySimpleCallback(0, ItemTouchHelper.RIGHT);
        }
    }

    private class MySimpleCallback extends ItemTouchHelper.SimpleCallback {

        public MySimpleCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final long wordId = adapter.getWordAt(viewHolder.getAdapterPosition()).id;
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    subgroupViewModel.deleteLinkWithSubgroup(wordId);
                    Snackbar.make(viewHolder.itemView, R.string.word_deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.to_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    subgroupViewModel.insertLinkWithSubgroup(wordId);
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    setAvailableSubgroupsObserver(wordId);
                    subgroupViewModel.getAvailableSubgroupsToLink(wordId).observe(
                            getViewLifecycleOwner(), availableSubgroupsObserver);

                    adapter.notifyDataSetChanged();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState,
                                boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            ColorDrawable swipeBackground = new ColorDrawable();

            int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int linkIconMargin = (itemView.getHeight() - linkIcon.getIntrinsicHeight()) / 2;

            if (dX > 0) {
                swipeBackground.setColor(ContextCompat.getColor(getContext(),
                        R.color.swipe_add_link_word));
                swipeBackground.setColor(
                        Color.parseColor("#C6FF00"));
                swipeBackground.setBounds(
                        itemView.getLeft(),
                        itemView.getTop(),
                        (int) dX,
                        itemView.getBottom());
            } else {
                swipeBackground.setColor(ContextCompat.getColor(getContext(),
                        R.color.swipe_delete_link_word));
                swipeBackground.setBounds(
                        itemView.getRight() + (int) dX,
                        itemView.getTop(),
                        itemView.getRight(),
                        itemView.getBottom());
            }
            swipeBackground.draw(c);

            if (dX > 0) {
                linkIcon.setBounds(
                        itemView.getLeft() + linkIconMargin,
                        itemView.getTop() + linkIconMargin,
                        itemView.getLeft() + linkIconMargin + linkIcon.getIntrinsicWidth(),
                        itemView.getBottom() - linkIconMargin);
                linkIcon.draw(c);
            } else {
                deleteIcon.setBounds(
                        itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth(),
                        itemView.getTop() + deleteIconMargin,
                        itemView.getRight() - deleteIconMargin,
                        itemView.getBottom() - deleteIconMargin);
                deleteIcon.draw(c);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private void setAvailableSubgroupsObserver(final long wordId) {
        availableSubgroupsObserver = new Observer<ArrayList<Subgroup>>() {
            @Override
            public void onChanged(ArrayList<Subgroup> subgroups) {
                Log.d(LOG_TAG, "availableSubgroups onChanged()");
                if (subgroups != null) {
                    Log.d(LOG_TAG, "availableSubgroups onChanged() value != null");
                    LinkOrDeleteWordDialogFragment linkOrDeleteWordDialogFragment =
                            new LinkOrDeleteWordDialogFragment();
                    Bundle arguments = new Bundle();

                    arguments.putLong(LinkOrDeleteWordDialogFragment.EXTRA_WORD_ID,
                            wordId);

                    arguments.putInt(LinkOrDeleteWordDialogFragment.EXTRA_FLAG,
                            LinkOrDeleteWordDialogFragment.TO_LINK);

                    long[] subgroupsIds = new long[subgroups.size()];
                    String[] subgroupsNames = new String[subgroups.size()];
                    for (int i = 0; i < subgroups.size(); i++) {
                        Subgroup subgroup = subgroups.get(i);
                        subgroupsNames[i] = subgroup.name;
                        subgroupsIds[i] = subgroup.id;
                    }
                    arguments.putStringArray(LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_NAMES,
                            subgroupsNames);
                    arguments.putLongArray(LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_IDS,
                            subgroupsIds);

                    linkOrDeleteWordDialogFragment.setArguments(arguments);

                    linkOrDeleteWordDialogFragment.show(getActivity().getSupportFragmentManager(), DIALOG_LINK_OR_DELETE_WORD);

                    subgroupViewModel.clearAvailableSubgroupsToAndRemoveObserver(availableSubgroupsObserver);
                } else {
                    Log.d(LOG_TAG, "availableSubgroups onChanged() value = null");
                }
            }
        };
    }


    // Диалоги.

    /**
     * Принимает сообщение от ResetWordProgressDialogFragment.
     *
     * @param message представляет из себя сообщение.
     */
    @Override
    public void resetMessage(String message) {
        if (message.equals(ResetProgressDialogFragment.RESET_MESSAGE)) {
            subgroupViewModel.resetWordsProgress();
        }
    }

    /**
     * Принимает сообщение от DeleteSubgroupDialogFragment.
     *
     * @param message удалять подгруппу или нет.
     */
    @Override
    public void deleteMessage(String message) {
        if (message.equals(DeleteSubgroupDialogFragment.DELETE_MESSAGE)) {
            Subgroup currentSubgroup = subgroupViewModel.getSubgroupMutableLiveData().getValue();
            if (currentSubgroup != null) {
                if (currentSubgroup.isCreatedByUser()) {
                    subgroupViewModel.deleteSubgroup();
                    deleteFlag = true;

                    NavDirections navDirections = SubgroupFragmentDirections.actionSubgroupFragmentToGroupsDest();
                    onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
                }
            }
        }
    }
}
