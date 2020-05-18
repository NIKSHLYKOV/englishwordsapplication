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
import android.widget.Toast;

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
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikshlykov.englishwordsapp.MyApplication;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.main.MainActivity;
import ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.word.ResetProgressDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.word.WordActivity;

public class SubgroupActivity extends AppCompatActivity
        implements SortWordsDialogFragment.SortWordsListener,
        ResetProgressDialogFragment.ResetProgressListener,
        DeleteSubgroupDialogFragment.DeleteSubgroupListener {

    // Ключи для получения аргументов.
    public static final String EXTRA_SUBGROUP_ID = "SubgroupId";
    public static final String EXTRA_IS_CREATED_BY_USER = "IsCreatedByUser";

    // Ключ для получения параметра сортировки слов.
    public static final String PREFERENCE_SORT_WORDS_IN_SUBGROUP = "SortWordsInSubgroup";

    // Возможные ответные коды из WordActivity.
    private static final int REQUEST_CODE_EDIT_EXISTING_WORD = 1;
    private static final int REQUEST_CODE_CREATE_NEW_WORD = 2;
    private static final int REQUEST_CODE_EDIT_SUBGROUP = 3;

    // Тег для логирования.
    private static final String LOG_TAG = "SubgroupActivity";

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_SORT_WORDS = "SortWordsDialogFragment";
    private static final String DIALOG_RESET_WORDS_PROGRESS = "ResetWordsProgressDialogFragment";
    private static final String DIALOG_DELETE_SUBGROUP = "DeleteSubgroupDialogFragment";
    private static final String DIALOG_LINK_WORD = "LinkWordDialogFragment";

    // View элементы.
    private FloatingActionButton createWordFloatingActionButton;
    //private CheckBox learnSubgroupCheckBox;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private WordsRecyclerViewAdapter adapter;

    // Иконки для свайпа слова в recyclerView.
    private Drawable deleteIcon;
    private Drawable linkIcon;

    private long subgroupId;
    private boolean subgroupIsCreatedByUser;
    private boolean deleteFlag;
    private SubgroupViewModel subgroupViewModel;

    private Observer<ArrayList<Subgroup>> availableSubgroupsObserver;

    private int sortParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subgroup);

        // Получаем id подгруппы из Intent.
        getBundleArguments();

        // Находим View элементы из разметки.
        findViews();

        // Устанавливаем наш toolbar.
        setSupportActionBar(toolbar);

        sortParam = getSortParam();

        // Создаём для Activity ViewModel.
        subgroupViewModel = new ViewModelProvider(this).get(SubgroupViewModel.class);

        subgroupViewModel.setLiveDataSubgroup(subgroupId, sortParam);
        subgroupViewModel.getLiveDataSubgroup().observe(this, new Observer<Subgroup>() {
            @Override
            public void onChanged(Subgroup subgroup) {
                if (subgroup != null) {
                    Log.i(LOG_TAG, "subgroup onChanged()");

                    CollapsingToolbarLayout toolbarLayout = findViewById(
                            R.id.activity_subgroup___collapsing_toolbar_layout);
                    toolbarLayout.setTitle(subgroup.name);
                    toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingToolbarCollapseTitle);
                    toolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsingToolbarExpandedTitle);

                    setSubgroupImage(subgroup.imageResourceId);

                    initCreateWordFAB(subgroup);

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
                            //learnSubgroupCheckBox.setVisibility(View.GONE);
                            Toast.makeText(SubgroupActivity.this,
                                    R.string.error_subgroup_is_empty, Toast.LENGTH_LONG)
                                    .show(); // Пуста может быть только подгруппа созданная пользователем.
                        } else {
                            //learnSubgroupCheckBox.setVisibility(View.VISIBLE);
                        }
                        adapter.setWords(words);
                    }
                }
            }
        });
    }

    private void setSubgroupImage(String imageResourceId) {
        Glide.with(this)
                .load(AppRepository.PATH_TO_HIGH_SUBGROUP_IMAGES + imageResourceId)
                .placeholder(R.drawable.shape_load_picture)
                .error(Glide.with(this).load(AppRepository.PATH_TO_SUBGROUP_IMAGES + imageResourceId))
                .into((ImageView)findViewById(R.id.activity_subgroup___image_view___subgroup_image));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!deleteFlag) {
            subgroupViewModel.updateSubgroup();
            saveSortParam(sortParam);
        }
    }

    /**
     * Метод обрабатывает результат работы WordActivity, которое может создавать новое слово
     * или редактировать уже существующее.
     *
     * @param requestCode код запроса.
     * @param resultCode код ответа.
     * @param data        содержит данные о слове. В любом случае там будут непустые слово (Word) и
     *                    значения (Value), а также транскрипция (Transcription), которая может быть пустой.
     *                    Дополнительно параметр может содержать id слова (WordId), если мы изменили
     *                    существующее слово.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String word = data.getStringExtra(WordActivity.EXTRA_WORD);
            String transcription = data.getStringExtra(WordActivity.EXTRA_TRANSCRIPTION);
            String value = data.getStringExtra(WordActivity.EXTRA_VALUE);

            switch (requestCode){
                // Создание нового слова.
                case REQUEST_CODE_CREATE_NEW_WORD:
                    final Word newWord = new Word(word, transcription, value);
                    subgroupViewModel.insert(newWord);
                    break;

                // Редактирование существующего слова.
                case REQUEST_CODE_EDIT_EXISTING_WORD:
                    long wordId = data.getLongExtra(WordActivity.EXTRA_WORD_ID, 0);
                    if (wordId != 0) {
                        subgroupViewModel.updateWord(wordId, word, value, transcription);
                    }
                    break;

                // Редактирование существующего слова.
                case REQUEST_CODE_EDIT_SUBGROUP:
                    String newSubgroupName = data.getStringExtra(AddOrEditSubgroupActivity.EXTRA_SUBGROUP_NAME);
                    subgroupViewModel.updateSubgroupName(newSubgroupName);
                    break;
            }
        }
    }

    /**
     * Методы по работе с Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.activity_subgroup_toolbar_menu, menu);
        MenuItem deleteSubgroupItem = menu.findItem(R.id.activity_subgroup___action___delete_subgroup);
        MenuItem editSubgroupItem = menu.findItem(R.id.activity_subgroup___action___edit_subgroup);
        if (!subgroupIsCreatedByUser) {
            deleteSubgroupItem.setVisible(false);
            editSubgroupItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();

        switch (item.getItemId()) {
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
                        .getLiveDataSubgroup().getValue().name);
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

    /**
     * Обрабатывает результат работы SortWordsDialogFragment.
     * @param sortParam параметр сортировки, полученный из фрагмента.
     */
    @Override
    public void sort(int sortParam) {
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
     * @return параметр сортировки.
     */
    private int getSortParam() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(MyApplication.PREFERENCE_FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(PREFERENCE_SORT_WORDS_IN_SUBGROUP,
                SortWordsDialogFragment.BY_PROGRESS);
    }

    /**
     * Сохраняет параметр сортировки слов в подгруппе.
     * @param sortParam последний выставленный параметр сортировки.
     */
    private void saveSortParam(int sortParam){
        SharedPreferences.Editor editor =
                getSharedPreferences(MyApplication.PREFERENCE_FILE_NAME, MODE_PRIVATE).edit();
        editor.putInt(PREFERENCE_SORT_WORDS_IN_SUBGROUP, sortParam);
        editor.apply();
    }


    /**
     * Получает из Extras id подгруппы и флаг того, создана ли она пользователем.
     */
    private void getBundleArguments() {
        Bundle arguments = getIntent().getExtras();
        if (arguments == null)
            finish();
        try {
            subgroupId = arguments.getLong(EXTRA_SUBGROUP_ID);
            subgroupIsCreatedByUser = arguments.getBoolean(EXTRA_IS_CREATED_BY_USER);
        } catch (NullPointerException ex) {
            finish();
        }
    }

    private void findViews() {
        createWordFloatingActionButton = findViewById(R.id.activity_subgroup___floating_action_button___new_word);
        toolbar = findViewById(R.id.activity_subgroup___toolbar);
        recyclerView = findViewById(R.id.activity_subgroup___recycler_view___words);
    }

    private void setAvailableSubgroupsObserver(final long wordId){
        availableSubgroupsObserver = new Observer<ArrayList<Subgroup>>() {
            @Override
            public void onChanged(ArrayList<Subgroup> subgroups) {
                Log.d(LOG_TAG, "availableSubgroups onChanged()");
                if (subgroups != null){
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
                    for (int i = 0; i < subgroups.size(); i++){
                        Subgroup subgroup = subgroups.get(i);
                        subgroupsNames[i] = subgroup.name;
                        subgroupsIds[i] = subgroup.id;
                    }
                    arguments.putStringArray(LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_NAMES,
                            subgroupsNames);
                    arguments.putLongArray(LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_IDS,
                            subgroupsIds);

                    linkOrDeleteWordDialogFragment.setArguments(arguments);

                    linkOrDeleteWordDialogFragment.show(getSupportFragmentManager(), "some tag");

                    subgroupViewModel.clearAvailableSubgroupsToAndRemoveObserver(availableSubgroupsObserver);
                }
                else{
                    Log.d(LOG_TAG, "availableSubgroups onChanged() value = null");
                }
            }
        };
    }

    private void initCreateWordFAB(Subgroup subgroup) {
        // Проверяем, что подгруппа создана пользователем.
        if (subgroup.isCreatedByUser()) {
            // Присваиваем обработчик кнопке для создания нового слова.
            createWordFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNewWordIntent = new Intent(getApplicationContext(),
                            WordActivity.class);
                    createNewWordIntent.putExtra(EXTRA_SUBGROUP_ID, subgroupId);
                    startActivityForResult(createNewWordIntent, REQUEST_CODE_CREATE_NEW_WORD);
                }
            });
        } else {
            // Скрываем fab для создания нового слова.
            createWordFloatingActionButton.setClickable(false);
            createWordFloatingActionButton.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView() {
        // Создаём вспомогательные вещи для RecyclerView и соединяем их с ним.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SubgroupActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SubgroupActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
                editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_ID, currentWord.id);
                startActivityForResult(editExistingWordIntent, REQUEST_CODE_EDIT_EXISTING_WORD);
            }
        });
    }

    private void initSwipeIcons() {
        // Находим иконки, изображаемые при свайпе.
        linkIcon = ContextCompat.getDrawable(this, R.drawable.ic_link_white_24dp);
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp);
    }


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

    @Override
    public void deleteMessage(String message) {
        if (message.equals(DeleteSubgroupDialogFragment.DELETE_MESSAGE)) {
            Subgroup currentSubgroup = subgroupViewModel.getLiveDataSubgroup().getValue();
            if (currentSubgroup != null) {
                if (currentSubgroup.isCreatedByUser()) {
                    subgroupViewModel.deleteSubgroup();
                    deleteFlag = true;
                    finish();
                    // Тут ещё необходимо удалить все линки с данной подгруппой.
                    // Тут ещё необходимо удалить все линки с данной подгруппой.
                    // Тут ещё необходимо удалить все линки с данной подгруппой.
                    // Тут ещё необходимо удалить все линки с данной подгруппой.
                    // Тут ещё необходимо удалить все линки с данной подгруппой.
                }
            }
        }
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
}
