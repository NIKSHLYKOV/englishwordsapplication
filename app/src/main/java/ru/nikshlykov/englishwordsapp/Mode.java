package ru.nikshlykov.englishwordsapp;

public class Mode {
    private long id;
    private String modeName;
    private boolean isSelected;
    private int imageResourseId;

    public static final Mode[] modes = {
            new Mode(1, "Словарные карточки (с английского на русский)", false, R.drawable.notes_1),
            new Mode(2, "Написать слово", false, R.drawable.notes_1),
            new Mode(3, "Словарные карточки (с русского на английский)", false, R.drawable.notes_1),
            new Mode(4, "Собрать слово по буквам", false, R.drawable.notes_1),
            new Mode(5, "Написать слово по звучанию", false, R.drawable.notes_1),
            new Mode(6, "Выбрать одно из четырёх по звучанию", false, R.drawable.notes_1),
            new Mode(7, "Какой-то текст", false, R.drawable.notes_1),
    };

    private Mode(long id, String modeName, boolean isSelected, int imageResourseId) {
        this.id = id;
        this.modeName = modeName;
        this.isSelected = isSelected;
        this.imageResourseId = imageResourseId;
    }

    public long getId() {
        return id;
    }

    public String getModeName() {
        return modeName;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public int getImageResourseId(){
        return imageResourseId;
    }
}
