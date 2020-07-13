package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.word.ResetProgressDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.word.WordActivity;

public class SubgroupActivity extends AppCompatActivity
        implements SortWordsDialogFragment.SortWordsListener,
        ResetProgressDialogFragment.ResetProgressListener,
        DeleteSubgroupDialogFragment.DeleteSubgroupListener {

    // TODO сделать свою view для отображения прогресса по слову.
    //  Лучше базу брать из той, которая в WordActivity.

    // Ключи для получения аргументов.
    public static final String EXTRA_SUBGROUP_OBJECT = "SubgroupObject";
    public static final String EXTRA_SUBGROUP_ID = "SubgroupId";
    public static final String EXTRA_SUBGROUP_IS_CREATED_BY_USER = "SubgroupIsCreatedByUser";
    public static final String EXTRA_SUBGROUP_IS_STUDIED = "SubgroupIsStudied";

    // Ключи для передачи аргументов.
    public static final String EXTRA_DELETE_SUBGROUP = "DeleteSubgroup";

    // Коды запросов для startActivityForResult().
    private static final int REQUEST_CODE_EDIT_EXISTING_WORD = 1;
    private static final int REQUEST_CODE_CREATE_NEW_WORD = 2;
    private static final int REQUEST_CODE_EDIT_SUBGROUP = 3;

    // Тег для логирования.
    private static final String LOG_TAG = "SubgroupActivity";

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_SORT_WORDS = "SortWordsDialogFragment";
    private static final String DIALOG_RESET_WORDS_PROGRESS = "ResetWordsProgressDialogFragment";
    private static final String DIALOG_DELETE_SUBGROUP = "DeleteSubgroupDialogFragment";
    private static final String DIALOG_LINK_OR_DELETE_WORD = "LinkOrDeleteWordDialogFragment";

    // View элементы.
    private FloatingActionButton createWordFAB;
    private Toolbar toolbar;
    private TextView infoTextView;

    private RecyclerView recyclerView;
    private WordsRecyclerViewAdapter adapter;
    // Иконки для свайпа слова в recyclerView.
    private Drawable deleteIcon;
    private Drawable linkIcon;

    private SubgroupViewModel subgroupViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    private Subgroup subgroup;
    // TODO убрать всякие id и т.д.
    private long subgroupId;
    private boolean subgroupIsStudied;
    private boolean subgroupIsCreatedByUser;

    private boolean deleteFlag;

    private Observer<ArrayList<Subgroup>> availableSubgroupsObserver;

    // параметр сортировки слов в подгруппе.
    private int sortParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App)getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        subgroupViewModel = viewModelFactory.create(SubgroupViewModel.class);

        setContentView(R.layout.activity_subgroup);

        // Получаем id подгруппы из Intent.
        getBundleArguments();

        // Находим View элементы из разметки.
        findViews();

        // Устанавливаем наш toolbar.
        setSupportActionBar(toolbar);

        sortParam = getSortParam();

        initCreateWordFAB();

        // TODO сделать нормально сразу из arguments.
        subgroupViewModel.setLiveDataSubgroup(subgroup, sortParam);

        subgroupViewModel.getSubgroupMutableLiveData().observe(this, new Observer<Subgroup>() {
            @Override
            public void onChanged(Subgroup subgroup) {
                if (subgroup != null) {
                    Log.i(LOG_TAG, "subgroup onChanged()");

                    CollapsingToolbarLayout toolbarLayout = findViewById(
                            R.id.activity_subgroup___collapsing_toolbar_layout);
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
        subgroupViewModel.getWords().observe(this, new Observer<List<Word>>() {
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
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause()");
        if (!deleteFlag) {
            subgroupViewModel.updateSubgroup();
            saveSortParam(sortParam);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onPause()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String word = data.getStringExtra(WordActivity.EXTRA_WORD);
            String transcription = data.getStringExtra(WordActivity.EXTRA_TRANSCRIPTION);
            String value = data.getStringExtra(WordActivity.EXTRA_VALUE);

            Word wordObj = data.getParcelableExtra(WordActivity.EXTRA_WORD_OBJECT);

            switch (requestCode) {
                // Создание нового слова.
                case REQUEST_CODE_CREATE_NEW_WORD:
                    final Word newWord = new Word(word, transcription, value);
                    subgroupViewModel.insert(newWord);
                    break;

                // Редактирование существующего слова.
                case REQUEST_CODE_EDIT_EXISTING_WORD:
                    long wordId = data.getLongExtra(WordActivity.EXTRA_WORD_ID, 0);
                    if (wordId < 0) {
                        subgroupViewModel.updateWord(wordId, word, value, transcription);
                    }
                    break;

                // Редактирование подгруппы.
                case REQUEST_CODE_EDIT_SUBGROUP:
                    String newSubgroupName = data.getStringExtra(AddOrEditSubgroupActivity.EXTRA_SUBGROUP_NAME);
                    subgroupViewModel.setSubgroupName(newSubgroupName);
                    break;
            }
        }
    }


    // Toolbar.

    @Override
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

        /*ToggleButton learnToggleButton = (ToggleButton) menu.findItem(R.id.activity_subgroup___action___learn)
                .getActionView();
        learnToggleButton.setLayoutParams(new Toolbar.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_width),
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_height)));
        learnToggleButton.setText("");
        learnToggleButton.setTextOn("");
        learnToggleButton.setTextOff("");
        learnToggleButton.setBackgroundDrawable(getDrawable(R.drawable.background_brain_icon));*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.activity_subgroup___action___learn:
                if (item.isChecked()) {
                    item.setChecked(false);
                    item.setIcon(getDrawable(R.drawable.ic_brain_not_selected));
                    subgroupViewModel.setIsStudied(false);
                } else {
                    item.setChecked(true);
                    item.setIcon(getDrawable(R.drawable.ic_brain_selected_yellow));
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
                Intent intent = new Intent(this, AddOrEditSubgroupActivity.class);
                intent.putExtra(AddOrEditSubgroupActivity.EXTRA_SUBGROUP_NAME, subgroupViewModel
                        .getSubgroupMutableLiveData().getValue().name);
                startActivityForResult(intent, REQUEST_CODE_EDIT_SUBGROUP);
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
        ImageView subgroupImageView = findViewById(R.id.activity_subgroup___image_view___subgroup_image);
        if (subgroupIsCreatedByUser) {
            Drawable imageColor = getDrawable(R.drawable.user_subgroups_default_color);
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
                PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(sharedPreferences.getString(getString(R.string.preference_key___sort_words_in_subgroups),
                String.valueOf(SortWordsDialogFragment.BY_ALPHABET)));
    }

    /**
     * Сохраняет параметр сортировки слов в подгруппе.
     *
     * @param sortParam последний выставленный параметр сортировки.
     */
    private void saveSortParam(int sortParam) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(getString(R.string.preference_key___sort_words_in_subgroups), String.valueOf(sortParam));
        editor.apply();
    }


    // Создание Activity и View.

    /**
     * Получает из Extras id подгруппы и флаг того, создана ли она пользователем.
     */
    private void getBundleArguments() {
        Bundle arguments = getIntent().getExtras();
        try {
            subgroup = arguments.getParcelable(SubgroupActivity.EXTRA_SUBGROUP_OBJECT);
            if (subgroup != null) {
                subgroupIsCreatedByUser = subgroup.isCreatedByUser();
                subgroupId = subgroup.id;
                subgroupIsStudied = subgroup.isStudied == 1;
            } else {
                finish();
            }
            /*subgroupId = arguments.getLong(EXTRA_SUBGROUP_ID);
            subgroupIsCreatedByUser = arguments.getBoolean(EXTRA_SUBGROUP_IS_CREATED_BY_USER);
            subgroupIsStudied = arguments.getBoolean(EXTRA_SUBGROUP_IS_STUDIED);*/

        } catch (NullPointerException ex) {
            finish();
        }
    }

    /**
     * Находит View в разметке.
     */
    private void findViews() {
        createWordFAB = findViewById(R.id.activity_subgroup___floating_action_button___new_word);
        toolbar = findViewById(R.id.activity_subgroup___toolbar);
        recyclerView = findViewById(R.id.activity_subgroup___recycler_view___words);
        infoTextView = findViewById(R.id.activity_subgroup___text_view___info);
    }

    private void initCreateWordFAB() {
        // Проверяем, что подгруппа создана пользователем.
        if (subgroupIsCreatedByUser) {
            // Присваиваем обработчик кнопке для создания нового слова.
            createWordFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNewWordIntent = new Intent(getApplicationContext(),
                            WordActivity.class);
                    createNewWordIntent.putExtra(EXTRA_SUBGROUP_ID, subgroupId);
                    createNewWordIntent.putExtra(WordActivity.EXTRA_START_TO, WordActivity.START_TO_CREATE_WORD);
                    startActivityForResult(createNewWordIntent, REQUEST_CODE_CREATE_NEW_WORD);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(SubgroupActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(SubgroupActivity.this,
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
        adapter = new WordsRecyclerViewAdapter(SubgroupActivity.this);

        // Присваиваем обработчик нажатия на элемент в RecyclerView (слово).
        adapter.setOnEntryClickListener(new WordsRecyclerViewAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                final Word currentWord = adapter.getWords().get(position);
                Intent editExistingWordIntent = new Intent(SubgroupActivity.this,
                        WordActivity.class);
                editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_OBJECT, currentWord);
                editExistingWordIntent.putExtra(WordActivity.EXTRA_START_TO, WordActivity.START_TO_EDIT_WORD);
                //editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_ID, currentWord.id);
                startActivityForResult(editExistingWordIntent, REQUEST_CODE_EDIT_EXISTING_WORD);
            }
        });
    }

    private void initSwipeIcons() {
        // Находим иконки, изображаемые при свайпе.
        linkIcon = ContextCompat.getDrawable(this, R.drawable.ic_link_white_24dp);
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp);
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
                            SubgroupActivity.this, availableSubgroupsObserver);

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
                swipeBackground.setColor(ContextCompat.getColor(SubgroupActivity.this,
                        R.color.swipe_add_link_word));
                swipeBackground.setColor(
                        Color.parseColor("#C6FF00"));
                swipeBackground.setBounds(
                        itemView.getLeft(),
                        itemView.getTop(),
                        (int) dX,
                        itemView.getBottom());
            } else {
                swipeBackground.setColor(ContextCompat.getColor(SubgroupActivity.this,
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

                    linkOrDeleteWordDialogFragment.show(getSupportFragmentManager(), DIALOG_LINK_OR_DELETE_WORD);

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

                    Intent wordData = new Intent();
                    wordData.putExtra(EXTRA_DELETE_SUBGROUP, true);
                    setResult(RESULT_OK, wordData);

                    finish();
                }
            }
        }
    }
}
