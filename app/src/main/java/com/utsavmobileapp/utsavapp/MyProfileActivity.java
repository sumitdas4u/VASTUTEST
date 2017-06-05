package com.utsavmobileapp.utsavapp;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
    }

    public void actionClick(View v) {
        if(v.getId() == R.id.lyt_username){

        }else if(v.getId() == R.id.lyt_phone){

        }else {
            TextView textView = getChildTextView(v);
            String str = textView == null ? "-" : textView.getText().toString();
        }
    }

    public TextView getChildTextView(View v) {
        for (int index = 0; index < ((LinearLayout) v).getChildCount(); ++index) {
            View nextChild = ((LinearLayout) v).getChildAt(index);
            if(nextChild instanceof TextView){
                return (TextView) nextChild;
            }
        }
        return null;
    }
}
