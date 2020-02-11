package ru.nikshlykov.englishwordsapp;

public class Mode {
    private long id;
    private String modeName;
    private boolean isSelected;
    private int imageResourceId;

    public Mode(long id, String modeName, boolean isSelected, int imageResourceId) {
        this.id = id;
        this.modeName = modeName;
        this.isSelected = isSelected;
        this.imageResourceId = imageResourceId;
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

    public int getImageResourceId(){
        return imageResourceId;
    }

    public void setIsSelected(boolean isSelected){
        this.isSelected = isSelected;
    }
}
