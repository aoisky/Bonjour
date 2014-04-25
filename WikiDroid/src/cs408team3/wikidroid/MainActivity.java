package cs408team3.wikidroid;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import cs408team3.wikidroid.blur.Blur;
import cs408team3.wikidroid.blur.BlurTask;
import cs408team3.wikidroid.languages.LanguageList;
import cs408team3.wikidroid.languages.Languages;
import cs408team3.wikidroid.languages.UrlList;
import cs408team3.wikidroid.listArticles.ListSaveArticles;
import cs408team3.wikidroid.search.SearchArticle;
import cs408team3.wikidroid.tab.TabListAdapter;
import cs408team3.wikidroid.tab.TabManager;

@SuppressWarnings(value = { "unused" })
public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String   TAG                    = "MainActivity";

    private static final int      ACTIONBAR_NORMAL_TITLE = 0x1;
    private static final int      ACTIONBAR_DRAWER_TITLE = 0x2;

    private final Context         mContext               = this;

    private TabManager            mTabManager;
    private WebViewClient         mWebViewClient;
    private WebChromeClient       mWebChromeClient;
    private Languages             mLanguages;

    private DrawerLayout          mDrawerLayout;

    private TabListAdapter        mDrawerListAdapter;
    private ListView              mDrawerList;
    private ImageView             mBlurImage;
    private FrameLayout           mContentFrame;
    private WebView               mWebPage;
    private MenuItem              mSearchMenuItem;
    private ProgressBar           mWebProgressBar;
    private Toast                 mToast;

    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence          mDrawerTitle;
    private CharSequence          mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebViewClient = new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mTabManager.isForeground(view)) {
                    setTitle(mTabManager.getTitle(view), ACTIONBAR_NORMAL_TITLE);
                }
                mDrawerListAdapter.notifyDataSetChanged();
            }
        };
        mWebChromeClient = new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (mTabManager.isForeground(view)) {
                    if (progress < 100) {
                        startWebProgressBar();
                    }

                    if (progress == 100) {
                        stopWebProgressBar();
                    }
                } else {
                    stopWebProgressBar();
                }
            }
        };

        mTabManager = new TabManager(this, mWebViewClient, mWebChromeClient);
        if (mTabManager.size() == 0) {
            mTabManager.newTab();
            mTabManager.setForeground(0);
        }
        mLanguages = new Languages();

        mDrawerTitle = getTitle();
        mWebPage = mTabManager.displayTab(0);
        setTitle(mTabManager.getTitle(mWebPage), ACTIONBAR_NORMAL_TITLE);

        mWebProgressBar = (ProgressBar) findViewById(R.id.content_progress);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mBlurImage = (ImageView) findViewById(R.id.blur_image);
        mContentFrame = (FrameLayout) findViewById(R.id.content_frame);

        mContentFrame.addView(mWebPage, 0);

        mDrawerToggle = new WikiDroidActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerListAdapter = new TabListAdapter(this, R.layout.drawer_list_item, R.id.drawer_list_item_text, mTabManager);
        mDrawerListAdapter.setOnTabRemoveListener(new TabListAdapter.OnTabRemoveListener() {

            @Override
            public void onTabRemove(int position) {
                boolean removed = mTabManager.removeTab(position - 1);

                if (removed) {
                    int displayPosition = position - 1 >= 0 ? position - 1 : 0;
                    displayWebView(displayPosition);
                    mDrawerListAdapter.notifyDataSetChanged();
                } else {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(mContext, R.string.error_remove_tab_failed, Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });
        mDrawerList.setAdapter(mDrawerListAdapter);

        mDrawerList.setOnItemClickListener(this);

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebPage.canGoBack()) {
            mWebPage.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        menu.findItem(R.id.action_add_tab).setVisible(drawerOpen);
        menu.findItem(R.id.search).setVisible(!drawerOpen);
        menu.findItem(R.id.saveArticle).setVisible(!drawerOpen);
        menu.findItem(R.id.languages).setVisible(!drawerOpen);
        menu.findItem(R.id.action_share_article).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                boolean haveNet = true;

                if (haveNet == false) {
                    Toast t = Toast.makeText(mContext, "Sorry, No internet connection", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 5, 5);
                    t.show();
                    return false;
                } else {
                    if (false) {
                        Toast t = Toast.makeText(mContext, "Sorry, invalid input. Try again", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 5, 5);
                        t.show();
                        return false;
                    }

                    SearchArticle search = new SearchArticle(mContext, mWebPage);
                    search.execute(query);

                    return true;
                }

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

        case R.id.action_add_tab:
            if (!mTabManager.newTab()) {
                if (mToast != null) {
                    mToast.cancel();

                }

                mToast.show();
            } else {
                mDrawerListAdapter.notifyDataSetChanged();
            }

            return true;

        case R.id.languages:
            showLanguagesDialog();
            return true;

        case R.id.saveArticle:
            if (mTabManager.size() == 0 || mTabManager == null)
                return false;
            String title = mWebPage.getTitle();

            return true;

        case R.id.action_share_article:
            Intent shareIntent = new Intent();
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wikipedia article - " + Utils.trimWikipediaTitle(mWebPage.getTitle()));
            shareIntent.setType("text/plain");

            return true;

        case R.id.action_show_saved_articles:
            Intent intent = new Intent(this, ListSaveArticles.class);
            startActivity(intent);
            return true;

        case R.id.action_settings:
            Log.e(TAG, "I just died");

            finish();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        displayWebView(position);

        if (parent.getSelectedItemPosition() != position) {
            stopWebProgressBar();
        }

        mDrawerLayout.closeDrawers();
    }

    private void showLanguagesDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.dialog_language_loading));
        progressDialog.show();

        LanguageList langList = new LanguageList(mLanguages, mWebPage.getUrl(), new LanguageList.Listener() {

            @Override
            public void onResponse(List<String> languageOptions) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.action_languages);

                if (languageOptions.size() > 0) {
                    ListAdapter stringListAdapter = new ArrayAdapter<String>(mContext, R.layout.languages_list_item, languageOptions);

                    builder.setSingleChoiceItems(stringListAdapter, languageOptions.size() - 1, null)
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    final int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                                    if (selectedPosition == -1 || selectedPosition == 6) {
                                    }

                                    else {
                                        UrlList urlList = new UrlList(mLanguages, mWebPage.getUrl(), new UrlList.Listener() {

                                            @Override
                                            public void onResponse(List<String> urlOptions) {
                                                mWebPage.loadUrl(urlOptions.get(selectedPosition));
                                            }
                                        });
                                        urlList.execute();
                                    }
                                }
                            });
                } else {
                    builder.setNeutralButton(R.string.dialog_ok, null);
                }

                progressDialog.dismiss();
                builder.create().show();
            }
        });
        langList.execute();
    }

    private class WikiDroidActionBarDrawerToggle extends ActionBarDrawerToggle implements BlurTask.Listener {

        private Bitmap scaled;

        public WikiDroidActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
            if (slideOffset > 0.0f) {
                setBlurAlpha(slideOffset);
            } else {
                clearBlurImage();
            }
        }

        @Override
        public void onDrawerClosed(final View view) {
            super.onDrawerClosed(view);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    toggleTitle(ACTIONBAR_NORMAL_TITLE);
                    clearBlurImage();
                    invalidateOptionsMenu();
                }

            }, 1000);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    toggleTitle(ACTIONBAR_DRAWER_TITLE);
                    invalidateOptionsMenu();
                }
            }, 1000);
        }

        private void setBlurAlpha(float slideOffset) {
            if (mBlurImage.getVisibility() != View.VISIBLE) {
                setBlurImage();
            }

            mBlurImage.setAlpha(slideOffset);
        }

        private void setBlurImage() {
            mBlurImage.setImageBitmap(null);
            mBlurImage.setVisibility(View.VISIBLE);

            scaled = Utils.drawViewToBitmap(scaled, mContentFrame, mContentFrame.getWidth(), mContentFrame.getHeight(), Blur.DEFAULT_DOWNSAMPLING);
            new BlurTask(mContentFrame.getContext(), null, scaled);

            mBlurImage.setImageBitmap(scaled);
        }

        private void clearBlurImage() {
            mBlurImage.setVisibility(View.GONE);
            mBlurImage.setImageBitmap(null);
        }

        @Override
        public void onBlurOperationFinished() {
            mBlurImage.invalidate();
        }

    }

    private void displayWebView(int position) {
        mContentFrame.removeView(mWebPage);
        mWebPage = mTabManager.displayTab(position);
        mContentFrame.addView(mWebPage, 0);
        setTitle(mTabManager.getTitle(mWebPage), ACTIONBAR_NORMAL_TITLE);
    }

    private void setTitle(String title, int status) {
        switch (status) {
        case ACTIONBAR_NORMAL_TITLE:
            if (title != null)
                mTitle = title;
            getActionBar().setTitle(mTitle);
            return;
        case ACTIONBAR_DRAWER_TITLE:
            if (title != null)
                mDrawerTitle = title;
            getActionBar().setTitle(mDrawerTitle);
            return;
        }
    }

    private void toggleTitle(int status) {
        setTitle(null, status);
    }

    private void startWebProgressBar() {
        if (mWebProgressBar.getVisibility() != View.VISIBLE) {
            mWebProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void stopWebProgressBar() {
        if (mWebProgressBar.getVisibility() != View.GONE) {
            mWebProgressBar.setVisibility(View.GONE);
        }
    }

}
