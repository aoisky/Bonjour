package cs408team3.wikidroid.languages;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class UrlList extends AsyncTask<String, Integer, List<String>> {

    private Languages languages;
    private String    webUrl;
    private Listener  listener;

    public UrlList(Languages languages, String url, Listener listener) {
        this.languages = languages;
        this.webUrl = url;
        this.listener = listener;
    }

    public interface Listener {

        public void onResponse(List<String> result);

    }

    @Override
    protected List<String> doInBackground(String... url) {
        ArrayList<String> names = languages.getLanguageURLs(webUrl);

        return names;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onPostExecute(List<String> result) {
        listener.onResponse(result);
    }
}
