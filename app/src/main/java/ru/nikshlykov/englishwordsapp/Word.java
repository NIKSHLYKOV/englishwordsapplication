package ru.nikshlykov.englishwordsapp;

public class Word {

    private String word;
    private String transcription;
    private String value;
    private long id;

    public Word(String word, String transcription, String value, long id) {
        this.word = word;
        this.transcription = transcription;
        this.value = value;
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public String getTranscription() {
        return transcription;
    }

    public String getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
