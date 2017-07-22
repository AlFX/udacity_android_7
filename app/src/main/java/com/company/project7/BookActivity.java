package com.company.project7;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    /* BASE URL from Google API */
    private static final String BASIC_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    /* COMPLETE URL from Google API */
    private static final String DEFAULT_URL = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";
    /* Max number of results */
    private static final String MAX_RESULTS = "&maxResults=20";

    /* Books list adapter */
    private BookAdapter mAdapter;
    /* EditText to gather user search query */
    private EditText formEditText;
    /* Empty state TextView */
    private TextView mEmptyView;
    /* display a loading indicator while loading stuff */
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        /* Connectivity Manager */
        final ConnectivityManager ConnMan = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnMan.getActiveNetworkInfo();

        /* connectivity flag */
        final boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        /* Find all the view id */
        final ListView bookListView = (ListView) findViewById(R.id.list);
        formEditText = (EditText) findViewById(R.id.form);
        Button searchButton = (Button) findViewById(R.id.button);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        loadingIndicator = findViewById(R.id.loading_indicator);

        /* set empty state view when there is no data */
        bookListView.setEmptyView(mEmptyView);

        /* New adapter accepting empty list of books + set to XML */
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(mAdapter);

        BookListAsyncTask task = new BookListAsyncTask()    ;

        /* check if connectivity flag is true */
        if (isConnected) {
            /* fetch books */
            task.execute(DEFAULT_URL);
        } else {
            /* get rid of the progress bar */
            loadingIndicator.setVisibility(View.GONE);
            /* display empty state TextView */
            mEmptyView.setVisibility(View.VISIBLE);
            /* set text into empty state TextView */
            mEmptyView.setText(R.string.no_connection);
        }

        /* Hide the keyboard at startup */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /* search button mess below */
        searchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                NetworkInfo activeNetworkInfo = ConnMan.getActiveNetworkInfo();
                final boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

                /* check if connectivity flag is true */
                if (isConnected) {
                    /* sanitize the input */
                    String query = formEditText.getText().toString().replaceAll("\\s+", "").toLowerCase();
                    /* cannot search an empty query, prompt the user */
                    if (query.isEmpty()) {
                        Toast.makeText(BookActivity.this, getString(R.string.hint), Toast.LENGTH_SHORT).show();
                    /* if all is good = connection ok + query != "" */
                    } else {
                        /* Hide the empty state */
                        mEmptyView.setVisibility(View.GONE);
                        /* Start AsyncTask to fetch data */
                        new BookListAsyncTask().execute(BASIC_URL + query + MAX_RESULTS);
                    }
                /* connectivity flag false */
                } else {
                    /* get rid of the progress bar */
                    loadingIndicator.setVisibility(View.GONE);
                    /* get rid of the listView */
                    bookListView.setVisibility(View.GONE);
                    /* display empty state TextView */
                    mEmptyView.setVisibility(View.VISIBLE);
                    /* set text into empty state TextView */
                    mEmptyView.setText(R.string.no_connection);
                }
            }
        });

    }

    private class BookListAsyncTask extends AsyncTask<String, Void, List<Book>> {

        /* UI thread before doInBackground() */
        @Override
        protected void onPreExecute() {
            /* Displays loading indicator */
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        /* background thread */
        @Override
        protected List<Book> doInBackground(String... urls) {
            /* no URL no request */
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            /* performs the network request */
            List<Book> result = QueryUtils.fetchBooksData(urls[0]);
            /* never update UI from background thread, instead return a list of Books as result*/
            return result;
        }

        /* UI thread after the background work, receive doInBackground output */
        @Override
        protected void onPostExecute(List<Book> books) {

            /* Hide loading indicator */
            loadingIndicator.setVisibility(View.GONE);
            /* Clear the adapter */
            mAdapter.clear();

            /* if books list is not empty */
            if (books != null && !books.isEmpty()) {
                /* add books to the adapter, ListView automatic update */
                mAdapter.addAll(books);
            } else {
                /* in this case connectivity flag must be false so display empty state */
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(R.string.no_connection);
            }
        }
    }

}
