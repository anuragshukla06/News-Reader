package com.example.anuragshukla.newsreader;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ArticleUrlLoader extends AppCompatActivity {

      String url;
//    SQLiteDatabase articleDB;
//
//    class HTMLdownloader extends AsyncTask<String, Void, String>{
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//            String articleHTML = "";
//            try {
//                URL url = new URL(strings[0]);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                InputStream inputStream = httpURLConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//
//                int data = reader.read();
//                while(data != -1){
//                    articleHTML += (char) data;
//                    data = reader.read();
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return articleHTML;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//        }
//    }

//    public void downloadNews(String newsUrl){
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//        switch (item.getItemId()){
//            case R.id.downloadOption:
//                downloadNews(url);
//
//            default:
//                Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
//        }
//        return true;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_url_loader);

        Intent intent = getIntent();
        url = intent.getStringExtra("Url");
//        articleDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
//        articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles (ArticleID INTEGER PRIMARY KEY, Title VARCHAR, URL VARCHAR, HTML VARCHAR)");


        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        Log.i("url", url);

        webView.loadUrl(url);
    }
}
