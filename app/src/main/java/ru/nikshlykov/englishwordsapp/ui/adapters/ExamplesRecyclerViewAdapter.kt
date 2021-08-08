package ru.nikshlykov.englishwordsapp.ui.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.example.Example;

public class ExamplesRecyclerViewAdapter
        extends RecyclerView.Adapter<ExamplesRecyclerViewAdapter.ExamplesViewHolder> {

    // Слова подгруппы.
    private List<Example> examples = new ArrayList<>();

    // TextToSpeech, который будет воспроизводить примеры.
    private TextToSpeech textToSpeech;




    public ExamplesRecyclerViewAdapter(final Context context) {

        /*// Создаём TTS
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Установка языка, высоты и скорости речи.
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(1.3f);
                    textToSpeech.setSpeechRate(0.7f);
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(context, TTS_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return examples.size();
    }


    class ExamplesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // View для параметров слова.
        private EditText inEnglish;
        private EditText inRussian;

        ExamplesViewHolder(View itemView) {
            super(itemView);

            // Находим View, в которые будем устанавливать контент в onBindViewHolder().
            inEnglish = itemView.findViewById(R.id.example_item___edit_text___in_english);
            inRussian = itemView.findViewById(R.id.example_item___edit_text___in_russian);

            /*// Находим кнопку для воспроизведения слова и присваиваем ей обработчик нажатия -
            // сам ViewHolder.
            Button ttsButton = itemView.findViewById(R.id.word_in_subgroup_item___button___voice);
            ttsButton.setOnClickListener(this);*/

            /*// Находим контейнер, который хранит в себе всё кроме кнопки воспроизведения слова
            // и присваиваем ей обработчик нажатия.
            LinearLayout allWithoutVoiceButtonLayout = itemView.findViewById(R.id.word_in_subgroup_item___layout___all_without_voice_button);
            allWithoutVoiceButtonLayout.setOnClickListener(this);*/
        }

        @Override
        public void onClick(View v) {
            /*switch (v.getId()) {
                case R.id.word_in_subgroup_item___button___voice:
                    // Воспроизводим слово.
                    textToSpeech.speak(word.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "somethingID");
                    break;
                case R.id.word_in_subgroup_item___layout___all_without_voice_button:
                    // Вызываем метод слушателя, который реализован в SubgroupActivity.
                    // Прежде всего это необходимо для того, чтобы запускать WordActivity
                    // через startActivityForResult().
                    if (mOnEntryClickListener != null) {
                        mOnEntryClickListener.onEntryClick(v, getLayoutPosition());
                    }
                    break;
            }*/
        }

    }

    @NonNull
    @Override
    public ExamplesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.example_item, parent, false);
        return new ExamplesViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamplesViewHolder holder, final int position) {
        final Example currentExample = examples.get(position);
        holder.inEnglish.setText(currentExample.text);
        holder.inRussian.setText(currentExample.translation);
    }


    public void setExamples(List<Example> examples) {
        this.examples = examples;
        notifyDataSetChanged();
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void addExample(){
        examples.add(new Example());
        notifyItemInserted(examples.size() - 1);
    }

    public Example getExampleAt(int position) {
        return examples.get(position);
    }
}
