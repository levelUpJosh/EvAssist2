package com.lborof028685.evassist2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class NewsActivity extends AppCompatActivity {
    /**
     * Displays an RSS feed of Electric Vehicle news
     * GRIDSERVE's RSS is used as an example
     */
    BottomNavigationView bottomNavigationView;

    ListView lvRss;
    ArticleList articles;


    public boolean internetAvailable() {
        /**
         * Determines if there is an active connection to the Internet
         */
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!internetAvailable()){
            // Abort if Internet is not available and provide a toast to explain this to the user
            Context context = getApplicationContext();
            CharSequence text = "No internet access to retrieve news";

            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_news);


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // select the correct nav item
        bottomNavigationView.setSelectedItemId(R.id.newsSelector);

        // assign listeners to nav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.guideSelector:
                    startActivity(new Intent(getApplicationContext(), GuideActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.chargingSelector:
                    startActivity(new Intent(getApplicationContext(), ChargingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.newsSelector:

                    return true;
                case R.id.settingSelector:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        // get the ListView that will contain the news feed
        lvRss = (ListView) findViewById(R.id.lvRss);

        // generate a new ArticleList
        articles = new ArticleList();

        // for each Item clicked
        lvRss.setOnItemClickListener((adapterView, view, position, id) -> {
            // get the item's uri
            Uri webpage = Uri.parse(articles.get(position).getLink());

            // create and send an intent with the webpage to be received by a browser
            Intent intent = new Intent(Intent.ACTION_VIEW,webpage);
            startActivity(intent);

        });

        // enable long clicking
        lvRss.setLongClickable(true);

        // for each item long clicked:
        lvRss.setOnItemLongClickListener((parent, view, position, id) -> {
            // get the link as a Uri
            Uri webpage = Uri.parse(articles.get(position).getLink());

            // create a new Intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            // add the link as an extra
            shareIntent.putExtra(Intent.EXTRA_TEXT, articles.get(position).getLink());

            // set the type
            // here it's plain as we want any app that can send text
            shareIntent.setType("text/plain");

            // give the Intent a title
            shareIntent.putExtra(Intent.EXTRA_TITLE, articles.get(position).getTitle());

            // create an Android Sharesheet for the Intent
            startActivity(Intent.createChooser(shareIntent, null));
            return true;
        });

        // get the RSS feed
        new ProcessInBackground("https://www.gridserve.com/feed/").execute();



    }
    public InputStream getInputStream(URL url) {
        /**
         * Opens an InputStream to the given url and returns the InputStream
         */
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer,Integer,Exception> {
        /**
         * Gets the information from a given RSS feed and turns it into an accessible ArticleList
         */

        ProgressBar progressBar = new ProgressBar(NewsActivity.this);
        Exception exception = null;
        private String url;

        public ProcessInBackground(String url_to_use){
            // get the url
            this.url = url_to_use;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setMessage("Loading RSS Feed");
            //progressBar.show();
        }


        @Override
        protected Exception doInBackground(Integer... integers) {
            // returned in string in three <> types

            try {
                // make a URL object from the given url
                URL url = new URL(this.url);

                // create an XmlPullParserFactory
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                // make a new XmlPullParser
                XmlPullParser xpp = factory.newPullParser();

                // give the InputStream to the XPP
                xpp.setInput(getInputStream(url),"UTF-8");

                // define a boolean of whether we are currently looking in an item
                boolean insideItem = false;

                // initialise a new Article
                Article article = new Article();

                // get the initial event type
                int eventType = xpp.getEventType();

                // while it is not the end of the feed
                while (eventType != XmlPullParser.END_DOCUMENT){
                    // if this is a start tag
                    if (eventType == XmlPullParser.START_TAG) {
                        // if the name is "item"
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            // set inside an item
                            insideItem = true;

                            // make a new Article
                            article = new Article();
                        }
                        else if(insideItem) {

                            if (xpp.getName().equalsIgnoreCase("title")) {
                                article.setTitle(xpp.nextText()); //if name is "title" save it
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                article.setLink(xpp.nextText()); //if name is "link" save it
                            }

                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        // if this is an end tag "item"
                        insideItem = false; // outside item
                        articles.add(article); //  finalise the article by adding it
                    }
                    // get the next event type
                    eventType = xpp.next();
                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch(XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }


            return exception;
        }
        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
            /**
             * Runs after executing the doInBackground method
             */

            // generate a new ArrayAdapter for the Articles
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewsActivity.this, android.R.layout.simple_list_item_1,articles.getAllTitles());

            // set the adapter
            lvRss.setAdapter(adapter);

            // hide the loading circle
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        }


    }
}