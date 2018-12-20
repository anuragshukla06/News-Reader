package com.example.anuragshukla.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ArticleUrlLoader extends AppCompatActivity {

    String url;
    String Title;
    static SQLiteDatabase articleDB;
    String articleHTML = "";

    class HTMLdownloader extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {


            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read(); int p =1;
                while(data != -1){
                    Log.i("msggg", Integer.toString(p++));
                    articleHTML += (char) data;
                    data = reader.read();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return articleHTML;
        }

        @Override
        protected void onPostExecute(String s) {
            String sql = "INSERT INTO articles (Title, URL, HTML) VALUES (?, ?, ?)";
            SQLiteStatement statement = articleDB.compileStatement(sql);
            statement.bindString(1, Title);
            statement.bindString(2, url);
            statement.bindString(3, articleHTML);

            statement.execute();
            Toast.makeText(getApplicationContext(), "Download compeleted", Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);

        }
    }

    public void downloadNews(String newsUrl){
        Cursor c = articleDB.rawQuery("SELECT * FROM articles WHERE URL = ?", new String[]{newsUrl});
        if (!(c.getCount() <=0)){
            Toast.makeText(getApplicationContext(), "This news is already in downloads", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
            HTMLdownloader htmLdownloader = new HTMLdownloader();
            htmLdownloader.execute(newsUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.downloadOption:
                downloadNews(url);
                break;

            default:
                Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_url_loader);

        Intent intent = getIntent();
        url = intent.getStringExtra("Url");
        Title = intent.getStringExtra("Title");
        articleDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles (ArticleID INTEGER PRIMARY KEY, Title VARCHAR, URL VARCHAR, HTML VARCHAR)");


        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        Log.i("url", url);

        webView.loadUrl(url);
    }
}
