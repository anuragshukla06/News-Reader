package com.example.anuragshukla.newsreader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DownloadedNewsActivity extends AppCompatActivity {

    ArrayList<String> downloadedNewsTitle = new ArrayList<String>();
    ArrayList<String> downloadedNewsHTML;
    ArrayAdapter<String> arrayAdapter;
    TextView emptyTextView;
    ListView downloadedNewsListView;

    public void loadNews(){
        try {
            Cursor c = ArticleUrlLoader.articleDB.rawQuery("SELECT * FROM articles", null);
            int titleIndex = c.getColumnIndex("Title");
            int htmlIndex = c.getColumnIndex("HTML");

            c.moveToFirst();
            do {

                downloadedNewsTitle.add(c.getString(titleIndex));
                downloadedNewsHTML.add(c.getString(htmlIndex));
                arrayAdapter.notifyDataSetChanged();
                downloadedNewsListView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.INVISIBLE);

                Log.i("msg", c.getString(htmlIndex));

            } while (c.moveToNext());
        } catch (Exception e){
            downloadedNewsListView.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_news);
        downloadedNewsHTML = new ArrayList<String>();
        emptyTextView = findViewById(R.id.emptyTextView);

        downloadedNewsListView = findViewById(R.id.downloadedNewsListView);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, downloadedNewsTitle);
        downloadedNewsListView.setAdapter(arrayAdapter);

        loadNews();

        downloadedNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), downloadedUrlLoader.class);
                intent.putExtra("content", downloadedNewsHTML.get(position));
                startActivity(intent);
            }
        });

        downloadedNewsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(DownloadedNewsActivity.this)
                        .setIcon(R.drawable.ic_launcher_background)
                        .setTitle("Remove from downloads?")
                        .setMessage("This action cannot be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int p) {
                                ArticleUrlLoader.articleDB.execSQL("DELETE FROM articles WHERE Title = ?", new String[]{downloadedNewsTitle.get(position)});
                                downloadedNewsTitle.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                                if(downloadedNewsTitle.size() <= 0){
                                    emptyTextView.setVisibility(View.VISIBLE);
                                    downloadedNewsListView.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

    }
}
