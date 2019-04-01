package edu.miami.cs.hallefields.quepasa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//=============================================================================
public class MainActivity extends AppCompatActivity {

    //-----------------------------------------------------------------------------

    private static final int ACTIVITY_POST = 1;

    private WebView viewArea;

    private String ad;
    private String adTitle;
    private Bitmap adBitmap;

    private boolean complete = false;
    private boolean internet = true;

    SwipeRefreshLayout swipeRefresh;
    //-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String contentUrl;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide ad until loaded
        findViewById(R.id.ad_view).setVisibility(View.GONE);

        // check if connected to internet
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(!(conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())) {
            Log.i("INFO" , "Not connected to the internet");
            ConnectionDialog dialog = new ConnectionDialog();
            dialog.show(getSupportFragmentManager(), "ConnectionDialog");
        } else {

            // set-up swipe-refresh-layout
            swipeRefresh = findViewById(R.id.refresh_view);

            // set-up webview
            viewArea = findViewById(R.id.content_view);
            //----Load zoomed out to fit
            viewArea.getSettings().setLoadWithOverviewMode(true);
            //----Make the viewport "normal", rather than the size of the webview
            viewArea.getSettings().setUseWideViewPort(true);
            //----Enable javascript for site
            viewArea.getSettings().setJavaScriptEnabled(true);

            viewArea.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!url.contains("https://monte-cristi.000webhostapp.com")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        viewArea.loadUrl(url);
                        // load ad
                        Thread thread = new Thread(new Runnable(){
                            public void run() {
                                try {
                                    ad = getRandomAd();
                                    getAdData();
                                    complete = true;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        while (!complete) { }

                        ((TextView) findViewById(R.id.ad_text)).setText(adTitle);
                        try {
                            ImageView imageView = findViewById(R.id.ad_image);
                            imageView.setImageBitmap(adBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        findViewById(R.id.ad_view).setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });

            //----Turn on the zoom controls
            viewArea.getSettings().setSupportZoom(true);
            viewArea.getSettings().setBuiltInZoomControls(true);
            viewArea.getSettings().setDisplayZoomControls(false);

            // load page
            contentUrl = getResources().getString(R.string.content_url);
            viewArea.loadUrl(contentUrl);

            // load ad
            Thread thread = new Thread(new Runnable(){
                public void run() {
                    try {
                        ad = getRandomAd();
                        getAdData();
                        complete = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            while (!complete) {
            }

            ((TextView) findViewById(R.id.ad_text)).setText(adTitle);
            try {
                ImageView imageView = findViewById(R.id.ad_image);
                imageView.setImageBitmap(adBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            findViewById(R.id.ad_view).setVisibility(View.VISIBLE);

            swipeRefresh.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            viewArea.reload();
                        }
                    }
            );
        }
    }
    //-----------------------------------------------------------------------------
    public void myClickListener(View view) {

        switch (view.getId()) {
            case R.id.post_button:
                /*
                Intent nextActivity = new Intent();
                nextActivity.setClassName("edu.miami.cs.hallefields.quepasa",
                        "edu.miami.cs.hallefields.quepasa.PostActivity");
                startActivityForResult(nextActivity, ACTIVITY_POST);
                */
                // load page
                String contentUrl = getResources().getString(R.string.form_url);
                viewArea.loadUrl(contentUrl);
                break;
            case R.id.ad_view:
                viewArea.loadUrl(ad);
                break;
            default:
                break;
        }
    }
    //-----------------------------------------------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (viewArea.canGoBack()) {
                        viewArea.goBack();
                    } else {
                        //finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    //-----------------------------------------------------------------------------
    public String getRandomAd() {
        List<String> urlList = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(getResources().getString(R.string.site_map)).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements urls = doc.getElementsByTag("loc");

        for (Element url : urls) {
            if (url.toString().contains("advertisements"))
                urlList.add(url.text());
        }

        int randIndex = (int)(Math.random() * ((urlList.size())));
        return urlList.get(randIndex);
    }
    //-----------------------------------------------------------------------------
    public void getAdData() {
        Document doc = null;
        try {
            doc = Jsoup.connect(ad).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements title = doc.getElementsByClass("entry-title");
        Elements image = doc.getElementsByClass("featured-img");
        image = image.get(0).children();

        adTitle = title.get(0).text();
        String adImage = image.get(0).attr("src");
        try {
            Log.i("INFO", adImage);
            adBitmap = BitmapFactory.decodeStream((InputStream)new URL(adImage).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
}
//=============================================================================
