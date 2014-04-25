package cs408team3.wikidroid;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cs408team3.wikidroid.listArticles.LoadSavedStuffs;

public class ListSaveLinks extends Activity {

    private ListView          listView;
    private Map<String, ?>    list;
    private ArrayList<String> listLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_save_links);

        listView = (ListView) findViewById(R.id.listView1);

        list = getSavedLinkList();
        listLinks = new ArrayList<String>(list.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listLinks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String name = listLinks.get(arg2);
                String link = (String) list.get(name);
                if (link == null)
                    return;
                Intent intent = new Intent(getApplicationContext(), LoadSavedStuffs.class);
                intent.putExtra("url", link);
                startActivity(intent);
            }
        });
    }

    public Map<String, ?> getSavedLinkList() {
        SharedPreferences sharedPref = this.getSharedPreferences(Utils.LINKS, Context.MODE_PRIVATE);
        Map<String, ?> links = sharedPref.getAll();

        return links;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_save_links, menu);
        return true;
    }

}
