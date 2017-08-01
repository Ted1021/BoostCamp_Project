package com.tedkim.smartschedule.regist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.tedkim.smartschedule.R;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;
    ImageButton mBack, mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        initView();
    }

    public void initView(){

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_save);
        mSave.setOnClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0,0);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.imageButton_back:

                finish();
                break;

            case R.id.imageButton_save:

                break;

        }
    }
}
