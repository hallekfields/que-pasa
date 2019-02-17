package edu.miami.cs.hallefields.quepasa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//=============================================================================
public class MainActivity extends AppCompatActivity {

    //-----------------------------------------------------------------------------

    private static final int ACTIVITY_POST = 1;

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

        // load ad

    }
    //-----------------------------------------------------------------------------
    public void myClickListener(View view) {

        switch (view.getId()) {
            case R.id.post_button:
                Intent nextActivity = new Intent();
                nextActivity.setClassName("edu.miami.cs.hallefields.quepasa",
                        "edu.miami.cs.hallefields.quepasa.PostActivity");
                startActivityForResult(nextActivity, ACTIVITY_POST);
                break;
            default:
                break;
        }
    }
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
}
//=============================================================================
