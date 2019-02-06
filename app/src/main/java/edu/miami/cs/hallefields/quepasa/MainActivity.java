package edu.miami.cs.hallefields.quepasa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//=============================================================================
public class MainActivity extends AppCompatActivity {

    //-----------------------------------------------------------------------------
    private WebView viewArea;
    //-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String contentUrl;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewArea = findViewById(R.id.content_view);
        //----Load zoomed out to fit
        viewArea.getSettings().setLoadWithOverviewMode(true);
        //----Make the viewport "normal", rather than the size of the webview
        viewArea.getSettings().setUseWideViewPort(false);
        viewArea.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url) {
                view.loadUrl(url);
                return(false);
            }
        });
        //----Turn on the zoom controls
        viewArea.getSettings().setBuiltInZoomControls(true);
        // load page
        contentUrl = getResources().getString(R.string.content_url);
        viewArea.loadUrl(contentUrl);
    }
    //-----------------------------------------------------------------------------
    public void myClickHandler(View view) {

    }
//-----------------------------------------------------------------------------
}
//=============================================================================
