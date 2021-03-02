package de.walhalla.app.fragments.drink.search;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchModel implements Searchable {
    private String mTitle;

    public SearchModel(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
