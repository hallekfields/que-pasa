package edu.miami.cs.hallefields.quepasa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.util.GregorianCalendar;

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
                if (resultCode == Activity.RESULT_OK) {
                    selectedPhotoURI = returnedIntent.getData();
                    if (selectedPhotoURI == null) {
                        Log.i("INFO", "selectedPhotoURI is null");
                        finish();
                    }
                    try {
                        Bitmap selectedPicture = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(), selectedPhotoURI);
                        ((ImageView) findViewById(R.id.select_photo)).setImageBitmap(selectedPicture);
                    } catch (Exception e) {
                        Log.i("ERROR", "Could not get picture from " + selectedPhotoURI + " " + e.getMessage());
                        //----Should do something here
                    }
                }
                break;
            case ACTIVITY_SEND_EMAIL:
                if (resultCode == Activity.RESULT_OK) {
                    Intent nextActivity = new Intent();
                    nextActivity.setClassName("edu.miami.cs.hallefields.quepasa",
                            "edu.miami.cs.hallefields.quepasa.MainActivity");
                    startActivity(nextActivity);
                }
                break;
            default:
                break;
        }
    }
    //-----------------------------------------------------------------------------
    public void myClickListener(View view) {

        EditText titleView, descriptionView;
        DatePicker datePicker;
        TimePicker timePicker;
        String title, description;
        GregorianCalendar dateTime;

        String email = getResources().getString(R.string.admin_email);

        switch (view.getId()) {
            case R.id.select_photo:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,ACTIVITY_SELECT_PICTURE);
                break;
            case R.id.send:

                // get all text and date fields
                titleView = findViewById(R.id.post_title);
                descriptionView = findViewById(R.id.post_description);
                datePicker = findViewById(R.id.date_picker);
                timePicker = findViewById(R.id.time_picker);

                title = titleView.getText().toString().trim();
                description  = descriptionView.getText().toString().trim();
                dateTime = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(), datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                Intent nextIntent;
                Log.i("INFO", "RESULT_OK");
                nextIntent = new Intent(Intent.ACTION_SEND);
                nextIntent.setType("application/image");
                Log.i("INFO", "putting extras in email");

                //put admin email
                nextIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });

                //put subject line
                nextIntent.putExtra(Intent.EXTRA_SUBJECT, "New Post Request: " + title);

                //all content from user
                nextIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + title + "\n" +
                                                            "Description: " + description + "\n" +
                                                            "Date and Time: " + dateTime.getTime());

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
