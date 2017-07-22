package com.company.project7;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> books){
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /* Butterknife ViewHolder*/
        ViewHolder holder;
        /* this guy recycles objects */
        View booksList = convertView;

        /* if there is no book list you need to make one*/
        if (booksList == null) {
            booksList = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
            holder = new ViewHolder(booksList);
            booksList.setTag(holder);
        } else {
            holder = (ViewHolder) booksList.getTag();
        }

        Book currentBook = getItem(position);
        holder.titleView.setText(currentBook.getTitle());
        holder.authorView.setText(currentBook.getAuthor());
        return booksList;
    }

    static class ViewHolder {
        @BindView(R.id.title)
        TextView titleView;
        @BindView(R.id.author)
        TextView authorView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
