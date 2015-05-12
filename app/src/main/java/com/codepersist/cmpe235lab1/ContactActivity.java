package com.codepersist.cmpe235lab1;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class ContactActivity extends ActionBarActivity {

    EditText mName;
    TextView mEmail;
    EditText mSubject;
    EditText mBody;
    Button mSubmit;
    //CheckBox mSendReponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mName =  (EditText) findViewById(R.id.contact_name);
        mEmail = (TextView) findViewById(R.id.contact_send_to);
        mSubject = (EditText) findViewById(R.id.contact_subject);
        mBody = (EditText) findViewById(R.id.contact_body);
        //mSendReponse = (But) findViewById(R.id.contact_check_box);
        mSubmit = (Button) findViewById(R.id.contact_submit);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendFeedback(View view) {

        String mNameText = mName.getText().toString();
        String mSubjectText = mSubject.getText().toString();
        String mBodyText = mBody.getText().toString();
        String[] mToEmail = new String[1];
        mToEmail[0] = mEmail.getText().toString();

        Log.d("omg android", "Sending an e-mail to " + mToEmail);
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, mToEmail);
        email.putExtra(Intent.EXTRA_SUBJECT, mSubjectText);
        email.putExtra(Intent.EXTRA_TEXT, mNameText +" wrote: \n\n"+mBodyText);
        email.setType("*/*");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));


    }
}
