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
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.word.WordActivity;

public class SubgroupActivity extends AppCompatActivity implements SortWordsDialogFragment.SortWordsListener {

    public static final String EXTRA_SUBGROUP_ID = "SubgroupId";
    private static final int REQUEST_CODE_EDIT_EXISTING_WORD = 1;
    private static final int REQUEST_CODE_CREATE_NEW_WORD = 2;

    private static final String LOG_TAG = "SubgroupActivity";

    private static final String DIALOG_SORT_WORDS = "SortWordsDialogFragment";
    private static final String DIALOG_RESET_WORDS_PROGRESS = "ResetWordsProgressDialogFragment";
    private static final String DIALOG_DELETE_WORDS = "DeleteWordsDialogFragment";

    // View элементы.
    private FloatingActionButton createWordFloatingActionButton;
    private CheckBox learnSubgroupCheckBox;
    private Toolbar toolbar;

    // RecyclerView и вспомогательные элементы.
    private RecyclerView recyclerView;
    private WordsRecyclerViewAdapter adapter;
    Drawable deleteIcon;
    Drawable linkIcon;

    // Полученные данные из Intent'а.
    private Bundle arguments;
    private long subgroupId;

    private SubgroupViewModel subgroupViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subgroup);

        // Получаем Extras из Intent'а.
        arguments = getIntent().getExtras();
        if (arguments == null)
            finish();

        // Получаем id подгруппы.
        subgroupId = arguments.getLong(EXTRA_SUBGROUP_ID);

        // Создаём для Activity ViewModel.
        subgroupViewModel = new ViewModelProvider(this).get(SubgroupViewModel.class);
        subgroupViewModel.setSubgroup(subgroupId);

        // Находим View элементы из разметки.
        findViews();

        // Устанавливаем наш toolbar и закидываем ему заглавие в виде имени текущей подгруппы.
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(subgroupViewModel.getSubgroup().name);

        // Проверяем, что подгруппа создана пользователем.
        if (subgroupViewModel.getSubgroup().groupId == SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID) {
            // Присваиваем обработчик кнопке для создания нового слова.
            createWordFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createNewWordIntent = new Intent(getApplicationContext(), WordActivity.class);
                    createNewWordIntent.putExtra(EXTRA_SUBGROUP_ID, subgroupId);
                    startActivityForResult(createNewWordIntent, REQUEST_CODE_CREATE_NEW_WORD);
                }
            });
        } else {
            // Скрываем fab для создания нового слова.
            createWordFloatingActionButton.setVisibility(View.GONE);
            createWordFloatingActionButton.setClickable(false);
        }

        // Присваиваем чекбоксу изучения значение, находящееся в БД.
        // Делаем это до обработчика нажатия, чтобы данные не перезаписывались лишний раз.
        learnSubgroupCheckBox.setChecked(subgroupViewModel.getSubgroup().isStudied == 1);
        // Присваиваем обработчик нажатия на чекбокс изучения подгруппы.
        learnSubgroupCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Меняем данные по id подгруппы в БД.
                // Если флажок выставлен, то isStudied должен стать 1.
                // Если же флажок не выставлени, то isStudied должен стать 0.
                if (isChecked)
                    subgroupViewModel.getSubgroup().isStudied = 1;
                else
                    subgroupViewModel.getSubgroup().isStudied = 0;
                // Обновляем поле изучения подгруппы в БД.
                subgroupViewModel.update();
            }
        });


        // Создаём вспомогательные вещи для RecyclerView и соединяем их с ним.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SubgroupActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SubgroupActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Создаём adapter для RecyclerView.
        adapter = new WordsRecyclerViewAdapter(SubgroupActivity.this);

        // Присваиваем обработчик нажатия на элемент в RecyclerView (слово).
        adapter.setOnEntryClickListener(new WordsRecyclerViewAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                final Word currentWord = WordsRecyclerViewAdapter.getWords().get(position);
                Intent editExistingWordIntent = new Intent(SubgroupActivity.this, WordActivity.class);
                editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_ID, currentWord.id);
                startActivityForResult(editExistingWordIntent, REQUEST_CODE_EDIT_EXISTING_WORD);
            }
        });

        // Закидываем данные в адаптер, подписывая его на изменения в LiveData.
        subgroupViewModel.getWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                Log.i(LOG_TAG, "onChanged()");

                // Если слов нет, то скрываем CheckBox изучения подгруппы.
                if (words.isEmpty()) {
                    learnSubgroupCheckBox.setVisibility(View.GONE);
                    Toast.makeText(SubgroupActivity.this,
                            "На данный момент ваша группа пуста! Добавьте в неё слова из " +
                                    "других групп или создайте свои.", Toast.LENGTH_LONG)
                            .show();
                    // Пуста может быть только подгруппа созданная пользователем.
                } else {
                    learnSubgroupCheckBox.setVisibility(View.VISIBLE);
                }

                adapter.setWords(words);
            }
        });

        // Соединяем RecyclerView с адаптером для него.
        recyclerView.setAdapter(adapter);

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp);
        linkIcon = ContextCompat.getDrawable(this, R.drawable.ic_link_white_24dp);

        // Добавляем swipe на удаление из своей подгруппы.
        if (subgroupViewModel.getSubgroup().groupId == SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.LEFT) {
                        final int removedPosition = viewHolder.getAdapterPosition();
                        final Word removedWord = adapter.getWordAt(removedPosition);
                        subgroupViewModel.deleteLinkWithSubgroup(removedWord.id);
                        Snackbar.make(viewHolder.itemView, "Слово удалено", Snackbar.LENGTH_LONG)
                                .setAction("Oтменить", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        subgroupViewModel.insertLinkWithSubgroup(removedWord.id);
                                    }
                                }).show();
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

                    if(dX > 0){
                        linkIcon.setBounds(
                                itemView.getLeft() + linkIconMargin,
                                itemView.getTop() + linkIconMargin,
                                itemView.getLeft() + linkIconMargin + linkIcon.getIntrinsicWidth(),
                                itemView.getBottom() - linkIconMargin);
                        linkIcon.draw(c);
                    }
                    else{
                        deleteIcon.setBounds(
                                itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth(),
                                itemView.getTop() + deleteIconMargin,
                                itemView.getRight() - deleteIconMargin,
                                itemView.getBottom() - deleteIconMargin);
                        deleteIcon.draw(c);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }).attachToRecyclerView(recyclerView);
        }

        Log.d(LOG_TAG, "OnCreate");
    }

    private void findViews() {
        createWordFloatingActionButton = findViewById(R.id.activity_subgroup___floating_action_button___new_word);
        learnSubgroupCheckBox = findViewById(R.id.activity_subgroup___check_box___study_subgroup);
        toolbar = findViewById(R.id.activity_subgroup___toolbar);
        recyclerView = findViewById(R.id.activity_subgroup___recycler_view___words);
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
                Word newWord = new Word(word, transcription, value);
                //newWord.id = 2005L;
                long newWordId = subgroupViewModel.insert(newWord);

                Link linkWithThisSubgroup = new Link(subgroupId, newWordId);
                subgroupViewModel.insert(linkWithThisSubgroup);

                Toast.makeText(this, "id нового слова: " +
                                newWordId,
                        Toast.LENGTH_LONG).show();

            }

            if (requestCode == REQUEST_CODE_EDIT_EXISTING_WORD) {
                long wordId = data.getLongExtra(WordActivity.EXTRA_WORD_ID, 0);
                if (wordId != 0) {
                    Word editWord = subgroupViewModel.getWord(wordId);
                    editWord.word = word;
                    editWord.transcription = transcription;
                    editWord.value = value;
                    subgroupViewModel.update(editWord);
                }
            }
        }
    }

    /**
     * Методы по работе с Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_subgroup_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();

        // Адаптировать под группу
        // !!!!!!!!!!!!!!!!!!!!!!!!!!

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
                /*ResetWordProgressDialogFragment resetWordProgressDialogFragment = new ResetWordProgressDialogFragment();
                resetWordProgressDialogFragment.show(manager, DIALOG_RESET_WORDS_PROGRESS);*/
                return true;
            // Удаление слов из данной подгруппы.
            case R.id.activity_subgroup___action___delete_words:
                Log.d(LOG_TAG, "Delete words");
                /*CopyWordDialogFragment copyWordDialogFragment = new CopyWordDialogFragment();
                copyWordDialogFragment.show(manager, DIALOG_DELETE_WORDS);*/
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
}
