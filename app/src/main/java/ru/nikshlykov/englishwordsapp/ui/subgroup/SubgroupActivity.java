package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;
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

    // Возможные ответные коды из WordActivity.
    private static final int REQUEST_CODE_EDIT_EXISTING_WORD = 1;
    private static final int REQUEST_CODE_CREATE_NEW_WORD = 2;

    // Тег для логирования.
    private static final String LOG_TAG = "SubgroupActivity";

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_SORT_WORDS = "SortWordsDialogFragment";
    private static final String DIALOG_RESET_WORDS_PROGRESS = "ResetWordsProgressDialogFragment";
    private static final String DIALOG_DELETE_SUBGROUP = "DeleteSubgroupDialogFragment";
    private static final String DIALOG_LINK_WORD = "LinkWordDialogFragment";

    // View элементы.
    private FloatingActionButton createWordFloatingActionButton;
    private CheckBox learnSubgroupCheckBox;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private WordsRecyclerViewAdapter adapter;

    // Иконки для свайпа слова в recyclerView.
    private Drawable deleteIcon;
    private Drawable linkIcon;

    // id подгруппы.
    private long subgroupId;
    private boolean subgroupIsCreatedByUser;
    private boolean deleteFlag;
    private SubgroupViewModel subgroupViewModel;

    private MenuItem sortWords;
    private MenuItem resetWordsProgress;
    private MenuItem deleteSubgroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subgroup);

        // Получаем id подгруппы из Intent.
        getBundleArguments();

        // Находим View элементы из разметки.
        findViews();

        // Создаём для Activity ViewModel.
        subgroupViewModel = new ViewModelProvider(this).get(SubgroupViewModel.class);

        // Устанавливаем наш toolbar.
        setSupportActionBar(toolbar);

        subgroupViewModel.setLiveDataSubgroup(subgroupId);
        subgroupViewModel.getLiveDataSubgroup().observe(this, new Observer<Subgroup>() {
            @Override
            public void onChanged(Subgroup subgroup) {
                if (subgroup != null) {
                    Log.i(LOG_TAG, "subgroup onChanged()");

                    getSupportActionBar().setTitle(subgroup.name);

                    initCreateWordFAB(subgroup);

                    initLearnSubgroupCheckBox(subgroup);

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
                            learnSubgroupCheckBox.setVisibility(View.GONE);
                            Toast.makeText(SubgroupActivity.this,
                                    R.string.error_subgroup_is_empty, Toast.LENGTH_LONG)
                                    .show(); // Пуста может быть только подгруппа созданная пользователем.
                        } else {
                            learnSubgroupCheckBox.setVisibility(View.VISIBLE);
                        }
                        adapter.setWords(words);
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!deleteFlag) {
            subgroupViewModel.updateSubgroup();
        }
    }

    /**
     * Метод обрабатывает результат работы WordActivity, которое может создавать новое слово
     * или редактировать уже существующее.
     *
     * @param requestCode
     * @param resultCode
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

            if (requestCode == REQUEST_CODE_CREATE_NEW_WORD) {
                final Word newWord = new Word(word, transcription, value);
                subgroupViewModel.insertWordToSubgroup(newWord);
            }

            if (requestCode == REQUEST_CODE_EDIT_EXISTING_WORD) {
                long wordId = data.getLongExtra(WordActivity.EXTRA_WORD_ID, 0);
                if (wordId != 0) {
                    subgroupViewModel.updateWord(wordId, word, value, transcription);
                }
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
        sortWords = menu.findItem(R.id.activity_subgroup___action___sort);
        resetWordsProgress = menu.findItem(R.id.activity_subgroup___action___reset_words_progress);
        deleteSubgroup = menu.findItem(R.id.activity_subgroup___action___delete_subgroup);
        if (!subgroupIsCreatedByUser) {
            deleteSubgroup.setVisible(false);
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
                sortWordsDialogFragment.show(manager, DIALOG_SORT_WORDS);
                return true;
            // Сбрасывание прогресса слов данной подгруппы.
            case R.id.activity_subgroup___action___reset_words_progress:
                Log.d(LOG_TAG, "Reset words progress");
                ResetProgressDialogFragment resetProgressDialogFragment =
                        new ResetProgressDialogFragment();
                Bundle arguments = new Bundle();
                arguments.putInt(ResetProgressDialogFragment.EXTRA_FLAG,
                        ResetProgressDialogFragment.FOR_SUBGROUP);
                resetProgressDialogFragment.setArguments(arguments);
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

    @Override
    public void sort(int param) {
        subgroupViewModel.sortWords(param);
        recyclerView.getAdapter().notifyDataSetChanged();
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
        learnSubgroupCheckBox = findViewById(R.id.activity_subgroup___check_box___study_subgroup);
        toolbar = findViewById(R.id.activity_subgroup___toolbar);
        recyclerView = findViewById(R.id.activity_subgroup___recycler_view___words);
    }

    private void initLearnSubgroupCheckBox(Subgroup subgroup) {
        // Присваиваем чекбоксу изучения значение, находящееся в БД.
        learnSubgroupCheckBox.setChecked(subgroup.isStudied == 1);
        // Присваиваем обработчик нажатия на чекбокс изучения подгруппы.
        learnSubgroupCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Меняем данные по id подгруппы в БД.
                subgroupViewModel.setIsStudied(isChecked);
            }
        });
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
                    LinkOrDeleteWordDialogFragment deleteDialog = new LinkOrDeleteWordDialogFragment();
                    Bundle arguments = new Bundle();
                    arguments.putLong(LinkOrDeleteWordDialogFragment.EXTRA_WORD_ID, wordId);
                    arguments.putInt(LinkOrDeleteWordDialogFragment.EXTRA_FLAG, LinkOrDeleteWordDialogFragment.TO_LINK);
                    deleteDialog.setArguments(arguments);
                    deleteDialog.show(getSupportFragmentManager(), DIALOG_LINK_WORD);
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
                // НЕОБХОДИМО ПРОПИСАТЬ ВЫЗОВ ДИАЛОГА ДЛЯ ЛИНКОВКИ СЛОВА.
                swipeBackground.setColor(Color.parseColor("#7FB069"));
                swipeBackground.setBounds(
                        itemView.getLeft(),
                        itemView.getTop(),
                        (int) dX,
                        itemView.getBottom());
            } else {
                swipeBackground.setColor(Color.parseColor("#CC444B"));
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
