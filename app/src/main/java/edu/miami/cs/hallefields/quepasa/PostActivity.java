package edu.miami.cs.hallefields.quepasa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

//=============================================================================
public class PostActivity extends AppCompatActivity {
    //-----------------------------------------------------------------------------
    private static final int ACTIVITY_SELECT_PICTURE = 1;
    private static final int ACTIVITY_SEND_EMAIL = 2;
    Uri selectedPhotoURI;
    //-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.i("INFO", "onCreate");
        setContentView(R.layout.activity_post);
        ((ImageView)findViewById(R.id.select_photo)).setImageResource(R.drawable.clickhere);
    }
    //-----------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode,int resultCode,
                                 Intent returnedIntent) {
        super.onActivityResult(requestCode,resultCode,returnedIntent);

        switch (requestCode) {
            case ACTIVITY_SELECT_PICTURE:
                selectedPhotoURI = returnedIntent.getData();
                if (selectedPhotoURI == null) {
                    Log.i("INFO", "selectedPhotoURI is null");
                    finish();
                }
                break;
            default:
                break;
        }
    }
    //-----------------------------------------------------------------------------
    public void myClickListener(View view) {

        String email = getResources().getString(R.string.admin_email);
        switch (view.getId()) {
            case R.id.select_photo:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,ACTIVITY_SELECT_PICTURE);
                break;
            case R.id.send:
                Intent nextIntent;
                Log.i("INFO", "RESULT_OK");
                nextIntent = new Intent(Intent.ACTION_SEND);
                nextIntent.setType("application/image");
                Log.i("INFO", "putting extras in email");
                //put admin email
                nextIntent.putExtra(Intent.EXTRA_EMAIL, email);

                //put subject line
                nextIntent.putExtra(Intent.EXTRA_SUBJECT, "New Post Request");

                //all content from user
                nextIntent.putExtra(Intent.EXTRA_TEXT, "");
                nextIntent.putExtra(Intent.EXTRA_TEXT, "");
                nextIntent.putExtra(Intent.EXTRA_TEXT, "");
                nextIntent.putExtra(Intent.EXTRA_TEXT, "");

                if (selectedPhotoURI != null) {
                    //photo
                    nextIntent.putExtra(Intent.EXTRA_STREAM, selectedPhotoURI);
                }
                // send email
                Log.i("INFO", "starting email activity");
                startActivityForResult(nextIntent, ACTIVITY_SEND_EMAIL);
                break;
            default:
                break;
        }
    }
//-----------------------------------------------------------------------------
}
//=============================================================================
