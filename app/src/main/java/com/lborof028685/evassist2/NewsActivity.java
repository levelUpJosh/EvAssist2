package com.lborof028685.evassist2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class NewsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    ListView lvRss;
    ArticleList articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setSelectedItemId(R.id.newsSelector);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                }
                return false;
            }
        });


        lvRss = (ListView) findViewById(R.id.lvRss);

        articles = new ArticleList();


        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ArticleFragment.class);
                intent.putExtra("articleLink",articles.get(position).getLink());
                startActivity(intent);

            }
        });

        new ProcessInBackground("https://www.gridserve.com/feed/").execute();



    }
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer,Integer,Exception> {
        ProgressBar progressBar = new ProgressBar(NewsActivity.this);
        Exception exception = null;
        private String url;

        public ProcessInBackground(String url_to_use){
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
                URL url = new URL(this.url);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url),"UTF-8");

                boolean insideItem = false;

                Article article = new Article();

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT){
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true; //inside an item
                            article = new Article();
                        }
                        else if(insideItem) {
                            if (xpp.getName().equalsIgnoreCase("title")) {
                                article.setTitle(xpp.nextText());
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                article.setLink(xpp.nextText());
                            }

                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                        articles.add(article);
                    }
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewsActivity.this, android.R.layout.simple_list_item_1,articles.getAllTitles());

            lvRss.setAdapter(adapter);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        }


    }
}