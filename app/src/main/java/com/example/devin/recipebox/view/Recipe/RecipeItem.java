package com.example.devin.recipebox.view.Recipe;

public class RecipeItem {

    private String mText1;
    private String mText2;

    public RecipeItem( String text1, String text2 ) {
        mText1 = text1;
        mText2 = text2;
    }

    public void changeText1(String text) {
        mText1 = text;
    }
    public String getText1() {
        return mText1;
    }
    public String getText2() { return mText2; }
}
