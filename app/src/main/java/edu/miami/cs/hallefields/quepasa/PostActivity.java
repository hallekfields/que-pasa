package edu.miami.cs.hallefields.quepasa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.GregorianCalendar;

//=============================================================================
public class PostActivity extends AppCompatActivity {
    //-----------------------------------------------------------------------------
    private static final int ACTIVITY_SELECT_PICTURE = 1;
    private static final int ACTIVITY_SEND_EMAIL = 2;
    Uri selectedPhotoURI;

    private boolean dateTimeBool;
    //-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.i("INFO", "onCreate");
        setContentView(R.layout.activity_post);
        ((ImageView)findViewById(R.id.select_photo)).setImageResource(R.drawable.clickhere);

        Switch toggle = findViewById(R.id.datetime_switch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    dateTimeBool = true;
                    findViewById(R.id.start).setVisibility(View.VISIBLE);
                    findViewById(R.id.end).setVisibility(View.VISIBLE);

                } else {
                    // The toggle is disabled
                    dateTimeBool = false;
                    findViewById(R.id.start).setVisibility(View.GONE);
                    findViewById(R.id.end).setVisibility(View.GONE);
                }
            }
        });
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

        EditText titleView, descriptionView, locationView, urlView;
        DatePicker datePicker;
        TimePicker timePicker;
        String title, description, location, url;
        GregorianCalendar dateTime;

        String subject, body;

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
                locationView = findViewById(R.id.post_location);
                urlView = findViewById(R.id.post_url);

                title = titleView.getText().toString().trim();
                description  = descriptionView.getText().toString().trim();
                location = locationView.getText().toString().trim();
                url = urlView.getText().toString().trim();


                subject = "New Post Request: " + title;
                body = "Title: " + title + "\n" +
                        "Description: " + description + "\n" +
                        "Location: " + location + "\n" +
                        "URL: " + url + "\n" +
                        "Time Submitted: " + DateFormat.getInstance().format(System.currentTimeMillis()) + "\n";

                if (dateTimeBool) {
                    datePicker = findViewById(R.id.date_picker_start);
                    timePicker = findViewById(R.id.time_picker_start);

                    dateTime = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(), datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                    body += "Start: " + dateTime.getTime() + "\n";

                    datePicker = findViewById(R.id.date_picker_end);
                    timePicker = findViewById(R.id.time_picker_end);

                    dateTime = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(), datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                    body += "End: " + dateTime.getTime() + "\n";
                }

                if (selectedPhotoURI != null) {
                    //photo
                    Log.i("INFO", "selectedPhotoURI is not null");
                    new SendMail().execute(subject, body, getPath(getApplicationContext(), selectedPhotoURI));
                    break;
                }
                new SendMail().execute(subject, body, null);

                break;
            default:
                break;
        }
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        Log.i("INFO","Result is + " + result);
        return result;
    }

    /**
     *
     * Courtesy of http://www.edumobile.org/
     *             android/send-email-on-button-
     *             click-without-email-chooser/
     *
     * Downloaded: Feb 21, 2019
     * Modified by Halle Fields on February 21, 2019
     *
     */
    private class SendMail extends AsyncTask<String, Integer, Void> {

        private ProgressDialog progressDialog;
        private boolean sent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PostActivity.this,
                    "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(sent) {
                Toast.makeText(PostActivity.this,
                        "Post was sent successfully.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PostActivity.this,
                        "Post could not be sent. Try again!", Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();

            // return to homepage
            Intent nextActivity = new Intent();
            nextActivity.setClassName("edu.miami.cs.hallefields.quepasa",
                    "edu.miami.cs.hallefields.quepasa.MainActivity");
            startActivity(nextActivity);
        }

        protected Void doInBackground(String... params) {
            String user, pass;
            user = getResources().getString(R.string.admin_email);
            pass = getResources().getString(R.string.admin_password);
            Mail m = new Mail(user, pass, params[2]);

            String[] toArr = {user};
            m.setTo(toArr);
            m.setFrom(user);
            m.setSubject(params[0]);
            m.setBody(params[1]);

            try {
                sent = m.send();
            } catch(Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }
            return null;
        }
    }
//-----------------------------------------------------------------------------
}
//=============================================================================
