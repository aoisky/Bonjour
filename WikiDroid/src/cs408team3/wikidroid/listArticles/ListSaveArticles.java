package cs408team3.wikidroid.listArticles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cs408team3.wikidroid.R;
import cs408team3.wikidroid.Utils;

public class ListSaveArticles extends Activity {

    private ListView          listView;
    private ArrayList<String> listArticles;
    private Map<String, ?>    mapLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_save_links);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listView1);
        mapLinks = new HashMap<String, String>();
        ArrayList<String> articles = getSavedArticlesList();

        for (String s : articles) {
            mapLinks.put(s, null);
        }

        listArticles = new ArrayList<String>();

        if (Utils.isNetworkAvailable(this)) {
            Set<String> keys = mapLinks.keySet();

            for (String s : keys) {
                listArticles.add(s);
            }
        }

        ListAdapter adapter = new ListAdapter(this, R.layout.article_list_item, R.id.text_list_article, R.id.img_list_article);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String filename = listArticles.get(arg2);

                if (mapLinks.get(filename) == null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        filename += ".mht";
                    }
                    else {
                        filename += ".xml";
                    }
                    Intent intent = new Intent(getApplicationContext(), LoadSavedStuffs.class);
                    intent.putExtra("filename", filename);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), LoadSavedStuffs.class);
                    intent.putExtra("url", (String) mapLinks.get(filename));
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_save_links, menu);
        return true;
    }

    ArrayList<String> getSavedArticlesList() {

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/WikiDroid/");

        File[] filesOnDir = dir.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>(filesOnDir.length);
        for (File f : filesOnDir) {
            fileNames.add(f.getName());
        }
        return fileNames;
    }

    public Map<String, ?> getSavedLinkList() {

        SharedPreferences sharedPref = this.getSharedPreferences(Utils.LINKS, Context.MODE_PRIVATE);
        Map<String, ?> links = sharedPref.getAll();

        return links;
    }

    class ListAdapter extends BaseAdapter {

        private static final String TAG = "ListArticlesAdapter";

        private LayoutInflater      mInflater;
        private int                 mResource;
        private int                 mFieldId;
        private int                 mImgFieldId;

        public ListAdapter(Context context, int resource, int textViewResourceId, int ImageViewResourceId) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mResource = resource;
            mFieldId = textViewResourceId;
            mImgFieldId = ImageViewResourceId;
        }

        @Override
        public int getCount() {
            return listArticles.size();
        }

        @Override
        public String getItem(int position) {
            return listArticles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView text;
            ImageView imgView;

            if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);

            } else {
                view = convertView;
            }
            try {
                text = (TextView) view.findViewById(mFieldId);
                imgView = (ImageView) view.findViewById(mImgFieldId);

            } catch (ClassCastException e) {
                throw new IllegalStateException(TAG + " requires the resource ID to be a TextView", e);
            }
            text.setText(Utils.trimWikipediaTitle(getItem(position)));

            return view;
        }

    }
}
