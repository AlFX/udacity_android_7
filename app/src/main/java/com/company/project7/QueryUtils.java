package com.company.project7;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {


    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /* this class must not  be initialised */
    private QueryUtils() {
    }

    /* Provide list of Book objects */
    public static List<Book> extractFeaturesFromJson(String booksJSON) {

        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        /* empty ArrayList */
        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(booksJSON);
            if (baseJsonResponse.has("items")) {
                JSONArray booksArray = baseJsonResponse.getJSONArray("items");

                /* For each book create an object */
                for (int i = 0; i < booksArray.length(); i++) {
                    JSONObject currentBook = booksArray.getJSONObject(i);
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                  /* retrieve the title */
                    String title = volumeInfo.getString("title");

                   /* "authors" key might contain multiple values! */
                    JSONArray authorsArray;
                    StringBuilder authors = new StringBuilder();
                    if (volumeInfo.has("authors")) {
                        authorsArray = volumeInfo.getJSONArray("authors");
                        for (int n = 0; n < authorsArray.length(); n++) {
                            authors.append(System.getProperty("line.separator"));
                            authors.append(authorsArray.getString(n));
                        }
                    } else {
                        authors.append("No Author");
                    }

                /* Create a new Book object and append it to the ArrayList */
                    Book booksObject = new Book(title, authors);
                    books.add(booksObject);
                }
            }
        } catch (JSONException e) {
            /* catch any exception here, prevent a crash */
            Log.e(LOG_TAG, "Problem parsing the JSON list books", e);
        }

        return books;
    }

    /* Query the Google Books API and return a list of Book object */
    public static List<Book> fetchBooksData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL url = createUrl(requestUrl);
        String jsonRespond = null;
        try {
            jsonRespond = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
        }

        return extractFeaturesFromJson(jsonRespond);
    }

    /* Returns new URL object from the string */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    /* standard HTTP request execution */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /* successful request (code 200), then read input stream, parse the response */
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                /* Closing the input stream could throw an IOException */
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /* Convert the InputStream into a String which contains the whole JSON response from the server */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
