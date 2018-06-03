package com.dynamicoders.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class AgreementActivity extends AppCompatActivity {

    private ArrayList<Integer> usedStickers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agreement_activity);
        Intent i = getIntent();
        usedStickers = i.getIntegerArrayListExtra("usedStickers");
        TextView pledges = findViewById(R.id.pledges);
        StringBuilder builder = new StringBuilder();
        for(Integer id : usedStickers)
            builder.append("\u2022 "+id.toString()+"\n");
        pledges.setText(builder.toString());
    }

    public void onClickYes(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",true);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void onClickNo(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",false);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
