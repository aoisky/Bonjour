package cs408team3.wikidroid.listArticles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hyperionics.war_test.WebArchiveReader;

import cs408team3.wikidroid.R;
import cs408team3.wikidroid.Utils;

public class LoadSavedStuffs extends Activity {

    private WebView       webpage;
    private WebViewClient mWebViewClient;
    private final String  TAG = "LoadStuffs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_saved_stuffs);

        webpage = (WebView) findViewById(R.id.webView1);
        webpage.getSettings().setJavaScriptEnabled(true);

        mWebViewClient = new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                getActionBar().setTitle(Utils.trimWikipediaTitle(view.getTitle()));

            }
        };

        String link = getIntent().getStringExtra("url");
        if (link != null) {
            webpage.loadUrl(link);
        }
        String filename = getIntent().getStringExtra("filename");
        if (filename != null) {
            loadSavedWebPage(webpage, filename);
        }

        this.getActionBar().setTitle(Utils.trimWikipediaTitle(webpage.getTitle()));
    }

    private void loadSavedWebPage(WebView webView, String fileName) {
        File sdCard = Environment.getExternalStorageDirectory();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            File dir = new File(sdCard.getAbsolutePath() + "/WikiDroid/" + fileName);
            webView.loadUrl("file:///" + dir.toString());
        }
        else {
            File dir = new File(sdCard.getAbsolutePath() + "/WikiDroid/" + fileName);
            try {
                FileInputStream is = new FileInputStream(dir);
                WebArchiveReader wr = new WebArchiveReader() {

                    @Override
                    public void onFinished(WebView v) {
                    }
                };

                if (wr.readWebArchive(is)) {
                    wr.loadToWebView(webView);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.load_saved_stuffs, menu);
        return true;
    }

}
