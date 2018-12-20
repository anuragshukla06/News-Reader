package com.example.anuragshukla.newsreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView newsListView;
    ArrayList<String> newsUrlArrayList = new ArrayList<>();
    ArrayList<String> newsTitleArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.downloaded_news_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.downloads:
                Intent intent = new Intent(getApplicationContext(), DownloadedNewsActivity.class);
                startActivity(intent);
                break;

            default:
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    class NewsLoader extends AsyncTask<String, String, String> {

        ProgressDialog dialog;

        NewsLoader(Activity MainActivity){
            dialog = new ProgressDialog(MainActivity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("News loading, please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String newsIds = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while(data != -1){
                    newsIds += (char) data;
                    data = reader.read();
                }

                JSONArray newsIdsJsonArray = new JSONArray(newsIds);
                int noToDisplay = 20;

                for(int i=0; i<noToDisplay; i++){
                    String articleInfo = "";

                    String articleId = newsIdsJsonArray.getString(i);
                    Log.i("debug", Integer.toString(i) + " " + articleId);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    inputStream = httpURLConnection.getInputStream();
                    reader = new InputStreamReader(inputStream);

                    data = reader.read();
                    while(data != -1){
                        articleInfo += (char) data;
                        data = reader.read();
                    }
                    Log.i("debug", Integer.toString(i) + " " + articleInfo);

                    JSONObject jsonObject = new JSONObject(articleInfo);
                    if(!jsonObject.isNull("url") && !jsonObject.isNull("title")){
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");
                        String toAdd = articleTitle + ";" + articleUrl;
                        publishProgress(toAdd);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";

        }

        @Override
        protected void onProgressUpdate(String... values) {

            newsUrlArrayList.add(values[0].split(";")[1]);
            String titleOfArticle = values[0].split(";")[0];
            newsTitleArrayList.add(titleOfArticle);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
        }
    }


    void loadNews(){
        NewsLoader newsLoader = new NewsLoader(this);
        newsLoader.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsListView = findViewById(R.id.newsListView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newsTitleArrayList);
        newsListView.setAdapter(arrayAdapter);
        ArticleUrlLoader.articleDB = this.openOrCreateDatabase("Articles", Context.MODE_PRIVATE, null);
        loadNews();

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleUrlLoader.class);
                intent.putExtra("Url", newsUrlArrayList.get(index));
                intent.putExtra("Title", newsTitleArrayList.get(index));
                startActivity(intent);

            }
        });

    }
}
