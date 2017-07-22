package com.company.project7;

public class Book {

    private String mTitle;
    private StringBuilder mAuthor;

    public Book (String title, StringBuilder author) {
        mTitle = title;
        mAuthor = author;
    }

    public String getTitle(){
        return mTitle;
    }
    public StringBuilder getAuthor(){
        return mAuthor;
    }
}
